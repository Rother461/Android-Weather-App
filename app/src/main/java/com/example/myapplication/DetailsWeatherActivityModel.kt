package com.example.myapplication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.repository.WeatherRepository
import com.example.myapplication.repository.models.AirPollutionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsWeatherActivityModel : ViewModel() {

    private val weatherRepository = WeatherRepository()

    private val airPollutionData = MutableLiveData<UiState<List<AirPollutionResponse>>>()
    val immutableAirPollutionData: LiveData<UiState<List<AirPollutionResponse>>> = airPollutionData

    fun getData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                airPollutionData.postValue(UiState(isLoading = true))
                val request = weatherRepository.getCurrentAirPollution()
                if (request.raw().code == 400) {
                    airPollutionData.postValue(UiState(error = "Niepoprawne współrzędne"))
                }
                if (request.isSuccessful) {
                    airPollutionData.postValue(UiState(isLoading = false))
                    val airPollutionResponse = request.body()
                    val air = AirPollutionResponse(list = airPollutionResponse?.list ?: emptyList())

                    airPollutionData.postValue(UiState(data = listOf(air)))
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Operacja nie powiodła się", e)
                airPollutionData.postValue(UiState(isLoading = false))
                airPollutionData.postValue(UiState(error = e.message))
            }
        }
    }
}