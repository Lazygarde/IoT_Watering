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
    }
}