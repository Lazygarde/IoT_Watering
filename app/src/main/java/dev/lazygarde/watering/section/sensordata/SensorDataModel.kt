package dev.lazygarde.watering.section.sensordata

import com.google.errorprone.annotations.Keep

@Keep
data class SensorDataModel(
    val id: Long = 0,
    val time: Long = 0,
    val temperature: Double = 0.0,
    val humidity: Double = 0.0,
)
