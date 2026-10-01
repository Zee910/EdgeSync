package com.nexis.edgesync.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = EdgePrimary,
    secondary = EdgeSecondary,
    background = EdgeSurfaceLight,
    surface = EdgeCardLight,
    surfaceVariant = EdgeOutline,
    outline = EdgeOutline,
)

private val DarkColors = darkColorScheme(
    primary = EdgePrimaryDark,
    secondary = EdgeSecondary,
    background = EdgeSurfaceDark,
    surface = EdgeCardDark,
    surfaceVariant = EdgeOutlineDark,
    outline = EdgeOutlineDark,
)

@Composable
fun EdgeSyncTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = EdgeTypography,
        content = content,
    )
}
