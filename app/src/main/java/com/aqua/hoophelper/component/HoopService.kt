package com.aqua.hoophelper.component

import android.annotation.SuppressLint
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
import kotlinx.coroutines.coroutineScope

class HoopService: LifecycleService() {

    override fun onCreate() {
        super.onCreate()
        Log.d("service", "OnCreate1")
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        super.onStartCommand(intent, flags, startId)

        HoopRemoteDataSource.getInvitations().observe(this) {
            if (!it.isNullOrEmpty()) {
//                Log.d("service","${it[0].id}")
                val intent1 = Intent(this, CheckService::class.java)
                intent1.putExtra("teamId",it[0].teamId)
                intent1.putExtra("inviteId",it[0].id)
                intent1.putExtra("mail",it[0].inviteeMail)
                val serviceActionPendingIntent1 =
                    PendingIntent
                        .getService(this,
                            0,
                            intent1,
                            PendingIntent.FLAG_CANCEL_CURRENT)

                Log.d("service1","Hi1 ${intent1.getStringExtra("inviteId")}")

                val action1 = Notification.Action.Builder(R.drawable.ball_icon, "ACCEPT", serviceActionPendingIntent1).build()

                val channel =
                    NotificationChannel("1", "Invite", NotificationManager.IMPORTANCE_HIGH)
                val notification = Notification.Builder(this, "1")
                    .setContentTitle("Title")
                    .setContentText("invitation")
                    .setSmallIcon(R.drawable.ball_icon)
                    .addAction(action1)
//                    .setCustomContentView(RemoteViews(this.packageName, R.layout.notify_layout))
                    .setAutoCancel(true)
                    .build()

                val manager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                manager.createNotificationChannel(channel)
                manager.notify(1, notification)
//                Log.d("service1","Hi3 ${intent1.getStringExtra("inviteId")}")
            }
        }


        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("service", "OnDestroy1")
    }
}