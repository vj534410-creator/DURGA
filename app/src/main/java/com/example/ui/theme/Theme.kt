package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun DurgaTheme(
    themeMode: DurgaThemeMode = DurgaThemeMode.BLUE,
    content: @Composable () -> Unit
) {
    val colorScheme = getDurgaColorScheme(themeMode)
    val customColors = getDurgaCustomColors(themeMode)

    CompositionLocalProvider(
        LocalDurgaColors provides customColors,
        LocalDurgaThemeMode provides themeMode
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    DurgaTheme(themeMode = DurgaThemeMode.BLUE, content = content)
}
