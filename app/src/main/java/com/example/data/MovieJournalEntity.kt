package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieJournalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val posterUrl: String = "",
    val backdropUrl: String = "",
    val releaseYear: Int = 2024,
    val director: String = "",
    val genres: String = "", // e.g. "Sci-Fi, Drama"
    val runtimeMinutes: Int = 120,
    val country: String = "USA",
    val language: String = "English",
    val overview: String = "",
    val status: String = "WATCHED", // "WATCHED", "WATCHING", "WANT_TO_WATCH"
    val watchedDate: String = "2026-08-28", // YYYY-MM-DD
    val rating: Float = 5.0f, // 0.0 to 5.0 (supports half stars)
    val isFavorite: Boolean = false,
    val mood: String = "Thought-provoking", // "Loved it", "Comfort", "Emotional", "Thought-provoking", "Romantic", "Exciting", "Feel-good", "Scary", "Disappointed"
    val watchLocation: String = "Cinema", // "Home", "Cinema", "With Friends", "With Family", "Other"
    val wouldRewatch: String = "Yes", // "Yes", "Maybe", "No"
    val journalEntry: String = "",
    val favoriteMoment: String = "",
    val favoriteScene: String = "",
    val favoriteCharacter: String = "",
    val favoriteQuote: String = "",
    val likedNotes: String = "",
    val dislikedNotes: String = "",
    val wouldRecommend: Boolean = true,
    val tags: String = "", // "#cinema, #emotional"
    val hasSpoiler: Boolean = false,
    val favoriteCategory: String = "All-Time Favorites", // "All-Time Favorites", "Comfort Movies", "Best Stories", "Beautiful Cinematography", "Childhood Favorites", "Rewatch Forever"
    val createdAt: Long = System.currentTimeMillis()
)
