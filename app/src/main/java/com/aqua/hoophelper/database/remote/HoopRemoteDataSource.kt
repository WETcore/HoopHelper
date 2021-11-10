package com.aqua.hoophelper.database.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aqua.hoophelper.HoopInfo
import com.aqua.hoophelper.User
import com.aqua.hoophelper.database.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object HoopRemoteDataSource: HoopRepository {

    override fun getEvents(): LiveData<List<Event>> {
        val result = MutableLiveData<List<Event>>()
        result.let {
            FirebaseFirestore.getInstance()
                .collection("Events")
                .orderBy("actualTime", Query.Direction.DESCENDING)
//                .whereEqualTo("teamId", HoopInfo.spinnerSelectedTeamId)
                .addSnapshotListener { value, error ->
                    it.value = value?.toObjects(Event::class.java) ?: mutableListOf()
                }
        }

        return result
    }

    override fun getMatches(): LiveData<List<Match>> {
        val result = MutableLiveData<List<Match>>()
        result.let {
            FirebaseFirestore.getInstance()
                .collection("Matches")
                .orderBy("actualTime", Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    it.value = value?.toObjects(Match::class.java) ?: mutableListOf()
                }
        }

        return result
    }

    override fun getRoster(): MutableLiveData<List<Player>> {
        val result = MutableLiveData<List<Player>>()
        result.let {
            FirebaseFirestore.getInstance()
                .collection("Players")
                .whereEqualTo("teamId", User.teamId)
                .addSnapshotListener { value, error ->
                    it.value = value?.toObjects(Player::class.java) ?: mutableListOf()
                }
        }
        return result
    }

    override fun getInvitations(): MutableLiveData<List<Invitation>> {
        val result = MutableLiveData<List<Invitation>>()
        result.let {
            FirebaseFirestore.getInstance()
                .collection("Invitations")
                .whereEqualTo("inviteeMail", Firebase.auth.currentUser?.email)
                .addSnapshotListener { value, error ->
                    it.value = value?.toObjects(Invitation::class.java) ?: mutableListOf()
                }
        }
        return result
    }

    // captain participate the match. find captain's member
    override suspend fun getMatchMembers(): List<Player> = suspendCoroutine { conti ->
        val db = FirebaseFirestore.getInstance()
        db.collection("Players")
            .whereEqualTo("teamId", User.teamId)
            .get()
            .addOnCompleteListener { value ->
                val result = value.result?.toObjects(Player::class.java) ?: mutableListOf()
                User.teamMembers = result
//                Log.d("roster","${User.teamMembers.value}")
                conti.resume(result)
            }
    }


    override suspend fun getTeams(): List<Team> = suspendCoroutine { conti ->
        FirebaseFirestore.getInstance()
            .collection("Teams")
            .get()
            .addOnCompleteListener { value ->
                val result = value.result?.toObjects(Team::class.java) ?: mutableListOf()
                conti.resume(result)
            }
    }

    override suspend fun getTeamMembers(): List<Player> = suspendCoroutine { conti ->
        FirebaseFirestore.getInstance()
            .collection("Players")
            .whereEqualTo("teamId", HoopInfo.spinnerSelectedTeamId.value!!)
            .get()
            .addOnCompleteListener { value ->
                val result = value.result?.toObjects(Player::class.java) ?: mutableListOf()
                conti.resume(result)
            }
    }

    // TODO get playerId to get event
    override suspend fun getPlayerData(playerId: String): List<Event> = suspendCoroutine { conti ->
        FirebaseFirestore.getInstance()
            .collection("Events")
            .whereEqualTo("playerId", playerId)
            .get()
            .addOnCompleteListener { value ->
                val result = value.result?.toObjects(Event::class.java) ?: mutableListOf()
                conti.resume(result)
            }
    }

    override suspend fun getUserInfo(): List<Player> = suspendCoroutine { conti ->
        if (User.account != null) {
            FirebaseFirestore.getInstance()
                .collection("Players")
                .whereEqualTo("email", User.account?.email)
                .get()
                .addOnCompleteListener {
                    val result = it.result?.toObjects(Player::class.java) ?: mutableListOf()
                    if (result.size != 0) {
                        User.teamId = result.first().teamId
                        User.isCaptain = result.first().captain
                        User.id = result.first().id
                    }
                    Log.d("userInfo","${User.account?.email}")
                    conti.resume(result)
                }
        }
    }

    override suspend fun getTeamInfo(): Team = suspendCoroutine { conti ->
        if (User.account != null) {
            FirebaseFirestore.getInstance()
                .collection("Teams")
                .whereEqualTo("id", User.teamId)
                .get()
                .addOnCompleteListener {
                    val result = it.result?.toObjects(Team::class.java)?.first() ?: Team()
                    conti.resume(result)
                }
        }
    }

    override suspend fun getRule(): Rule = suspendCoroutine { conti ->
        FirebaseFirestore.getInstance()
            .collection("Rule")
            .document("rule")
            .get()
            .addOnCompleteListener {
                val result = it.result?.toObject(Rule::class.java) ?: Rule()
                conti.resume(result)
            }
    }

    override suspend fun getPlayer(): Player = suspendCoroutine { conti ->
        FirebaseFirestore.getInstance()
            .collection("Players")
            .whereEqualTo("id", User.id)
            .get()
            .addOnCompleteListener {
                val result = it.result.first().toObject(Player::class.java)
                conti.resume(result)
            }
    }

}