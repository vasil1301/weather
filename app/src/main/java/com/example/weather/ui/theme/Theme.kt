package com.example.weather.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val WeatherColorScheme = lightColorScheme(
    primary = SkyBlue,
    secondary = SunYellow,
    primaryContainer = SkyBlueLight
)

/**
 * Тема застосунку. Використовується лише світла схема, оскільки
 * текст розміщено на світлих напівпрозорих картках поверх фонового зображення.
 */
@Composable
fun WeatherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = WeatherColorScheme,
        typography = Typography,
        content = content
    )
}
