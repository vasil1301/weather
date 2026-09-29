# Weather — практична робота №7 «Проєкт Прогноз погоди»

Android-застосунок (Kotlin + Jetpack Compose), який показує поточну погоду у вибраному місті
за допомогою API сервісу https://www.weatherapi.com/ та бібліотеки Volley.

## Як запустити
1. Android Studio → **File → Open** → вибрати папку `Weather` (ту, де лежить `settings.gradle.kts`).
2. Дочекатися завершення **Gradle Sync** (потрібен Інтернет для завантаження залежностей).
3. У файлі `app/src/main/java/com/example/weather/GetData.kt` замінити `API_KEY` на власний ключ
   з особистого кабінету weatherapi.com (безкоштовний тариф).
4. Запустити на емуляторі або телефоні (кнопка **Run 'app'**).

## Структура
- `MainActivity.kt` — точка входу і навігація між двома вікнами (NavHost).
- `GetData.kt` — запит до API через Volley і розбір JSON.
- `WeatherData.kt` — модель даних, стани екрана, перерахунок одиниць.
- `screens/CityScreen.kt` — вікно 1: введення назви міста.
- `screens/MainScreen.kt` — вікно 2: погода (температура, вологість, вітер, тиск тощо).
- `screens/WeatherBackground.kt` — фонове зображення `res/drawable/fon.jpg`.

Версії: AGP 8.7.3, Gradle 8.11.1, Kotlin 2.0.21, Compose BOM 2024.12.01, compileSdk 35, minSdk 26.
