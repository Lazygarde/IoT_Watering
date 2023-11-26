package dev.lazygarde.watering.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.firebase.database.FirebaseDatabase

class ButtonReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        setWaterPump(true)
        //set notification to false
        val firebaseInstance = FirebaseDatabase.getInstance()
        val firebaseDatabase = firebaseInstance.getReference("notification")
        firebaseDatabase.setValue(false)
        //dismiss notification
        val notificationManager = p0?.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.cancel(1)
    }

    private fun setWaterPump(waterPump: Boolean) {
        val firebaseInstance = FirebaseDatabase.getInstance()
        val firebaseDatabase = firebaseInstance.getReference("water_pump")
        firebaseDatabase.setValue(waterPump)

    }

}