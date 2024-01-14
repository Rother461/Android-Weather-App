package com.example.myapplication.repository.models

data class CurrentWeatherResponse(
    val weather: List<WeatherData>,
    val main: Main
)
