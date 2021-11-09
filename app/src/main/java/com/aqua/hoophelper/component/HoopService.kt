package com.aqua.hoophelper.component

import android.annotation.SuppressLint
import android.app.*
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
                Log.d("service","${it[0].id}")
                val intent1 = Intent(this, CheckService::class.java)
                intent1.putExtra("teamId",it.first().teamId)
                intent1.putExtra("inviteId",it.first().id)
                intent1.putExtra("mail",it.first().inviteeMail)
                intent1.putExtra("name",it.first().playerName)
                val serviceActionPendingIntent1 =
                    PendingIntent
                        .getService(this,
                            0,
                            intent1,
                            PendingIntent.FLAG_CANCEL_CURRENT)
                Log.d("service1","Hi1 ${intent1.getStringExtra("inviteId")}")
                val action1 = Notification.Action.Builder(R.drawable.ball_icon, "ACCEPT",serviceActionPendingIntent1)
                    .addRemoteInput(
                        RemoteInput.Builder("numKey")
                        .setLabel("Jersey Number")
                        .build()
                    )
                    .build()

                val intent2 = Intent(this, CheckService::class.java)
                intent2.putExtra("inviteId",it.first().id)
                val serviceActionPendingIntent2 =
                    PendingIntent
                        .getService(this,
                            0,
                            intent2,
                            PendingIntent.FLAG_CANCEL_CURRENT)
                val action2 = Notification.Action.Builder(R.drawable.ball_icon, "CANCEL",serviceActionPendingIntent2).build()

                val channel =
                    NotificationChannel("1", "Invite", NotificationManager.IMPORTANCE_HIGH)
                val notification = Notification.Builder(this, "1")
                    .setContentTitle("Hoooooop~")
                    .setContentText("Invitation")
                    .setSmallIcon(R.drawable.ball_icon)
                    .addAction(action1)
                    .addAction(action2)
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