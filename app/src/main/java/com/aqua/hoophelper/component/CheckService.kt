package com.aqua.hoophelper.component

import android.app.RemoteInput
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleService
import com.aqua.hoophelper.User
import com.aqua.hoophelper.database.Player
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class CheckService: LifecycleService() {

    override fun onCreate() {
        super.onCreate()
        Log.d("service", "OnCreate2")
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

        if (remoteInput != null) {
            player.number = remoteInput.getCharSequence("numKey").toString()
        }

        if (player.teamId.length > 5) {
            Log.d("service1","Hi2 ${player.id}")
            FirebaseFirestore.getInstance()
                .collection("Invitations")
                .whereEqualTo("id", player.id)
                .get().addOnCompleteListener {
                    Log.d("service1","Hi2---")
                    FirebaseFirestore.getInstance()
                        .collection("Players")
                        .add(player)
                    it.result.documents.first().reference.delete()
                    Log.d("service1","Hi2----")
                    stopSelf()
                }
        } else if (player.id.length > 5) {
            Log.d("service1","Hi3----")
            FirebaseFirestore.getInstance()
                .collection("Invitations")
                .whereEqualTo("id", player.id)
                .get().addOnCompleteListener {
                    it.result.documents.first().reference.delete()
                    stopSelf()
                }
        }
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("service", "OnDestroy2")
    }
}