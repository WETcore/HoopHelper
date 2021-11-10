package com.aqua.hoophelper.component

import android.app.RemoteInput
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleService
import com.aqua.hoophelper.database.Player
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

class CheckService: LifecycleService() {

    override fun onCreate() {
        super.onCreate()
        Log.d("service2", "OnCreate2")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        super.onStartCommand(intent, flags, startId)

        val player = Player()

        player.email = intent?.getStringExtra("mail") ?: "No"
        player.id = intent?.getStringExtra("inviteId") ?: "No"
        player.teamId = intent?.getStringExtra("teamId") ?: "No"
        player.name = intent?.getStringExtra("name") ?: "No"
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
//                    Log.d("service2", "Hi")
                    val reIntent = Intent(applicationContext, HoopService::class.java)
                    reIntent.putExtra("dualNumber", true)
                    stopService(Intent(applicationContext, HoopService::class.java))
                    startService(reIntent)
                    stopSelf()
                } else {
                    if (player.id.length > 5) {
                        Log.d("service2", "Hi2 ${player.id}")
                        db.collection("Invitations")
                            .whereEqualTo("id", player.id)
                            .get().addOnCompleteListener {
                                Log.d("service2", "Hi2---")
                                db.collection("Players").add(player)
                                it.result.documents.first().reference.delete()
                                Log.d("service2", "Hi2----")
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
                    Log.d("service2","Hi3---")
                    it.result.documents.first().reference.delete()
                    Log.d("service2","Hi3----")
                    stopService(Intent(applicationContext, HoopService::class.java))
                    stopSelf()
                }
        }



        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("service2", "OnDestroy2")
    }
}