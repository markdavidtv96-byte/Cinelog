package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserProfileState(
    val name: String = "Alex",
    val avatarId: String = "avatar_1",
    val personalityTitle: String = "Cinephile",
    val selectedTheme: String = "Amber Classic", // "Amber Classic", "Ocean", "Sage", "Lavender", "Rose", "Gothic"
    val selectedBackground: String = "Subtle Paper", // "None", "Subtle Paper", "Minimal Grid", "Botanical", "Cinema Doodles"
    val selectedDecorations: String = "Film Strips & Stars",
    val yearlyGoal: Int = 50,
    val weeklyGoal: Int = 3,
    val defaultRatingSystem: String = "5_STARS", // "5_STARS", "10_POINTS"
    val defaultMood: String = "Loved it",
    val isOnboardingCompleted: Boolean = true,
    val isDarkMode: Boolean = false
)

class UserPreferencesRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("cinelog_prefs", Context.MODE_PRIVATE)

    private val _userProfile = MutableStateFlow(loadUserProfile())
    val userProfile: StateFlow<UserProfileState> = _userProfile.asStateFlow()

    private fun loadUserProfile(): UserProfileState {
        return UserProfileState(
            name = prefs.getString("name", "Alex") ?: "Alex",
            avatarId = prefs.getString("avatarId", "avatar_1") ?: "avatar_1",
            personalityTitle = prefs.getString("personalityTitle", "Cinephile") ?: "Cinephile",
            selectedTheme = prefs.getString("selectedTheme", "Amber Classic") ?: "Amber Classic",
            selectedBackground = prefs.getString("selectedBackground", "Subtle Paper") ?: "Subtle Paper",
            selectedDecorations = prefs.getString("selectedDecorations", "Film Strips & Stars") ?: "Film Strips & Stars",
            yearlyGoal = prefs.getInt("yearlyGoal", 50),
            weeklyGoal = prefs.getInt("weeklyGoal", 3),
            defaultRatingSystem = prefs.getString("defaultRatingSystem", "5_STARS") ?: "5_STARS",
            defaultMood = prefs.getString("defaultMood", "Loved it") ?: "Loved it",
            isOnboardingCompleted = prefs.getBoolean("isOnboardingCompleted", true),
            isDarkMode = prefs.getBoolean("isDarkMode", false)
        )
    }

    fun updateProfile(updater: (UserProfileState) -> UserProfileState) {
        val newState = updater(_userProfile.value)
        prefs.edit().apply {
            putString("name", newState.name)
            putString("avatarId", newState.avatarId)
            putString("personalityTitle", newState.personalityTitle)
            putString("selectedTheme", newState.selectedTheme)
            putString("selectedBackground", newState.selectedBackground)
            putString("selectedDecorations", newState.selectedDecorations)
            putInt("yearlyGoal", newState.yearlyGoal)
            putInt("weeklyGoal", newState.weeklyGoal)
            putString("defaultRatingSystem", newState.defaultRatingSystem)
            putString("defaultMood", newState.defaultMood)
            putBoolean("isOnboardingCompleted", newState.isOnboardingCompleted)
            putBoolean("isDarkMode", newState.isDarkMode)
            apply()
        }
        _userProfile.value = newState
    }

    fun setTheme(theme: String) {
        updateProfile { it.copy(selectedTheme = theme) }
    }

    fun setBackground(bg: String) {
        updateProfile { it.copy(selectedBackground = bg) }
    }

    fun setGoals(yearly: Int, weekly: Int) {
        updateProfile { it.copy(yearlyGoal = yearly, weeklyGoal = weekly) }
    }

    fun setOnboardingCompleted(completed: Boolean) {
        updateProfile { it.copy(isOnboardingCompleted = completed) }
    }
}
