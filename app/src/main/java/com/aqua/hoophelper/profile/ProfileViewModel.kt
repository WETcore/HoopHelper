package com.aqua.hoophelper.profile

import android.content.Intent
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.User
import com.aqua.hoophelper.database.Event
import com.aqua.hoophelper.database.Player
import com.aqua.hoophelper.database.Team
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.firestore.FirebaseFirestore

const val RC_SIGN_IN = 0

class ProfileViewModel : ViewModel() {

    // Firebase
    val db = FirebaseFirestore.getInstance()

    var player = Player()
    var team = Team()

    val result = MutableLiveData<List<Player>>()

    fun signIn(googleSignInClient: GoogleSignInClient, activity: FragmentActivity) {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(activity, signInIntent, RC_SIGN_IN, null)
    }

    fun sendTeamInfo(teamName: String) {
        team.name = teamName
        team.id = db.collection("Teams").document().id
        db.collection("Teams").add(team)
    }

    fun sendCaptainInfo(number: String) {
        player.email = User.account?.email ?: "error"
        player.id = db.collection("Players").document().id
        player.teamId = team.id
        player.number = number
        db.collection("Players").add(player)
    }

}