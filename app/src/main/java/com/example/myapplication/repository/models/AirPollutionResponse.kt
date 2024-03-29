package com.example.myapplication.repository.models

data class AirPollutionResponse(
    val list: List<AirPollutionData>
)

data class AirPollutionData(
    val components: Component
)

data class Component(
    val co: Double,
    val no: Double,
    val no2: Double,
    val o3: Double,
    val so2: Double,
    val pm2_5: Double,
    val pm10: Double,
    val nh3: Double
)