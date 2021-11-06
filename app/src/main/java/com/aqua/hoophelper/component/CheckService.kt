package com.aqua.hoophelper.component

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import com.aqua.hoophelper.User
import com.aqua.hoophelper.database.Player
import com.google.firebase.firestore.FirebaseFirestore

class CheckService: Service() {
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Toast.makeText(this, "Hi2", Toast.LENGTH_SHORT).show()

        val player = Player()

        player.teamId = intent?.getStringExtra("teamId") ?: "No"

        player.number = 11.toString()

        FirebaseFirestore.getInstance()
            .collection("Players")
            .add(player)

        FirebaseFirestore.getInstance()
            .collection("Invitations")
            .whereEqualTo("inviteeMail", User.account?.email)
            .get().addOnSuccessListener {
                it.documents[0].reference.delete()
            }

        return super.onStartCommand(intent, flags, startId)
    }
}