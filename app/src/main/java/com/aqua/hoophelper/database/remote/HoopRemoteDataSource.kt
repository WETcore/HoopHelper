package com.aqua.hoophelper.database.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aqua.hoophelper.HoopInfo
import com.aqua.hoophelper.User
import com.aqua.hoophelper.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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

    override suspend fun getTeams(): LiveData<List<Team>> = suspendCoroutine { conti ->
        val result = MutableLiveData<List<Team>>()
        result.let {
            FirebaseFirestore.getInstance()
                .collection("Teams")
                .get()
                .addOnCompleteListener { value ->
                    it.value = value.result?.toObjects(Team::class.java) ?: mutableListOf()

                    conti.resume(result)
                }
        }
    }

    override suspend fun getUserInfo(): LiveData<List<Player>> = suspendCoroutine { conti ->
        val result = MutableLiveData<List<Player>>()
        if (User.account != null) {
            result.let { its ->
                FirebaseFirestore.getInstance()
                    .collection("Players")
                    .whereEqualTo("email", User.account?.email)
                    .get()
                    .addOnCompleteListener {
                        its.value = it.result?.toObjects(Player::class.java) ?: mutableListOf()
                        User.teamId = its.value!![0].teamId

                        conti.resume(result)
                    }
            }
        }
    }

}