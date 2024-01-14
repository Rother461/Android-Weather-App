package com.example.myapplication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.repository.WeatherRepository
import com.example.myapplication.repository.models.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ForecastWeatherActivityModel : ViewModel() {
    private val weatherRepository = WeatherRepository()

    private val mutableForecastedWeatherData = MutableLiveData<UiState<List<WeatherResponse>>>()
    val immutableForecastedWeatherData: LiveData<UiState<List<WeatherResponse>>> = mutableForecastedWeatherData

    fun getData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mutableForecastedWeatherData.postValue(UiState(isLoading = true))
                val request = weatherRepository.getForecastedWeatherResponse()
                if (request.raw().code == 400) {
                    mutableForecastedWeatherData.postValue(UiState(error = "Niepoprawne współrzędne"))
                }
                Thread.sleep(2000)
                if (request.isSuccessful) {
                    mutableForecastedWeatherData.postValue(UiState(isLoading = false))
                    val weather = request.body()
                    val weatherList = weather?.list ?: emptyList()


                    val mappedWeatherList = weatherList.map { weatherItem ->
                        WeatherResponse(
                            list = listOf(weatherItem),

                            )
                    }

                    mutableForecastedWeatherData.postValue(UiState(data = mappedWeatherList))
                }
            } catch (e: Exception) {
                Log.e("ForecastWeatherActivityModel", "Operacja nie powiodła się", e)
                mutableForecastedWeatherData.postValue(UiState(isLoading = false))
                mutableForecastedWeatherData.postValue(UiState(error = e.message))
            }
        }
    }
}
