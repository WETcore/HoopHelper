package com.aqua.hoophelper.util

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import androidx.lifecycle.LifecycleService
import com.aqua.hoophelper.R
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource
import java.io.Serializable

class HoopService : LifecycleService() {

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        HoopRemoteDataSource.getInvitations().observe(this) { invite ->
            if (!invite.isNullOrEmpty()) {
                val intent1 = Intent(applicationContext, CheckService::class.java)
                intent1.apply {
                    putExtra(TEAM_ID, invite.first().teamId)
                    putExtra(INVITE_ID, invite.first().id)
                    putExtra(MAIL, invite.first().inviteeMail)
                    putExtra(NAME, invite.first().playerName)
                    putExtra(
                        NUMBERS,
                        invite.first().existingNumbers.toMutableSet() as Serializable
                    )
                    putExtra(TOGGLE, true)
                }
                val serviceActionPendingIntent1 =
                    PendingIntent
                        .getService(
                            applicationContext,
                            1,
                            intent1,
                            PendingIntent.FLAG_CANCEL_CURRENT
                        )
                val action1 = Notification.Action.Builder(
                    R.drawable.ball_icon,
                    "ACCEPT",
                    serviceActionPendingIntent1
                )
                    .addRemoteInput(
                        RemoteInput.Builder("numKey")
                            .setLabel("Jersey Number")
                            .build()
                    )
                    .build()

                val intent2 = Intent(applicationContext, CheckService::class.java)
                intent2.putExtra(INVITE_ID, invite.first().id)
                intent2.putExtra(TOGGLE, false)
                val serviceActionPendingIntent2 =
                    PendingIntent
                        .getService(
                            applicationContext,
                            2,
                            intent2,
                            PendingIntent.FLAG_CANCEL_CURRENT
                        )
                val action2 = Notification.Action.Builder(
                    R.drawable.ball_icon,
                    "CANCEL",
                    serviceActionPendingIntent2
                ).build()

                val channel =
                    NotificationChannel("1", "Invite", NotificationManager.IMPORTANCE_HIGH)

                // check recall or not
                if (intent?.getBooleanExtra(DUAL_NUMBER, false) == false) {
                    val notification = Notification.Builder(applicationContext, "1")
                        .setContentTitle("Hoooooop~")
                        .setContentText("Invitation")
                        .setSmallIcon(R.drawable.ball_icon)
                        .addAction(action1)
                        .addAction(action2)
                        .setAutoCancel(true)
                        .build()
                    val manager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    manager.createNotificationChannel(channel)
                    manager.notify(1, notification)
                } else if (intent?.getBooleanExtra(DUAL_NUMBER, false) == true) {
                    var numbers = ""
                    for (i in invite.first().existingNumbers.indices) {
                        numbers += invite.first().existingNumbers[i] + " "
                    }
                    val notification = Notification.Builder(applicationContext, "1")
                        .setContentTitle("Hoooooop~")
                        .setContentText("Number existed!")
                        .setStyle(Notification.BigTextStyle().bigText(numbers))
                        .setSmallIcon(R.drawable.ball_icon)
                        .addAction(action1)
                        .addAction(action2)
                        .setAutoCancel(true)
                        .build()

                    val manager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    manager.createNotificationChannel(channel)
                    manager.notify(1, notification)
                }
            }
        }

        return START_NOT_STICKY
    }

}