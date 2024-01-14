package com.example.myapplication.repository

import com.example.myapplication.repository.models.AirPollutionResponse
import com.example.myapplication.repository.models.CurrentWeatherResponse
import com.example.myapplication.repository.models.WeatherResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("forecast")
    suspend fun getForecastWeatherResponse(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = API_KEY
    ): Response<WeatherResponse>

    @GET("air_pollution")
    suspend fun getAirPollutionResponse(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = API_KEY
    ): Response<AirPollutionResponse>

    @GET("weather")
    suspend fun getCurrentWeatherResponse(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = API_KEY
    ): Response<CurrentWeatherResponse>

    companion object {
        private const val API_KEY = "0eadefea20345742ec17c87ae3d8c55a"
        private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

        private val logger = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        private val okHttp = OkHttpClient.Builder().apply {
            this.addInterceptor(logger)
        }.build()

        private val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttp)
                .build()
        }

        val weatherService: WeatherService by lazy { retrofit.create(WeatherService::class.java) }
    }
}
