package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.CineNavTab
import com.example.ui.components.DecorativeBackground
import com.example.ui.components.FrostedGlassNavBar
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InsightsScreen
import com.example.ui.screens.JournalScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.LogMovieModal
import com.example.ui.screens.MovieDetailModal
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.CineLogTheme
import com.example.ui.viewmodel.CineLogViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: CineLogViewModel = viewModel()
            val userProfile by viewModel.userProfile.collectAsState()

            CineLogTheme(
                selectedTheme = userProfile.selectedTheme,
                darkTheme = userProfile.isDarkMode
            ) {
                CineLogApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CineLogApp(viewModel: CineLogViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isLogModalOpen by viewModel.isLogModalOpen.collectAsState()
    val editingMovie by viewModel.editingMovie.collectAsState()
    val selectedMovie by viewModel.selectedMovie.collectAsState()

    var currentTab by remember { mutableStateOf(CineNavTab.HOME) }

    DecorativeBackground(
        backgroundStyle = userProfile.selectedBackground,
        isDark = userProfile.isDarkMode
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            bottomBar = {
                FrostedGlassNavBar(
                    selectedTab = currentTab,
                    onTabSelected = { currentTab = it }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.openLogModal() },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp),
                    modifier = Modifier
                        .padding(bottom = 70.dp, end = 4.dp)
                        .size(56.dp)
                        .testTag("fab_log_movie")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Log Movie",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
            ) {
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = {
                        (fadeIn() + slideInHorizontally { width -> if (targetState.ordinal > initialState.ordinal) width / 4 else -width / 4 })
                            .togetherWith(fadeOut() + slideOutHorizontally { width -> if (targetState.ordinal > initialState.ordinal) -width / 4 else width / 4 })
                    },
                    label = "tab_content_anim"
                ) { tab ->
                    when (tab) {
                        CineNavTab.HOME -> HomeScreen(
                            viewModel = viewModel,
                            onNavigateToSettings = { currentTab = CineNavTab.SETTINGS },
                            onNavigateToJournal = { currentTab = CineNavTab.JOURNAL },
                            onOpenLogModal = { viewModel.openLogModal() },
                            onSelectMovie = { viewModel.selectMovieDetail(it) }
                        )
                        CineNavTab.LIBRARY -> LibraryScreen(
                            viewModel = viewModel,
                            onSelectMovie = { viewModel.selectMovieDetail(it) },
                            onOpenLogModal = { viewModel.openLogModal() }
                        )
                        CineNavTab.JOURNAL -> JournalScreen(
                            viewModel = viewModel,
                            onSelectMovie = { viewModel.selectMovieDetail(it) },
                            onOpenLogModal = { viewModel.openLogModal() }
                        )
                        CineNavTab.INSIGHTS -> InsightsScreen(
                            viewModel = viewModel,
                            onSelectMovie = { viewModel.selectMovieDetail(it) }
                        )
                        CineNavTab.SETTINGS -> SettingsScreen(
                            viewModel = viewModel
                        )
                    }
                }
            }
        }

        // Modals & Overlays
        if (isLogModalOpen) {
            LogMovieModal(
                viewModel = viewModel,
                movieToEdit = editingMovie,
                onDismiss = { viewModel.closeLogModal() }
            )
        }

        if (selectedMovie != null) {
            MovieDetailModal(
                movie = selectedMovie!!,
                viewModel = viewModel,
                onDismiss = { viewModel.selectMovieDetail(null) }
            )
        }
    }
}
