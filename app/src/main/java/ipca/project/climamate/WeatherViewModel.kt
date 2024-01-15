package ipca.project.climamate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.LiveData


class WeatherViewModel : ViewModel() {

    private val _weather = MutableLiveData<WeatherData>()
    val weather: LiveData<WeatherData> get() = _weather

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> get() = _error

    private val CITY: String = "porto,pt"
    private val API: String = "62cab5a0e2d58e759a83a06ec3d54f6d"

    fun getWeatherData() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API")
                    .readText(Charsets.UTF_8)

                val weatherData = parseWeatherData(response)
                _weather.postValue(weatherData)
            } catch (e: Exception) {
                _error.postValue(true)
            }
        }
    }

    private fun parseWeatherData(response: String): WeatherData {
        val jsonObj = JSONObject(response)
        val main = jsonObj.getJSONObject("main")
        val sys = jsonObj.getJSONObject("sys")
        val wind = jsonObj.getJSONObject("wind")
        val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

        val updatedAt: Long = jsonObj.getLong("dt")
        val updatedAtText = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(updatedAt * 1000))
        val temp = main.getString("temp") + "°C"
        val tempMin = "Min Temp: " + main.getString("temp_min") + "°C"
        val tempMax = "Max Temp: " + main.getString("temp_max") + "°C"
        val pressure = main.getString("pressure")
        val humidity = main.getString("humidity")

        val sunrise: Long = sys.getLong("sunrise")
        val sunset: Long = sys.getLong("sunset")
        val windSpeed = wind.getString("speed")
        val weatherDescription = weather.getString("description")

        val address = jsonObj.getString("name") + ", " + sys.getString("country")

        return WeatherData(
            address,
            updatedAtText,
            weatherDescription,
            temp,
            tempMin,
            tempMax,
            SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000)),
            SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000)),
            windSpeed,
            pressure,
            humidity
        )
    }
}
