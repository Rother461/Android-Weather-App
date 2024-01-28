package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.repository.models.WeatherItem
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class ForecastWeatherActivity : ComponentActivity() {
    private val viewModel: ForecastWeatherActivityModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getData()

        setContent {
            MyApplicationTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Showcase(viewModel) { finish() }
                }
            }
        }

    }
}

@Composable
fun Showcase(viewModel: ForecastWeatherActivityModel, onBack: () -> Unit) {
    val uiState by viewModel.immutableForecastedWeatherData.observeAsState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Button(onClick = onBack) {
                Text("Back")
            }
        }
        item {
            Text(
                text = "Prognozowana pogoda dla Krakowa",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val imageUrls = listOf(
                    "https://openweathermap.org/img/wn/09d@2x.png",
                    "https://openweathermap.org/img/wn/04d@2x.png",
                    "https://openweathermap.org/img/wn/13d@2x.png",
                    "https://openweathermap.org/img/wn/02d@2x.png"
                )

                items(imageUrls) { imageUrl ->
                    ImageItem(
                        imageUrl = imageUrl,
                        modifier = Modifier
                            .size(80.dp)
                            .padding(6.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        when {
            uiState?.isLoading == true -> {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(50.dp)
                            .padding(8.dp)
                    )
                }
            }

            uiState?.error != null -> {
                item {
                    Text(
                        text = "Błąd: ${uiState!!.error}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }

            else -> {
                val weatherData = uiState?.data ?: emptyList()
                items(weatherData) { weatherResponse ->
                    forecastWeatherItem(weatherResponse.list[0])
                }
            }
        }
    }
}

@Composable
fun forecastWeatherItem(weatherItem: WeatherItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Date: ${formatDate(weatherItem.dt)}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Main: ${weatherItem.weather[0].main}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Description: ${weatherItem.weather[0].description}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Temp: %.2f°C".format(calvinToCelcius(weatherItem.main.temp)),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Feels Like: %.2f°C".format(calvinToCelcius(weatherItem.main.feels_like)),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Humidity: ${weatherItem.main.humidity}%",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Pressure: ${weatherItem.main.pressure} hPa",
                style = MaterialTheme.typography.bodyLarge
            )

            val iconCode = weatherItem.weather[0].icon
            val imageUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
            ImageItem(
                imageUrl = imageUrl,
                modifier = Modifier
                    .size(100.dp)
                    .padding(6.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

fun formatDate(timestamp: Long): String {
    val instant = Instant.ofEpochSecond(timestamp)
    val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return localDateTime.format(formatter)
}