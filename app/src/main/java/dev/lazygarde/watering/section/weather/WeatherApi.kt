package dev.lazygarde.watering.section.weather

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("/")
    suspend fun getWeather(
        @Query("tempmax") tempmax: Double = 0.0,
        @Query("tempmin") tempmin: Double = 0.0,
        @Query("humidity") humidity: Double = 0.0,
        @Query("temp") temp: Double = 0.0,
    ): WeatherResponse
}