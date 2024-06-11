package com.example.weatherapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import com.example.weatherapplication.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//83ddb2ccc37852c18bc7d1fcca76cb38
class MainActivity : AppCompatActivity() {
    private  val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchweatherdata("Faridabad")
        Searchcity()
    }

    private fun Searchcity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchweatherdata(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchweatherdata(cityName:String) {
        val retrofit =Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response=retrofit.getWeatherData(cityName,"83ddb2ccc37852c18bc7d1fcca76cb38","metric")
        response.enqueue(object :Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody !=null){
                   val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windspeed = responseBody.wind.speed
                    val sunrise = responseBody.sys.sunrise.toLong()
                    val  sunset = responseBody.sys.sunset.toLong()
                    val sea = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val max = responseBody.main.temp_max
                    val min = responseBody.main.temp_min


                    binding.temp.text="$temperature °C"
                    binding.weather.text = condition
                    binding.max.text = "Max Temp: $max °C"
                    binding.min.text = "Min Temp: $min °C"
                    binding.humidity.text="$humidity"
                    binding.windspeed.text="$windspeed m/s"
                    binding.sunrise.text="${time(sunrise)}"
                    binding.sunset.text="${time(sunset)}"
                    binding.sea.text="$sea"
                    binding.condition.text=condition
                    binding.dayName.text=dayName(System.currentTimeMillis())
                    binding.date.text=date()
                    binding.city.text="$cityName"

                    changeImageWeather(condition)
//                    Log.d("TAG", "onResponse: $temperature")
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
            }

        })

    }

    private fun changeImageWeather(conditions:String) {
        when(conditions){
            "Clear Sky","Sunny","Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView2.setAnimation(R.raw.sun)
            }"Partly Clouds","Clouds","Overcast","Mist","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView2.setAnimation(R.raw.cloud)
            }"Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView2.setAnimation(R.raw.rain)
            }"Light Snow","Moderate Snow","Heavy Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView2.setAnimation(R.raw.snow)
            }
            else->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView2.setAnimation(R.raw.sun)

            }
        }
        binding.lottieAnimationView2.pauseAnimation()
    }

    fun dayName(timestamp: Long): String{
        val simpleDateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        return simpleDateFormat.format((Date()))
    }
    fun date(): String{
        val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return simpleDateFormat.format((Date()))
    }
   private fun time(timestamp: Long): String{
        val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return simpleDateFormat.format((Date(timestamp*1000)))
    }
}