package com.example.weather

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.weather.screens.CityScreen
import com.example.weather.screens.MainScreen
import com.example.weather.ui.theme.WeatherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherTheme {
                WeatherApp()
            }
        }
    }
}

/** Маршрути (екрани) застосунку. */
private const val ROUTE_CITY = "city"
private const val ROUTE_WEATHER = "weather/{city}"

/**
 * Навігація між двома вікнами:
 * 1) CityScreen — введення назви міста;
 * 2) MainScreen — перегляд погоди у вибраному місті.
 */
@Composable
fun WeatherApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = ROUTE_CITY) {
        composable(ROUTE_CITY) {
            CityScreen(onCitySelected = { city ->
                navController.navigate("weather/${Uri.encode(city)}")
            })
        }
        composable(
            route = ROUTE_WEATHER,
            arguments = listOf(navArgument("city") { type = NavType.StringType })
        ) { backStackEntry ->
            val city = backStackEntry.arguments?.getString("city").orEmpty()
            MainScreen(
                name = city,
                onBack = { navController.popBackStack(ROUTE_CITY, inclusive = false) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewScreen() {
    WeatherTheme {
        MainScreen(Modifier, "London")
    }
}
