package dev.lazygarde.watering.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.lazygarde.watering.section.sensordata.SensorDataModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _sensorDataList = MutableStateFlow(listOf<SensorDataModel>())
    val sensorDataList = _sensorDataList.asStateFlow()

    private val _auto = MutableStateFlow(false)
    val auto = _auto.asStateFlow()

    private val _waterPump = MutableStateFlow(false)
    val waterPump = _waterPump.asStateFlow()

    init {
        val firebaseInstance = FirebaseDatabase.getInstance()
        val firebaseDatabase = firebaseInstance.getReference("sensor")
        firebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newList = mutableListOf<SensorDataModel>()
                for (childSnapshot in snapshot.children) {
                    try {
                        val design = childSnapshot.getValue(SensorDataModel::class.java)
                        design?.let {
                            newList.add(it)
                        }
                    } catch (e: Exception) {
                        Log.e("MainViewModel", e.toString())
                    }

                }
                viewModelScope.launch {
                    _sensorDataList.emit(newList)
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