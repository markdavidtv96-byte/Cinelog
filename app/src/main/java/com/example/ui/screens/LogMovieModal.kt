package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.DefaultMovieCatalog
import com.example.data.MovieJournalEntity
import com.example.ui.components.MoviePosterImage
import com.example.ui.components.StarRatingBar
import com.example.ui.viewmodel.CineLogViewModel

@Composable
fun LogMovieModal(
    viewModel: CineLogViewModel,
    movieToEdit: MovieJournalEntity? = null,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(movieToEdit?.title ?: "") }
    var releaseYear by remember { mutableStateOf(movieToEdit?.releaseYear?.toString() ?: "2024") }
    var director by remember { mutableStateOf(movieToEdit?.director ?: "") }
    var genres by remember { mutableStateOf(movieToEdit?.genres ?: "Drama, Sci-Fi") }
    var runtimeMinutes by remember { mutableStateOf(movieToEdit?.runtimeMinutes?.toString() ?: "120") }
    var posterUrl by remember { mutableStateOf(movieToEdit?.posterUrl ?: "") }
    var backdropUrl by remember { mutableStateOf(movieToEdit?.backdropUrl ?: "") }
    var rating by remember { mutableFloatStateOf(movieToEdit?.rating ?: 5.0f) }
    var watchedDate by remember { mutableStateOf(movieToEdit?.watchedDate ?: "2026-08-28") }
    var watchLocation by remember { mutableStateOf(movieToEdit?.watchLocation ?: "Cinema") }
    var mood by remember { mutableStateOf(movieToEdit?.mood ?: "Thought-provoking") }
    var wouldRewatch by remember { mutableStateOf(movieToEdit?.wouldRewatch ?: "Yes") }
    var isFavorite by remember { mutableStateOf(movieToEdit?.isFavorite ?: false) }
    var favoriteCategory by remember { mutableStateOf(movieToEdit?.favoriteCategory ?: "All-Time Favorites") }
    var journalEntry by remember { mutableStateOf(movieToEdit?.journalEntry ?: "") }
    var favoriteMoment by remember { mutableStateOf(movieToEdit?.favoriteMoment ?: "") }
    var favoriteScene by remember { mutableStateOf(movieToEdit?.favoriteScene ?: "") }
    var favoriteCharacter by remember { mutableStateOf(movieToEdit?.favoriteCharacter ?: "") }
    var favoriteQuote by remember { mutableStateOf(movieToEdit?.favoriteQuote ?: "") }
    var tags by remember { mutableStateOf(movieToEdit?.tags ?: "#cinema, #emotional") }
    var hasSpoiler by remember { mutableStateOf(movieToEdit?.hasSpoiler ?: false) }
    var status by remember { mutableStateOf(movieToEdit?.status ?: "WATCHED") }

    var catalogSearch by remember { mutableStateOf("") }
    val searchResults = if (catalogSearch.isNotBlank()) viewModel.searchCatalog(catalogSearch) else emptyList()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp)
                .testTag("log_movie_dialog"),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (movieToEdit != null) "Edit Movie Journal" else "Log a Movie",
                            fontSize = 24.sp,
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Record your personal cinematic memory",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Form Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Movie Search / Catalog Autofill
                    if (movieToEdit == null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "SEARCH CATALOG (AUTOFILL)",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.0.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                OutlinedTextField(
                                    value = catalogSearch,
                                    onValueChange = { catalogSearch = it },
                                    placeholder = { Text("Search Interstellar, Dune, Grand Budapest...", fontSize = 12.sp) },
                                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    singleLine = true
                                )

                                if (searchResults.isNotEmpty()) {
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        searchResults.take(3).forEach { item ->
                                            Surface(
                                                shape = RoundedCornerShape(12.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        title = item.title
                                                        releaseYear = item.releaseYear.toString()
                                                        director = item.director
                                                        genres = item.genres
                                                        runtimeMinutes = item.runtimeMinutes.toString()
                                                        posterUrl = item.posterUrl
                                                        backdropUrl = item.backdropUrl
                                                        mood = item.suggestedMood
                                                        favoriteQuote = item.sampleQuote
                                                        favoriteScene = item.sampleFavoriteScene
                                                        journalEntry = item.sampleReview
                                                        favoriteMoment = item.sampleReview.take(40) + "..."
                                                        catalogSearch = ""
                                                    }
                                                    .padding(vertical = 2.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(10.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                    MoviePosterImage(
                                                        posterUrl = item.posterUrl,
                                                        title = item.title,
                                                        modifier = Modifier.size(36.dp, 48.dp),
                                                        cornerRadius = 8.dp
                                                    )
                                                    Column {
                                                        Text(item.title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                        Text("${item.releaseYear} · ${item.director}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Movie Info Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "MOVIE DETAILS",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.0.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text("Movie Title *") },
                                modifier = Modifier.fillMaxWidth().testTag("log_movie_title_input"),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = releaseYear,
                                    onValueChange = { releaseYear = it },
                                    label = { Text("Year") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = director,
                                    onValueChange = { director = it },
                                    label = { Text("Director") },
                                    modifier = Modifier.weight(1.5f),
                                    shape = RoundedCornerShape(14.dp),
                                    singleLine = true
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = genres,
                                    onValueChange = { genres = it },
                                    label = { Text("Genres") },
                                    modifier = Modifier.weight(1.5f),
                                    shape = RoundedCornerShape(14.dp),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = runtimeMinutes,
                                    onValueChange = { runtimeMinutes = it },
                                    label = { Text("Runtime (min)") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp),
                                    singleLine = true
                                )
                            }
                        }
                    }

                    // Rating & Experience Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "HOW WOULD YOU RATE IT?",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.0.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StarRatingBar(
                                    rating = rating,
                                    onRatingChanged = { rating = it },
                                    starSize = 28.dp,
                                    activeColor = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "$rating ★",
                                    fontSize = 18.sp,
                                    fontFamily = FontFamily.Serif,
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Mood chips
                            Text(
                                text = "WHAT MOOD DID IT GIVE YOU?",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.0.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            val moodOptions = listOf(
                                "Loved it", "Comfort", "Emotional", "Thought-provoking",
                                "Romantic", "Exciting", "Feel-good", "Scary", "Disappointed"
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                moodOptions.forEach { m ->
                                    val isSelected = mood == m
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.clickable { mood = m }
                                    ) {
                                        Text(
                                            text = m,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            // Where did you watch it?
                            Text(
                                text = "WHERE DID YOU WATCH IT?",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.0.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            val locations = listOf("Home", "Cinema", "With Friends", "With Family", "Other")
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                locations.forEach { loc ->
                                    val isSelected = watchLocation == loc
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.clickable { watchLocation = loc }
                                    ) {
                                        Text(
                                            text = loc,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            // Favorite Toggle & Category
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = null,
                                        tint = if (isFavorite) Color(0xFFE74C3C) else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clickable { isFavorite = !isFavorite }
                                    )
                                    Text(
                                        text = "Add to Favorites",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Switch(
                                    checked = isFavorite,
                                    onCheckedChange = { isFavorite = it }
                                )
                            }
                        }
                    }

                    // Personal Journal Writing Area
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "PERSONAL JOURNAL & MEMORIES",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.0.sp,
                                color = MaterialTheme.colorScheme.primary
                            )

                            OutlinedTextField(
                                value = journalEntry,
                                onValueChange = { journalEntry = it },
                                label = { Text("What did this movie make you feel or think about?") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .testTag("log_movie_journal_input"),
                                shape = RoundedCornerShape(14.dp),
                                maxLines = 5
                            )

                            OutlinedTextField(
                                value = favoriteMoment,
                                onValueChange = { favoriteMoment = it },
                                label = { Text("Favorite Moment (short summary)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = favoriteScene,
                                onValueChange = { favoriteScene = it },
                                label = { Text("Favorite Scene") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = favoriteQuote,
                                onValueChange = { favoriteQuote = it },
                                label = { Text("Favorite Quote") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = tags,
                                onValueChange = { tags = it },
                                label = { Text("Tags (#cinema, #emotional)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Bottom Save Button
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val entity = (movieToEdit ?: MovieJournalEntity(title = title)).copy(
                                title = title,
                                releaseYear = releaseYear.toIntOrNull() ?: 2024,
                                director = director,
                                genres = genres,
                                runtimeMinutes = runtimeMinutes.toIntOrNull() ?: 120,
                                posterUrl = posterUrl,
                                backdropUrl = backdropUrl,
                                rating = rating,
                                watchedDate = watchedDate,
                                watchLocation = watchLocation,
                                mood = mood,
                                wouldRewatch = wouldRewatch,
                                isFavorite = isFavorite,
                                favoriteCategory = favoriteCategory,
                                journalEntry = journalEntry,
                                favoriteMoment = favoriteMoment,
                                favoriteScene = favoriteScene,
                                favoriteCharacter = favoriteCharacter,
                                favoriteQuote = favoriteQuote,
                                tags = tags,
                                hasSpoiler = hasSpoiler,
                                status = status
                            )
                            viewModel.saveMovie(entity)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_journal_button"),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "Save to My Journal ✦",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
