package com.example.weather

import java.util.Locale
import kotlin.math.roundToInt

/**
 * Дані про поточну погоду, отримані з weatherapi.com (метод current.json).
 */
data class WeatherData(
    val city: String,
    val region: String,
    val country: String,
    val localTime: String,
    val lastUpdated: String,
    val tempC: Double,
    val feelsLikeC: Double,
    val condition: String,
    val iconUrl: String,
    val humidity: Int,
    val windKph: Double,
    val windDegree: Int,
    val windDir: String,
    val gustKph: Double,
    val pressureMb: Double,
    val precipMm: Double,
    val cloud: Int,
    val uv: Double,
    val visKm: Double
) {
    /** Швидкість вітру в м/с (1 м/с = 3,6 км/год). */
    val windMs: Double get() = windKph / 3.6

    /** Швидкість поривів вітру в м/с. */
    val gustMs: Double get() = gustKph / 3.6

    /** Тиск у мм рт. ст. (1 гПа = 0,750062 мм рт. ст.). */
    val pressureMmHg: Int get() = (pressureMb * 0.750062).roundToInt()

    /** Назва напрямку вітру українською (звідки дме вітер). */
    val windDirectionUa: String
        get() {
            val names = listOf(
                "північний", "північно-східний", "східний", "південно-східний",
                "південний", "південно-західний", "західний", "північно-західний"
            )
            val index = (((windDegree % 360 + 360) % 360 + 22.5) / 45.0).toInt() % 8
            return names[index]
        }
}

/** Стан екрана погоди: завантаження, успіх або помилка. */
sealed interface WeatherState {
    data object Loading : WeatherState
    data class Success(val data: WeatherData) : WeatherState
    data class Error(val message: String) : WeatherState
}

private val uaLocale: Locale = Locale.forLanguageTag("uk-UA")

/** Форматування числа з заданою кількістю знаків після коми (українська локаль). */
fun Double.format(digits: Int = 1): String =
    String.format(uaLocale, "%.${digits}f", this)

/** Тестові дані — використовуються лише у вікні Preview Android Studio. */
fun sampleWeather(city: String) = WeatherData(
    city = city,
    region = "City of London, Greater London",
    country = "United Kingdom",
    localTime = "2026-09-28 19:15",
    lastUpdated = "2026-09-28 19:00",
    tempC = 17.3,
    feelsLikeC = 16.8,
    condition = "Мінлива хмарність",
    iconUrl = "",
    humidity = 68,
    windKph = 14.4,
    windDegree = 240,
    windDir = "WSW",
    gustKph = 21.6,
    pressureMb = 1016.0,
    precipMm = 0.0,
    cloud = 50,
    uv = 1.0,
    visKm = 10.0
)
