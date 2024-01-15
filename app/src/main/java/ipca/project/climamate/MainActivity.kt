package ipca.project.climamate


import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var weatherViewModel: WeatherViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        weatherViewModel.weather.observe(this, { weatherData: WeatherData? ->
            weatherData?.let {
                updateUI(it)
            }
        })



        weatherViewModel.error.observe(this, { isError ->
            isError?.let {
                if (it) {
                    showError()
                }
            }
        })

        findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
        findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
        findViewById<TextView>(R.id.errortext).visibility = View.GONE

        weatherViewModel.getWeatherData()
    }

    private fun updateUI(weatherData: WeatherData) {
        findViewById<TextView>(R.id.address).text = weatherData.address
        findViewById<TextView>(R.id.updated_at).text = weatherData.updatedAt
        findViewById<TextView>(R.id.status).text = weatherData.status.replaceFirstChar { it.uppercase(Locale.ROOT) }
        findViewById<TextView>(R.id.temp).text = weatherData.temp
        findViewById<TextView>(R.id.temp_min).text = weatherData.tempMin
        findViewById<TextView>(R.id.temp_max).text = weatherData.tempMax
        findViewById<TextView>(R.id.sunrise).text = weatherData.sunrise
        findViewById<TextView>(R.id.sunset).text = weatherData.sunset
        findViewById<TextView>(R.id.wind).text = weatherData.windSpeed
        findViewById<TextView>(R.id.pressure).text = weatherData.pressure
        findViewById<TextView>(R.id.humidity).text = weatherData.humidity

        findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
        findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
    }

    private fun showError() {
        findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
        findViewById<TextView>(R.id.errortext).visibility = View.VISIBLE
    }




}