package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class AppThemeColors(
    val primary: Color,
    val primaryVariant: Color,
    val secondary: Color,
    val container: Color,
    val onContainer: Color
)

fun getThemePalette(themeName: String, isDark: Boolean): AppThemeColors {
    return when (themeName) {
        "Ocean" -> AppThemeColors(
            primary = OceanPrimary,
            primaryVariant = OceanPrimaryVariant,
            secondary = OceanSecondary,
            container = if (isDark) Color(0xFF132A3E) else OceanContainer,
            onContainer = if (isDark) Color(0xFFC7E2F5) else OceanOnContainer
        )
        "Sage" -> AppThemeColors(
            primary = SagePrimary,
            primaryVariant = SagePrimaryVariant,
            secondary = SageSecondary,
            container = if (isDark) Color(0xFF1E2F26) else SageContainer,
            onContainer = if (isDark) Color(0xFFCFE0D6) else SageOnContainer
        )
        "Lavender" -> AppThemeColors(
            primary = LavenderPrimary,
            primaryVariant = LavenderPrimaryVariant,
            secondary = LavenderSecondary,
            container = if (isDark) Color(0xFF2A213D) else LavenderContainer,
            onContainer = if (isDark) Color(0xFFE2DBF2) else LavenderOnContainer
        )
        "Rose" -> AppThemeColors(
            primary = RosePrimary,
            primaryVariant = RosePrimaryVariant,
            secondary = RoseSecondary,
            container = if (isDark) Color(0xFF381B24) else RoseContainer,
            onContainer = if (isDark) Color(0xFFF2D3DC) else RoseOnContainer
        )
        "Gothic" -> AppThemeColors(
            primary = GothicPrimary,
            primaryVariant = GothicPrimaryVariant,
            secondary = GothicSecondary,
            container = if (isDark) Color(0xFF2B271F) else GothicContainer,
            onContainer = if (isDark) Color(0xFFF3E7C4) else GothicOnContainer
        )
        else -> AppThemeColors( // Amber Classic
            primary = AmberPrimary,
            primaryVariant = AmberPrimaryVariant,
            secondary = AmberSecondary,
            container = if (isDark) Color(0xFF382312) else AmberContainer,
            onContainer = if (isDark) Color(0xFFF9DCBF) else AmberOnContainer
        )
    }
}

@Composable
fun CineLogTheme(
    selectedTheme: String = "Amber Classic",
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val palette = getThemePalette(selectedTheme, darkTheme)

    val colorScheme: ColorScheme = if (darkTheme) {
        darkColorScheme(
            primary = palette.primary,
            onPrimary = Color.White,
            primaryContainer = palette.container,
            onPrimaryContainer = palette.onContainer,
            secondary = palette.secondary,
            onSecondary = Color.White,
            background = BackgroundDark,
            onBackground = TextPrimaryDark,
            surface = SurfaceDark,
            onSurface = TextPrimaryDark,
            surfaceVariant = SurfaceVariantDark,
            onSurfaceVariant = TextSecondaryDark,
            outline = BorderDark
        )
    } else {
        lightColorScheme(
            primary = palette.primary,
            onPrimary = Color.White,
            primaryContainer = palette.container,
            onPrimaryContainer = palette.onContainer,
            secondary = palette.secondary,
            onSecondary = Color.White,
            background = BackgroundLight,
            onBackground = TextPrimaryLight,
            surface = SurfaceLight,
            onSurface = TextPrimaryLight,
            surfaceVariant = SurfaceVariantLight,
            onSurfaceVariant = TextSecondaryLight,
            outline = BorderLight
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
