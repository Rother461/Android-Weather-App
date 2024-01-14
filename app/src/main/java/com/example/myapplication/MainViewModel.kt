package com.example.myapplication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.repository.WeatherRepository
import com.example.myapplication.repository.models.CurrentWeatherResponse
import com.example.myapplication.repository.models.Main
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val weatherRepository = WeatherRepository()

    private val mutableWeatherData = MutableLiveData<UiState<List<CurrentWeatherResponse>>>()
    val immutableWeatherData: LiveData<UiState<List<CurrentWeatherResponse>>> = mutableWeatherData

    fun getData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                mutableWeatherData.postValue(UiState(isLoading = true))
                val request = weatherRepository.getCurrentWeatherResponse()
                if (request.raw().code == 400) {
                    mutableWeatherData.postValue(UiState(error = "Niepoprawne współrzędne"))
                }
                if (request.isSuccessful) {
                    mutableWeatherData.postValue(UiState(isLoading = false))
                    val weatherResponse = request.body()
                    val weather = CurrentWeatherResponse(
                        weather = weatherResponse?.weather ?: emptyList(),
                        main = weatherResponse?.main ?: Main(
                            0.0f,
                            0.0f,
                            0.0f,
                            0.0f,
                            0.0f,
                            0.0f,
                            0.0f,
                            0.0f,
                            0.0f
                        )
                    )

                    mutableWeatherData.postValue(UiState(data = listOf(weather)))
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Operacja nie powiodła się", e)
                mutableWeatherData.postValue(UiState(isLoading = false))
                mutableWeatherData.postValue(UiState(error = e.message))
            }
        }
    }
}