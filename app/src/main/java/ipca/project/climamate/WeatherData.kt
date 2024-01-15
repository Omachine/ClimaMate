package ipca.project.climamate



// WeatherData.kt
data class WeatherData(
    val address: String,
    val updatedAt: String,
    val status: String,
    val temp: String,
    val tempMin: String,
    val tempMax: String,
    val sunrise: String,
    val sunset: String,
    val windSpeed: String,
    val pressure: String,
    val humidity: String
)

