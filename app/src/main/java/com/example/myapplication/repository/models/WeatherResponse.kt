package com.example.myapplication.repository.models

data class WeatherResponse(
    val list: List<WeatherItem>
)

data class Main(
    val temp: Float,
    val feels_like: Float,
    val temp_min: Float,
    val temp_max: Float,
    val pressure: Float,
    val sea_level: Float,
    val grnd_level: Float,
    val humidity: Float,
    val temp_kf: Float
)

data class WeatherItem(
    val dt: Long,
    val main: Main,
    val weather: List<WeatherData>
)