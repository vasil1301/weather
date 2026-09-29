package com.example.weather.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.weather.R

/** Фонове зображення res/drawable/fon.jpg на весь екран (п. 3, 5 методички). */
@Composable
fun WeatherBackground() {
    Image(
        painter = painterResource(id = R.drawable.fon),
        contentDescription = "im1",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}
