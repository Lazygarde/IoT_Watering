package dev.lazygarde.watering.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dev.lazygarde.watering.R

class WateringService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val fireBaseInstance = FirebaseDatabase.getInstance().getReference("notification")
        fireBaseInstance.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val notification = snapshot.getValue(Boolean::class.java)
                if (notification == true){
                    val notificationLayout = RemoteViews(packageName, R.layout.custom_notification)
                    val buttonIntent = Intent(this@WateringService, ButtonReceiver::class.java)
                    val buttonPendingIntent = PendingIntent.getBroadcast(this@WateringService, 0, buttonIntent,  PendingIntent.FLAG_MUTABLE)
                    notificationLayout.setOnClickPendingIntent(R.id.click, buttonPendingIntent)
                    val notification = NotificationCompat.Builder(this@WateringService, "channelId")
                        .setSmallIcon(R.drawable.ic_logo)
                        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                        .setCustomContentView(notificationLayout)
                        .build()
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(1, notification)
                    startForeground(1, notification)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        return START_NOT_STICKY
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("channelId", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}