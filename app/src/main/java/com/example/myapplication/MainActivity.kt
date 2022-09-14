package com.example.myapplication

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.myapplication.databinding.ActivityMainBinding
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    val CITY : String = "Shiraz,IR"
    val API : String = "1776df31b5cbb98df8e3ecf310a549e5"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        weatherTask().execute()
    }

    inner class weatherTask(): AsyncTask<String, Void, String>(){

        override fun onPreExecute() {
            super.onPreExecute()
            /*show the progressBar , Making the main design Gone*/
            binding.apply {
                loader.visibility = View.VISIBLE
                mainContainer.visibility = View.GONE
                errorText.visibility = View.GONE
            }
        }

        override fun doInBackground(vararg p0: String?): String? {
            var response : String?
            try{
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&appid=$API")
                    .readText(Charsets.UTF_8)
            }
            catch (e : Exception){
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val Wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updatedAt: Long = jsonObj.getLong("dt")
                val updatedAtText =
                    "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                        Date(updatedAt * 1000)
                    )
                val Temp = BigDecimal((main.getString("temp").toDouble() - 273.15)).setScale(2,RoundingMode.HALF_EVEN).toString() + "°C"
                val TempMin = "Min Temp: " + BigDecimal(((main.getString("temp_min")).toDouble()-273.15)).setScale(2,RoundingMode.HALF_EVEN) + "°C"
                val TempMax = "Max Temp: " + BigDecimal(((main.getString("temp_max")).toDouble()-273.15)).setScale(2,RoundingMode.HALF_EVEN) + "°C"
                val Pressure = main.getString("pressure")
                val Humidity = main.getString("humidity")

                val Sunrise: Long = sys.getLong("sunrise")
                val Sunset: Long = sys.getLong("sunset")
                val WindSpeed = Wind.getString("speed")
                val weatherDescription = weather.getString("description")

                val Address = jsonObj.getString("name") + ", " + sys.getString("country")

                /* Populating extracted data into our views */
                binding.apply {
                    address.text = Address
                    UpdatedTime.text = updatedAtText
                    status.text = weatherDescription.capitalize()
                    temp.text = Temp
                    tempMax.text = TempMax
                    tempMin.text = TempMin
                    sunrise.text =
                        SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(Sunrise * 1000))
                    sunset.text =
                        SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(Sunset * 1000))
                    wind.text = WindSpeed
                    pressure.text = Pressure
                    humidity.text = Humidity

                    /* Views populated, Hiding the loader, Showing the main design */
                    loader.visibility = View.GONE
                    mainContainer.visibility = View.VISIBLE
                }
            }
            catch(e : Exception){
                binding.loader.visibility = View.GONE
                binding.errorText.visibility = View.VISIBLE
            }
        }
    }
}