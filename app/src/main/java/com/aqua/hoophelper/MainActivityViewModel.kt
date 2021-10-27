package com.aqua.hoophelper

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.database.Event
import com.aqua.hoophelper.database.Match
import com.aqua.hoophelper.database.Player
import com.aqua.hoophelper.database.Team
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class MainActivityViewModel: ViewModel() {

    val db = FirebaseFirestore.getInstance()

    var match = Match()

    private val cal = Calendar.getInstance()

    var player = MutableLiveData<List<Player>>()
    var team = MutableLiveData<List<Team>>()
    var teamId = ""

    fun setMatchInfo() {
        match.gaming = true
        match.matchId = db.collection("Matches").document().id
        match.teamId = teamId
        match.date = cal.get(Calendar.DATE).toString()
        match.time = cal.get(Calendar.HOUR_OF_DAY).toString()
        match.actualTime = Calendar.getInstance().timeInMillis
    }

    fun getUserInfo() {
        db.collection("Players").whereEqualTo("email", User.account?.email).get().addOnCompleteListener {
            player.value = it.result?.toObjects(Player::class.java) ?: mutableListOf()
            teamId = player.value!![0].teamId
            getTeamInfo()
        }
    }

    fun getTeamInfo() {
        db.collection("Teams").whereEqualTo("id", teamId).get().addOnCompleteListener {
            team.value =it.result?.toObjects(Team::class.java) ?: mutableListOf()
        }
        getTeamMemberInfo()
    }

    fun getTeamMemberInfo() {
        db.collection("Players").whereEqualTo("teamId", teamId).get().addOnCompleteListener {
        }
    }

}