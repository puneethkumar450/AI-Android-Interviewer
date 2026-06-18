package com.puneeth.aiinterviewcoach.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColors = darkColorScheme(
    primary = Cyan,
    secondary = Emerald,
    tertiary = Amber,
    background = Midnight,
    surface = Slate,
)

private val ColorWhite = androidx.compose.ui.graphics.Color.White

private val LightColors = lightColorScheme(
    primary = Slate,
    secondary = Emerald,
    tertiary = Coral,
    background = Ice,
    surface = ColorWhite,
)

@Composable
fun AIAndroidInterviewCoachTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    MaterialTheme(
        colorScheme = when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && darkTheme ->
                dynamicDarkColorScheme(context)
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
                dynamicLightColorScheme(context)
            darkTheme -> DarkColors
            else -> LightColors
        },
        typography = AppTypography,
        content = content,
    )
}
