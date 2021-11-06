package com.aqua.hoophelper.component

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.aqua.hoophelper.MainActivity
import com.aqua.hoophelper.R
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource

class HoopService: LifecycleService() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        Log.d("service","Hi")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d("service","Hi2")
        HoopRemoteDataSource.getInvitations().observe(this) {
            if (!it.isNullOrEmpty()) {
//                Toast.makeText(this, "Hi", Toast.LENGTH_SHORT).show()
                val intent1 = Intent(this, CheckService::class.java)
                intent1.putExtra("teamId",it[0].teamId)
                val serviceActionPendingIntent1 =
                    PendingIntent
                        .getService(this,
                            0,
                            intent1,
                            PendingIntent.FLAG_UPDATE_CURRENT)

                val action1 = Notification.Action.Builder(R.drawable.ball_icon, "ACCEPT", serviceActionPendingIntent1).build()

                val channel =
                    NotificationChannel("1", "Invite", NotificationManager.IMPORTANCE_HIGH)
                val notification = Notification.Builder(this, "1")
                    .setContentTitle("Title")
                    .setContentText("invitation")
                    .setSmallIcon(R.drawable.ball_icon)
                    .setStyle(Notification.DecoratedCustomViewStyle())
                    .addAction(action1)
//                    .setCustomContentView(RemoteViews(this.packageName, R.layout.notify_layout))
                    .build()

                val manager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                manager.createNotificationChannel(channel)
                manager.notify(1, notification)
            }
        }


        return super.onStartCommand(intent, flags, startId)
    }
}