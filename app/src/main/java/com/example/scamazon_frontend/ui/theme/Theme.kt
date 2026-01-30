package com.example.scamazon_frontend.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ===========================================
// SCAMAZON - MINIMAL THEME
// Dark Blue + Gold Dark
// ===========================================

// Light Color Scheme
private val LightColorScheme = lightColorScheme(
    // Primary - Dark Blue
    primary = PrimaryBlue,
    onPrimary = White,
    primaryContainer = PrimaryBlueSoft,
    onPrimaryContainer = PrimaryBlueDark,

    // Secondary - Gold
    secondary = AccentGold,
    onSecondary = White,
    secondaryContainer = AccentGoldSoft,
    onSecondaryContainer = AccentGold,

    // Tertiary - Gold (accent)
    tertiary = AccentGold,
    onTertiary = White,
    tertiaryContainer = AccentGoldSoft,
    onTertiaryContainer = AccentGold,

    // Error
    error = StatusError,
    onError = White,
    errorContainer = Burgundy10,
    onErrorContainer = SecondaryRedDark,

    // Background & Surface
    background = BackgroundWhite,
    onBackground = PrimaryBlue,
    surface = White,
    onSurface = PrimaryBlue,
    surfaceVariant = BackgroundLight,
    onSurfaceVariant = PrimaryBlueLight,

    // Outline
    outline = BorderDefault,
    outlineVariant = BorderLight,

    // Inverse
    inverseSurface = PrimaryBlue,
    inverseOnSurface = White,
    inversePrimary = AccentGoldLight
)

// Dark Color Scheme
private val DarkColorScheme = darkColorScheme(
    // Primary
    primary = AccentGoldLight,
    onPrimary = PrimaryBlueDark,
    primaryContainer = PrimaryBlue,
    onPrimaryContainer = BackgroundLight,

    // Secondary
    secondary = AccentGold,
    onSecondary = PrimaryBlueDark,
    secondaryContainer = PrimaryBlueLight,
    onSecondaryContainer = White,

    // Tertiary
    tertiary = AccentGoldLight,
    onTertiary = PrimaryBlueDark,
    tertiaryContainer = AccentGold,
    onTertiaryContainer = White,

    // Error
    error = StatusError,
    onError = White,
    errorContainer = SecondaryRedDark,
    onErrorContainer = White,

    // Background & Surface
    background = PrimaryBlueDark,
    onBackground = White,
    surface = PrimaryBlue,
    onSurface = White,
    surfaceVariant = PrimaryBlueLight,
    onSurfaceVariant = BackgroundGrey,

    // Outline
    outline = PrimaryBlueLight,
    outlineVariant = Navy40,

    // Inverse
    inverseSurface = White,
    inverseOnSurface = PrimaryBlue,
    inversePrimary = PrimaryBlue
)

@Composable
fun ScamazonFrontendTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Always use light theme
    val colorScheme = LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = BackgroundWhite.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
