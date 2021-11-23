package com.aqua.hoophelper.util

import android.app.RemoteInput
import android.content.Intent
import androidx.lifecycle.LifecycleService
import com.aqua.hoophelper.database.Player
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class CheckService: LifecycleService() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        super.onStartCommand(intent, flags, startId)

        val player = Player()

        player.email = intent?.getStringExtra("mail") ?: ""
        player.id = intent?.getStringExtra("inviteId") ?: ""
        player.teamId = intent?.getStringExtra("teamId") ?: ""
        player.name = intent?.getStringExtra("name") ?: ""
        player.avatar = Firebase.auth.currentUser?.photoUrl.toString()

        val remoteInput = RemoteInput.getResultsFromIntent(intent)



        // check accept or cancel
        val db = FirebaseFirestore.getInstance()
        if (intent?.getBooleanExtra("toggle", false) == true) {
            if (remoteInput != null) {
                player.number = remoteInput.getCharSequence("numKey").toString()
                val numbers = intent.getSerializableExtra("numbers") as MutableSet<String>
                val bufferSize = numbers.size
                numbers.add(player.number)
                // check dual number
                if (numbers.size == bufferSize) {
                    val reIntent = Intent(applicationContext, HoopService::class.java)
                    reIntent.putExtra("dualNumber", true)
                    stopService(Intent(applicationContext, HoopService::class.java))
                    startService(reIntent)
                    stopSelf()
                } else {
                    if (player.id.length > 5) {
                        db.collection("Invitations")
                            .whereEqualTo("id", player.id)
                            .get().addOnCompleteListener {
                                db.collection("Players").add(player)
                                db.collection("Teams")
                                    .whereEqualTo("id", player.teamId)
                                    .get().addOnCompleteListener {
                                        it.result.documents.first().reference
                                        .update("jerseyNumbers",numbers.toList())
                                    }
                                it.result.documents.first().reference.delete()
                                stopService(Intent(applicationContext, HoopService::class.java))
                                stopSelf()
                            }
                    }
                }
            }
        } else {
            db.collection("Invitations")
                .whereEqualTo("id", player.id)
                .get().addOnCompleteListener {
                    it.result.documents.first().reference.delete()
                    stopService(Intent(applicationContext, HoopService::class.java))
                    stopSelf()
                }
        }

        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}