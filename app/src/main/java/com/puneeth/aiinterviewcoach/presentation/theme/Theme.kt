package com.puneeth.aiinterviewcoach.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = Cyan,
    secondary = Emerald,
    tertiary = Amber,
    background = Midnight,
    surface = Slate,
)

private val LightColors = lightColorScheme(
    primary = Slate,
    secondary = Emerald,
    tertiary = Coral,
    background = Ice,
    surface = ColorWhite,
)

private val ColorWhite = androidx.compose.ui.graphics.Color.White

@Composable
fun AIAndroidInterviewCoachTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content,
    )
}
