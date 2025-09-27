package com.example.weatherapp
import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val main: Main,
    val weather:List<Weather>,
    val wind: Wind,
    val name: String
)

data class Main(
    val temp : Double,
    @SerializedName("feels_like") val feelsLike: Double,
    val humidity: Int
)

data class Weather(
    val description : String,
    val icon: String
)

data class Wind(
    val speed : Double
)