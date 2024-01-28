package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.repository.models.CurrentWeatherResponse
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.gson.Gson


class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getData()

        setContent {
            MyApplicationTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Showcase(
                        viewModel,
                        onClick = { id -> navigateToDetailsWeatherActivity(id) },
                        onForecast = {
                            navigateToForecastWeatherActivity()
                        })
                }
            }
        }
    }

    fun navigateToDetailsWeatherActivity(id: CurrentWeatherResponse) {
        val detailsIntent = Intent(this, DetailsWeatherActivity::class.java)
        val gson = Gson()
        val jsonString = gson.toJson(id)
        detailsIntent.putExtra("CUSTOM_ID", jsonString)
        startActivity(detailsIntent)
    }

    fun navigateToForecastWeatherActivity() {
        val forecastIntent = Intent(this, ForecastWeatherActivity::class.java)
        startActivity(forecastIntent)
    }
}

@Composable
fun Showcase(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onClick: (CurrentWeatherResponse) -> Unit,
    onForecast: () -> Unit
) {
    val uiState by viewModel.immutableWeatherData.observeAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Button(onClick = onForecast) {
                Text("Forecast")
            }
        }
        item {
            Text(
                text = "Obecna pogoda dla Krakowa",
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
                val currentWeathers = uiState?.data
                currentWeathers?.let {
                    items(currentWeathers) { currentWeather ->
                        WeatherItem(
                            weatherItem = currentWeather,
                            onClick = { id -> onClick.invoke(id) })
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherItem(weatherItem: CurrentWeatherResponse?, onClick: (CurrentWeatherResponse) -> Unit) {
    if (weatherItem != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { onClick.invoke(weatherItem) }
            ) {
                Text(
                    text = "Description: ${weatherItem.weather[0].description}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Temp: %.2f°C".format(calvinToCelcius(weatherItem.main.temp)),
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
                Text(
                    text = "Click to show details",
                    color = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp)
                )

            }
        }
    }
}

fun calvinToCelcius(temp: Float): Float {
    return temp - 273.15f
}


@Composable
fun ImageItem(imageUrl: String, modifier: Modifier = Modifier) {
    AsyncImage(
        model = imageUrl,
        contentDescription = "Weather Icon",
        modifier = modifier
    )
}



