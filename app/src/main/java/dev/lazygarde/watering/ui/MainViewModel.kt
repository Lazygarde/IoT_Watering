package dev.lazygarde.watering.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.lazygarde.watering.section.sensordata.SensorDataModel
import dev.lazygarde.watering.section.weather.RetrofitHelper
import dev.lazygarde.watering.section.weather.WeatherApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _sensorDataList = MutableStateFlow(listOf<SensorDataModel>())
    val sensorDataList = _sensorDataList.asStateFlow()

    private val _sensorData = MutableStateFlow(SensorDataModel())
    val sensorData = _sensorData.asStateFlow()

    private val _auto = MutableStateFlow(false)
    val auto = _auto.asStateFlow()

    private val _waterPump = MutableStateFlow(false)
    val waterPump = _waterPump.asStateFlow()

    private val _weatherStatus = MutableStateFlow("")
    val weatherStatus = _weatherStatus.asStateFlow()

    init {

        val api = RetrofitHelper.getInstance().create(WeatherApi::class.java)
        val firebaseInstance = FirebaseDatabase.getInstance()
        val firebaseDatabase = firebaseInstance.getReference("sensor")
        firebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val temperature = snapshot.child("temperature").getValue(Double::class.java)
                val humidity = snapshot.child("humidity").getValue(Double::class.java)
                val soilMoisture = snapshot.child("soil_moisture").getValue(Double::class.java)
                val sensor = SensorDataModel(
                    temperature = temperature ?: 0.0,
                    humidity = humidity ?: 0.0,
                    soilMoisture = soilMoisture ?: 0.0,
                )
                viewModelScope.launch {
                    _sensorData.emit(sensor)
                    _sensorDataList.emit(_sensorDataList.value + listOf(sensor))
                }

                viewModelScope.launch {
                    try {
                        val weather = withContext(Dispatchers.IO) {
                            api.getWeather(
                                tempmax = getMaxTemperature(),
                                tempmin = getMinTemperature(),
                                humidity = sensor.humidity,
                                temp = sensor.temperature,
                            )
                        }

                        _weatherStatus.emit(weather.result)
                    }
                    catch (e: Exception) {
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        val firebaseDatabaseAuto = firebaseInstance.getReference("auto")
        firebaseDatabaseAuto.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val auto = snapshot.getValue(Boolean::class.java)
                viewModelScope.launch {
                    _auto.emit(auto ?: false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        val firebaseDatabaseWaterPump = firebaseInstance.getReference("water_pump")
        firebaseDatabaseWaterPump.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val waterPump = snapshot.getValue(Boolean::class.java)
                viewModelScope.launch {
                    _waterPump.emit(waterPump ?: false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun getMinTemperature(): Double {
        return sensorDataList.value.minOf { it.temperature }
    }

    fun getMaxTemperature(): Double {
        return sensorDataList.value.maxOf { it.temperature }
    }


    fun getAnswerFromSpeech(text: String): String {
        val lowerCaseText = text.toLowerCase()
        return when {
            lowerCaseText.contains("turn on") -> {
                when {
                    lowerCaseText.contains("auto") -> {
                        setAuto(true)
                        "Auto mode is on"
                    }

                    lowerCaseText.contains("water pump") -> {
                        setWaterPump(true)
                        "Water pump is on"
                    }

                    else -> {
                        "I don't understand"
                    }
                }
            }

            lowerCaseText.contains("turn off") -> {
                when {
                    lowerCaseText.contains("auto") -> {
                        setAuto(false)
                        "Auto mode is off"
                    }

                    lowerCaseText.contains("water pump") -> {
                        setWaterPump(false)
                        "Water pump is off"
                    }

                    else -> {
                        "I don't understand"
                    }
                }
            }

            lowerCaseText.contains("temperature") -> {
                "The temperature is ${sensorData.value.temperature} degree celsius"
            }

            lowerCaseText.contains("humidity") -> {
                "The humidity is ${sensorData.value.humidity} percent"
            }

            lowerCaseText.contains("soil moisture") -> {
                "The soil moisture is ${sensorData.value.soilMoisture} percent"
            }

            lowerCaseText.contains("auto") -> {
                "Auto mode is ${auto.value}"
            }

            lowerCaseText.contains("water pump") -> {
                "Water pump is ${waterPump.value}"
            }

            else -> {
                "I don't understand"
            }
        }
    }

    fun setAuto(auto: Boolean) {
        val firebaseInstance = FirebaseDatabase.getInstance()
        val firebaseDatabase = firebaseInstance.getReference("auto")
        firebaseDatabase.setValue(auto)
    }

    fun setWaterPump(waterPump: Boolean) {
        val firebaseInstance = FirebaseDatabase.getInstance()
        val firebaseDatabase = firebaseInstance.getReference("water_pump")
        firebaseDatabase.setValue(waterPump)
    }

    fun getLatestSensorData(): SensorDataModel? {
        return sensorDataList.value.lastOrNull()
    }

    fun addSensorData(sensorDataModel: SensorDataModel) {
        val firebaseInstance = FirebaseDatabase.getInstance()
        val firebaseDatabase = firebaseInstance.getReference("sensor")
        firebaseDatabase.push().setValue(sensorDataModel)
    }

    fun clearData() {
        val firebaseInstance = FirebaseDatabase.getInstance()
        val firebaseDatabase = firebaseInstance.getReference("sensor")
        firebaseDatabase.removeValue()
    }
}