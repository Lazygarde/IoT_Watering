package dev.lazygarde.watering.section.sensordata

import com.google.errorprone.annotations.Keep

@Keep
data class SensorDataModel(
    val time: Long = 0,
    val temperature: Double = 0.0,
    val humidity: Double = 0.0,
    val soilMoisture: Double = 0.0,
)
