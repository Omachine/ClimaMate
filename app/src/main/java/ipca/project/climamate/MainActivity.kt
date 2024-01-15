package ipca.project.climamate


import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var loginContainer: RelativeLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        loginContainer = findViewById(R.id.loginContainer)


        // Example: Assume registration button is clicked
        findViewById<Button>(R.id.registerButton).setOnClickListener {
            val email = findViewById<EditText>(R.id.editTextEmail).text.toString()
            val password = findViewById<EditText>(R.id.editTextPassword).text.toString()
            val preferredLocation =
                findViewById<EditText>(R.id.editTextPreferredLocation).text.toString()

            registerUser(email, password, preferredLocation)
        }

        findViewById<Button>(R.id.loginButton).setOnClickListener {
            val email = findViewById<EditText>(R.id.editTextEmail).text.toString()
            val password = findViewById<EditText>(R.id.editTextPassword).text.toString()

            loginUser(email, password)
        }


        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        weatherViewModel.weather.observe(this) { weatherData: WeatherData? ->
            weatherData?.let {
                updateUI(it)
            }
        }

        weatherViewModel.error.observe(this) { isError ->
            isError?.let {
                if (it) {
                    showError()
                }
            }
        }

        findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
        findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
        findViewById<TextView>(R.id.errortext).visibility = View.GONE

        weatherViewModel.getWeatherData()
    }
    private fun registerUser(email: String, password: String, preferredLocation: String) {
        showLoader()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                val mainContainer = findViewById<RelativeLayout>(R.id.mainContainer)
                val loader = findViewById<ProgressBar>(R.id.loader)
                val errorText = findViewById<TextView>(R.id.errortext)
                if (task.isSuccessful) {
                    // Registration successful
                    val user = FirebaseAuth.getInstance().currentUser
                    user?.let {
                        storeUserLocation(it.uid, preferredLocation)
                        // Assuming `mainContainer`, `loader`, and `errorText` are your other containers
                        mainContainer.visibility = View.VISIBLE
                        loader.visibility = View.GONE
                        errorText.visibility = View.GONE
                        loginContainer.visibility = View.GONE // This should be hidden after login/registration

                    }
                } else {
                    // Registration failed, handle the error
                    showError()
                }
            }
    }

    private fun loginUser(email: String, password: String) {
        showLoader()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login successful, proceed with other operations
                    onLoginSuccess()
                } else {
                    showError()
                }
            }
    }

    fun onLoginSuccess() {
        // Change visibility of other containers
        val mainContainer = findViewById<RelativeLayout>(R.id.mainContainer)
        val loader = findViewById<ProgressBar>(R.id.loader)
        val errorText = findViewById<TextView>(R.id.errortext)

        mainContainer.visibility = View.VISIBLE
        loader.visibility = View.GONE
        errorText.visibility = View.GONE

        // Change visibility of loginContainer
        loginContainer.visibility = View.GONE
    }




    private fun storeUserLocation(userId: String, preferredLocation: String) {
        val db = FirebaseFirestore.getInstance()
        val userLocation = hashMapOf(
            "preferredLocation" to preferredLocation
        )

        db.collection("users")
            .document(userId)
            .set(userLocation)
            .addOnSuccessListener {
                // User location stored successfully
                // Proceed with other operations or navigate to the app's main screen
            }
            .addOnFailureListener { _ ->
                // Error storing user location
                showError()
            }
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

    private fun showLoader() {
        findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
        findViewById<TextView>(R.id.errortext).visibility = View.GONE
        findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
    }

    private fun showError() {
        findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
        findViewById<TextView>(R.id.errortext).visibility = View.VISIBLE
        findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
    }
}