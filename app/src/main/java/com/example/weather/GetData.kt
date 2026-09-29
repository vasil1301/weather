package com.example.weather

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import com.android.volley.NetworkResponse
import com.android.volley.NoConnectionError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.TimeoutError
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.net.URLEncoder

// Ключ доступу з особистого кабінету https://www.weatherapi.com/
// (після реєстрації замініть на власний ключ)
const val API_KEY = "2b462378835b488588893919230508"

private const val BASE_URL = "https://api.weatherapi.com/v1/current.json"
private const val TAG = "MyLog"

// Одна черга запитів Volley на весь застосунок
private var requestQueue: RequestQueue? = null

private fun getQueue(context: Context): RequestQueue =
    requestQueue ?: Volley.newRequestQueue(context.applicationContext).also { requestQueue = it }

/**
 * Надсилає GET-запит до weatherapi.com і записує результат у [state].
 *
 * @param name    назва міста (наприклад, "London", "Lutsk")
 * @param context контекст Android, потрібний для створення черги Volley
 * @param state   стан, який відображається на екрані
 */
fun getData(name: String, context: Context, state: MutableState<WeatherState>) {
    val url = BASE_URL +
            "?key=$API_KEY" +
            "&q=${URLEncoder.encode(name, "UTF-8")}" +
            "&aqi=no" +
            "&lang=uk"

    val stringRequest = object : StringRequest(
        Request.Method.GET,
        url,
        { response ->
            try {
                val data = parseWeather(response)
                state.value = WeatherState.Success(data)
                Log.d(TAG, "Response: $data")
            } catch (e: JSONException) {
                Log.d(TAG, "JSON parsing error: ${e.message}")
                state.value = WeatherState.Error("Помилка обробки даних: ${e.message}")
            }
        },
        { error ->
            Log.d(TAG, "Volley error: $error")
            state.value = WeatherState.Error(errorMessage(error, name))
        }
    ) {
        // Примусово читаємо відповідь у UTF-8, щоб коректно відображалась кирилиця
        override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
            val body = String(response.data, Charsets.UTF_8)
            return Response.success(body, HttpHeaderParser.parseCacheHeaders(response))
        }
    }

    getQueue(context).add(stringRequest)
}

/** Розбір JSON-відповіді сервера у об'єкт [WeatherData]. */
fun parseWeather(json: String): WeatherData {
    val obj = JSONObject(json)
    val location = obj.getJSONObject("location")
    val current = obj.getJSONObject("current")
    val condition = current.getJSONObject("condition")
    val icon = condition.optString("icon")

    return WeatherData(
        city = location.getString("name"),
        region = location.optString("region"),
        country = location.optString("country"),
        localTime = location.getString("localtime"),
        lastUpdated = current.optString("last_updated"),
        tempC = current.getDouble("temp_c"),
        feelsLikeC = current.getDouble("feelslike_c"),
        condition = condition.getString("text"),
        iconUrl = if (icon.startsWith("//")) "https:$icon" else icon,
        humidity = current.getInt("humidity"),
        windKph = current.getDouble("wind_kph"),
        windDegree = current.optInt("wind_degree"),
        windDir = current.optString("wind_dir"),
        gustKph = current.optDouble("gust_kph", 0.0),
        pressureMb = current.getDouble("pressure_mb"),
        precipMm = current.optDouble("precip_mm", 0.0),
        cloud = current.optInt("cloud"),
        uv = current.optDouble("uv", 0.0),
        visKm = current.optDouble("vis_km", 0.0)
    )
}

/** Зрозуміле повідомлення про помилку для користувача. */
private fun errorMessage(error: VolleyError, name: String): String {
    val body = error.networkResponse?.data
    if (body != null) {
        try {
            val apiError = JSONObject(String(body, Charsets.UTF_8)).getJSONObject("error")
            return when (apiError.optInt("code")) {
                1006 -> "Місто «$name» не знайдено. Перевірте назву (краще латиницею)."
                1002, 2006 -> "Невірний API-ключ. Вкажіть свій ключ у файлі GetData.kt."
                2007 -> "Вичерпано ліміт запитів для цього API-ключа."
                2008 -> "API-ключ вимкнено. Отримайте новий на weatherapi.com."
                else -> "Помилка сервера: ${apiError.optString("message")}"
            }
        } catch (e: JSONException) {
            Log.d(TAG, "Error body is not JSON: ${e.message}")
        }
    }
    return when (error) {
        is NoConnectionError -> "Немає з'єднання з Інтернетом."
        is TimeoutError -> "Сервер не відповідає. Спробуйте пізніше."
        else -> "Не вдалося отримати дані (${error.javaClass.simpleName})."
    }
}
