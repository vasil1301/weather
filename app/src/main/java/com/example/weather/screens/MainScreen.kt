package com.example.weather.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weather.WeatherData
import com.example.weather.WeatherState
import com.example.weather.format
import com.example.weather.getData
import com.example.weather.sampleWeather
import com.example.weather.ui.theme.CardBackground
import com.example.weather.ui.theme.WeatherTheme

/**
 * Другий екран застосунку — погода в місті [name].
 *
 * @param onBack дія кнопки «Інше місто» (повернення на екран вводу міста)
 */
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    name: String,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    // У вікні Preview мережеві запити не виконуються, тому показуємо тестові дані
    val isPreview = LocalInspectionMode.current
    val weatherState = remember(name) {
        mutableStateOf<WeatherState>(
            if (isPreview) WeatherState.Success(sampleWeather(name)) else WeatherState.Loading
        )
    }
    var refreshKey by remember { mutableIntStateOf(0) }

    // Запит до API виконується один раз при відкритті екрана та після натискання «Оновити»
    LaunchedEffect(name, refreshKey) {
        if (!isPreview) {
            weatherState.value = WeatherState.Loading
            getData(name, context, weatherState)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        WeatherBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(5.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (val state = weatherState.value) {
                is WeatherState.Loading -> MessageCard(name, "Завантаження…", showProgress = true)
                is WeatherState.Error -> MessageCard(name, state.message, showProgress = false)
                is WeatherState.Success -> {
                    CurrentWeatherCard(state.data)
                    DetailsCard(state.data)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                    Text("Інше місто")
                }
                Button(onClick = { refreshKey++ }, modifier = Modifier.weight(1f)) {
                    Text("Оновити")
                }
            }
        }
    }
}

/** Основна картка: дата і час, місто, температура, стан погоди (пп. 6, 9 методички). */
@Composable
private fun CurrentWeatherCard(data: WeatherData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = data.localTime,
                color = Color.DarkGray,
                fontSize = 30.sp
            )
            Text(
                text = data.city,
                style = TextStyle(fontSize = 24.sp),
                color = Color.Red
            )
            Text(
                text = listOf(data.region, data.country).filter { it.isNotBlank() }.joinToString(", "),
                fontSize = 14.sp,
                color = Color.DarkGray,
                textAlign = TextAlign.Center
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (data.iconUrl.isNotEmpty()) {
                    AsyncImage(
                        model = data.iconUrl,
                        contentDescription = data.condition,
                        modifier = Modifier.size(72.dp)
                    )
                }
                Text(
                    text = "${data.tempC.format()}ºC",
                    style = TextStyle(fontSize = 65.sp),
                    color = Color.Magenta
                )
            }
            Text(
                text = data.condition,
                style = TextStyle(fontSize = 20.sp),
                color = Color.Black
            )
            Text(
                text = "Дані оновлено: ${data.lastUpdated}",
                fontSize = 12.sp,
                color = Color.DarkGray
            )
        }
    }
}

/** Картка з додатковими даними (самостійне завдання 1). */
@Composable
private fun DetailsCard(data: WeatherData) {
    val rows = listOf(
        "Відчувається як" to "${data.feelsLikeC.format()}ºC",
        "Вологість" to "${data.humidity} %",
        "Вітер" to "${data.windMs.format()} м/с (${data.windKph.format()} км/год)",
        "Напрямок вітру" to "${data.windDirectionUa} (${data.windDir}, ${data.windDegree}º)",
        "Пориви вітру" to "до ${data.gustMs.format()} м/с",
        "Атмосферний тиск" to "${data.pressureMmHg} мм рт. ст. (${data.pressureMb.format(0)} гПа)",
        "Хмарність" to "${data.cloud} %",
        "Опади" to "${data.precipMm.format()} мм",
        "УФ-індекс" to data.uv.format(),
        "Видимість" to "${data.visKm.format()} км"
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(
                text = "Детальніше",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            rows.forEachIndexed { index, (label, value) ->
                if (index > 0) HorizontalDivider(color = Color.LightGray)
                DetailRow(label, value)
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 15.sp, color = Color.DarkGray)
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            textAlign = TextAlign.End
        )
    }
}

/** Картка для стану «Завантаження…» або повідомлення про помилку. */
@Composable
private fun MessageCard(name: String, message: String, showProgress: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = name, style = TextStyle(fontSize = 24.sp), color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
            if (showProgress) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(
                text = message,
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    WeatherTheme {
        MainScreen(Modifier, "London")
    }
}
