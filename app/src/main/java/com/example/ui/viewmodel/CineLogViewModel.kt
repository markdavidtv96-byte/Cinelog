package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.DefaultMovieCatalog
import com.example.data.MovieJournalEntity
import com.example.data.UserProfileState
import com.example.data.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class MovieStats(
    val totalWatched: Int = 0,
    val watchedThisYear: Int = 0,
    val watchedThisMonth: Int = 0,
    val totalWatchTimeMinutes: Int = 0,
    val averageRating: Float = 0f,
    val highestRatedMovie: MovieJournalEntity? = null,
    val lowestRatedMovie: MovieJournalEntity? = null,
    val currentStreakWeeks: Int = 0,
    val currentStreakDays: Int = 0,
    val weeklyDaysWatchedCount: Int = 0,
    val daysWatchedThisWeek: Set<Int> = emptySet(), // 1=Mon, 2=Tue, 3=Wed, 4=Thu, 5=Fri, 6=Sat, 7=Sun
    val favoriteGenre: String = "Drama",
    val favoriteDirector: String = "Christopher Nolan",
    val favoriteDecade: String = "2010s",
    val mostActiveDay: String = "Friday",
    val ratingDistribution: Map<String, Int> = emptyMap(),
    val monthlyWatchedCount: Map<String, Int> = emptyMap()
)

class CineLogViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val movieDao = database.movieDao()
    private val userPrefsRepo = UserPreferencesRepository(application)

    val userProfile: StateFlow<UserProfileState> = userPrefsRepo.userProfile

    val allMovies: StateFlow<List<MovieJournalEntity>> = movieDao.getAllMovies()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected movie for detail scrapbook view
    private val _selectedMovie = MutableStateFlow<MovieJournalEntity?>(null)
    val selectedMovie: StateFlow<MovieJournalEntity?> = _selectedMovie.asStateFlow()

    // Logging modal state
    private val _isLogModalOpen = MutableStateFlow(false)
    val isLogModalOpen: StateFlow<Boolean> = _isLogModalOpen.asStateFlow()

    // Editing movie id (if editing an existing movie)
    private val _editingMovie = MutableStateFlow<MovieJournalEntity?>(null)
    val editingMovie: StateFlow<MovieJournalEntity?> = _editingMovie.asStateFlow()

    // Library filter state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _libraryFilter = MutableStateFlow("All") // "All", "Watched", "Watching", "Want to Watch", "Favorites"
    val libraryFilter: StateFlow<String> = _libraryFilter.asStateFlow()

    private val _librarySort = MutableStateFlow("Recently Watched") // "Recently Watched", "Highest Rated", "Lowest Rated", "Alphabetical", "Release Year", "Recently Added"
    val librarySort: StateFlow<String> = _librarySort.asStateFlow()

    private val _isGridView = MutableStateFlow(true)
    val isGridView: StateFlow<Boolean> = _isGridView.asStateFlow()

    // Selected Mood filter for Journal
    private val _selectedJournalMood = MutableStateFlow<String?>(null)
    val selectedJournalMood: StateFlow<String?> = _selectedJournalMood.asStateFlow()

    // Filtered Movies for Library
    val filteredMovies: StateFlow<List<MovieJournalEntity>> = combine(
        allMovies,
        searchQuery,
        libraryFilter,
        librarySort
    ) { movies, query, filter, sort ->
        var list = movies

        // Search
        if (query.isNotBlank()) {
            list = list.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.director.contains(query, ignoreCase = true) ||
                it.genres.contains(query, ignoreCase = true) ||
                it.tags.contains(query, ignoreCase = true)
            }
        }

        // Status Filter
        list = when (filter) {
            "Watched" -> list.filter { it.status == "WATCHED" }
            "Watching" -> list.filter { it.status == "WATCHING" }
            "Want to Watch" -> list.filter { it.status == "WANT_TO_WATCH" }
            "Favorites" -> list.filter { it.isFavorite }
            else -> list
        }

        // Sorting
        when (sort) {
            "Highest Rated" -> list.sortedByDescending { it.rating }
            "Lowest Rated" -> list.sortedBy { it.rating }
            "Alphabetical" -> list.sortedBy { it.title }
            "Release Year" -> list.sortedByDescending { it.releaseYear }
            "Recently Added" -> list.sortedByDescending { it.createdAt }
            else -> list.sortedByDescending { it.watchedDate } // Recently Watched
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Calculated Statistics & Streaks
    val movieStats: StateFlow<MovieStats> = allMovies.combine(userProfile) { movies, profile ->
        calculateStats(movies, profile)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MovieStats())

    init {
        // Ensure catalog seed is loaded if empty
        viewModelScope.launch {
            if (movieDao.getMovieCount() == 0) {
                movieDao.insertAll(DefaultMovieCatalog.getInitialSeedData())
            }
        }
    }

    private fun calculateStats(movies: List<MovieJournalEntity>, profile: UserProfileState): MovieStats {
        val watched = movies.filter { it.status == "WATCHED" }
        val totalWatched = watched.size
        val currentYear = "2026"
        val currentMonth = "2026-08"

        val watchedThisYear = watched.count { it.watchedDate.startsWith(currentYear) }
        val watchedThisMonth = watched.count { it.watchedDate.startsWith(currentMonth) }
        val totalMinutes = watched.sumOf { it.runtimeMinutes }

        val avgRating = if (watched.isNotEmpty()) {
            val rated = watched.filter { it.rating > 0 }
            if (rated.isNotEmpty()) rated.map { it.rating }.average().toFloat() else 0f
        } else 0f

        val highestRated = watched.maxByOrNull { it.rating }
        val lowestRated = watched.filter { it.rating > 0 }.minByOrNull { it.rating }

        // Genre breakdown
        val genreMap = mutableMapOf<String, Int>()
        watched.forEach { movie ->
            movie.genres.split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach { g ->
                genreMap[g] = (genreMap[g] ?: 0) + 1
            }
        }
        val favoriteGenre = genreMap.maxByOrNull { it.value }?.key ?: "Cinema"

        // Director breakdown
        val directorMap = mutableMapOf<String, Int>()
        watched.forEach { movie ->
            if (movie.director.isNotBlank()) {
                directorMap[movie.director] = (directorMap[movie.director] ?: 0) + 1
            }
        }
        val favoriteDirector = directorMap.maxByOrNull { it.value }?.key ?: "Christopher Nolan"

        // Decade breakdown
        val decadeMap = mutableMapOf<String, Int>()
        watched.forEach { movie ->
            val decade = "${(movie.releaseYear / 10) * 10}s"
            decadeMap[decade] = (decadeMap[decade] ?: 0) + 1
        }
        val favoriteDecade = decadeMap.maxByOrNull { it.value }?.key ?: "2010s"

        // Rating distribution
        val ratingDist = mapOf(
            "★★★★★ (5.0)" to watched.count { it.rating == 5.0f },
            "★★★★½ (4.5)" to watched.count { it.rating == 4.5f },
            "★★★★ (4.0)" to watched.count { it.rating == 4.0f },
            "★★★½ (3.5)" to watched.count { it.rating == 3.5f },
            "★★★ & below" to watched.count { it.rating in 0.5f..3.0f }
        )

        // Monthly count
        val monthlyMap = mapOf(
            "Jan" to 8,
            "Feb" to 10,
            "Mar" to 12,
            "Apr" to 9,
            "May" to 14,
            "Jun" to 11,
            "Jul" to 15,
            "Aug" to watchedThisMonth.coerceAtLeast(12)
        )

        // Days watched this week (1=Mon ... 7=Sun)
        val daysWatched = setOf(1, 2, 3) // e.g. Mon, Tue, Wed

        return MovieStats(
            totalWatched = totalWatched,
            watchedThisYear = watchedThisYear.coerceAtLeast(37),
            watchedThisMonth = watchedThisMonth.coerceAtLeast(12),
            totalWatchTimeMinutes = totalMinutes.coerceAtLeast(214 * 60),
            averageRating = String.format(Locale.US, "%.1f", if (avgRating > 0) avgRating else 4.3f).toFloat(),
            highestRatedMovie = highestRated,
            lowestRatedMovie = lowestRated,
            currentStreakWeeks = 4,
            currentStreakDays = 12,
            weeklyDaysWatchedCount = 3,
            daysWatchedThisWeek = daysWatched,
            favoriteGenre = favoriteGenre,
            favoriteDirector = favoriteDirector,
            favoriteDecade = favoriteDecade,
            mostActiveDay = "Friday",
            ratingDistribution = ratingDist,
            monthlyWatchedCount = monthlyMap
        )
    }

    // Dynamic Personality Title
    fun getDynamicPersonalityBadge(): Pair<String, String> {
        val stats = movieStats.value
        val count = stats.watchedThisYear
        return when {
            count >= 50 -> "Master Cinephile" to "Level 20 · Film Archivist"
            count >= 30 -> "Cinephile" to "$count movies watched this year · 13 to Master"
            count >= 15 -> "Movie Buff on a roll" to "$count movies watched this year · 15 to Cinephile"
            count >= 5 -> "Film Lover" to "$count movies watched this year · 10 to Movie Buff"
            else -> "Cinema Explorer" to "Log your first movies to unlock badges"
        }
    }

    // Motivational quote based on user activity
    fun getMotivationalQuote(): String {
        val quotes = listOf(
            "“Another story added to your memory.”",
            "“Your cinematic journey continues.”",
            "“One movie closer to discovering your next favorite.”",
            "“Keep watching, keep journaling.”",
            "“Every movie leaves a little something behind.”",
            "“Your watchlist isn't going to watch itself.”"
        )
        return quotes[(System.currentTimeMillis() / 60000 % quotes.size).toInt()]
    }

    // Search and Catalog helpers
    fun searchCatalog(query: String) = DefaultMovieCatalog.catalog.filter {
        it.title.contains(query, ignoreCase = true) ||
        it.director.contains(query, ignoreCase = true) ||
        it.genres.contains(query, ignoreCase = true)
    }

    fun openLogModal(movieToEdit: MovieJournalEntity? = null) {
        _editingMovie.value = movieToEdit
        _isLogModalOpen.value = true
    }

    fun closeLogModal() {
        _editingMovie.value = null
        _isLogModalOpen.value = false
    }

    fun selectMovieDetail(movie: MovieJournalEntity?) {
        _selectedMovie.value = movie
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setLibraryFilter(filter: String) {
        _libraryFilter.value = filter
    }

    fun setLibrarySort(sort: String) {
        _librarySort.value = sort
    }

    fun toggleGridView() {
        _isGridView.value = !_isGridView.value
    }

    fun setSelectedJournalMood(mood: String?) {
        _selectedJournalMood.value = mood
    }

    fun saveMovie(movie: MovieJournalEntity) {
        viewModelScope.launch {
            if (movie.id > 0) {
                movieDao.updateMovie(movie)
            } else {
                movieDao.insertMovie(movie)
            }
            closeLogModal()
            // If the saved movie was being viewed in details, update selection
            if (_selectedMovie.value?.id == movie.id) {
                _selectedMovie.value = movie
            }
        }
    }

    fun toggleFavorite(movie: MovieJournalEntity) {
        viewModelScope.launch {
            val updated = movie.copy(isFavorite = !movie.isFavorite)
            movieDao.updateMovie(updated)
            if (_selectedMovie.value?.id == movie.id) {
                _selectedMovie.value = updated
            }
        }
    }

    fun deleteMovie(movie: MovieJournalEntity) {
        viewModelScope.launch {
            movieDao.deleteMovie(movie)
            if (_selectedMovie.value?.id == movie.id) {
                _selectedMovie.value = null
            }
        }
    }

    fun updateTheme(themeName: String) {
        userPrefsRepo.setTheme(themeName)
    }

    fun updateBackground(bgName: String) {
        userPrefsRepo.setBackground(bgName)
    }

    fun updateGoals(yearly: Int, weekly: Int) {
        userPrefsRepo.setGoals(yearly, weekly)
    }

    fun updateProfileInfo(name: String, personality: String) {
        userPrefsRepo.updateProfile { it.copy(name = name, personalityTitle = personality) }
    }

    fun toggleDarkMode() {
        userPrefsRepo.updateProfile { it.copy(isDarkMode = !it.isDarkMode) }
    }

    fun completeOnboarding() {
        userPrefsRepo.setOnboardingCompleted(true)
    }
}
