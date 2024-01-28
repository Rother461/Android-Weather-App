package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.myapplication.repository.models.AirPollutionResponse
import com.example.myapplication.repository.models.CurrentWeatherResponse
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.gson.Gson

class DetailsWeatherActivity : ComponentActivity() {
    private val viewModel: DetailsWeatherActivityModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getStringExtra("CUSTOM_ID")
        val gson = Gson()
        val jsonObj = gson.fromJson(id, CurrentWeatherResponse::class.java)
        setContent {
            MyApplicationTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    viewModel.getData()
                    Showcase(viewModel, jsonObj) {
                        finish()
                    }
                }
            }
        }
    }
}

@Composable
fun Showcase(
    viewModel: DetailsWeatherActivityModel, obj: CurrentWeatherResponse,
    onBack: () -> Unit
) {
    val uiState by viewModel.immutableAirPollutionData.observeAsState()

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
                text = "Szczegółowa pogoda dla Krakowa",
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
                        detailsWeatherItem(weatherItem = obj, air = currentWeather)
                    }
                }
            }
        }
    }
}

@Composable
fun detailsWeatherItem(weatherItem: CurrentWeatherResponse?, air: AirPollutionResponse) {
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
            ) {
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
                    text = "Pressure: ${weatherItem.main.pressure} hPa\n",
                    style = MaterialTheme.typography.bodyLarge
                )

                val airData = air.list[0].components


                val thresholds = mapOf(
                    "co" to 4400.0,
                    "no" to 40.0,
                    "no2" to 80.0,
                    "o3" to 60.0,
                    "so2" to 80.0,
                    "pm2_5" to 25.0,
                    "pm10" to 50.0,

                    )

                fun getBackgroundColor(value: Double, key: String): Color {
                    val threshold = thresholds[key] ?: return Color.Transparent
                    return if (value > threshold) Color.Red else Color.Green
                }

                listOf(
                    "co" to airData.co,
                    "no" to airData.no,
                    "no2" to airData.no2,
                    "o3" to airData.o3,
                    "so2" to airData.so2,
                    "pm2_5" to airData.pm2_5,
                    "pm10" to airData.pm10,
                ).forEachIndexed { index, (pollutant, value) ->
                    val backgroundColor = getBackgroundColor(value, pollutant)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(backgroundColor)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "${pollutant.uppercase()}: $value",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    if (index < thresholds.size - 1) {
                        Divider(color = Color.Gray, thickness = 1.dp)
                    }
                }

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
}

@Composable
fun Divider(color: Color, thickness: Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(thickness)
            .background(color)
    )
}
