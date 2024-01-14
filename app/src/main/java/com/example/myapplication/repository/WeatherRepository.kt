package com.example.myapplication.repository

import com.example.myapplication.repository.models.AirPollutionResponse
import com.example.myapplication.repository.models.CurrentWeatherResponse
import com.example.myapplication.repository.models.WeatherResponse
import retrofit2.Response

class WeatherRepository {

    suspend fun getForecastedWeatherResponse(): Response<WeatherResponse> =
        WeatherService.weatherService.getForecastWeatherResponse(50.04, 19.9)

    suspend fun getCurrentAirPollution(): Response<AirPollutionResponse> =
        WeatherService.weatherService.getAirPollutionResponse(50.04, 19.9)

    suspend fun getCurrentWeatherResponse(): Response<CurrentWeatherResponse> =
        WeatherService.weatherService.getCurrentWeatherResponse(50.04, 19.9)
}