package com.aqua.hoophelper.database.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aqua.hoophelper.database.Event
import com.aqua.hoophelper.database.HoopRepository
import com.aqua.hoophelper.database.Team
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
                .addSnapshotListener { value, error ->
                    it.value = value?.toObjects(Event::class.java) ?: mutableListOf()
                }
        }

        return result
    }

    override suspend fun getTeams(): LiveData<List<Team>> = suspendCoroutine { conti ->
        val result = MutableLiveData<List<Team>>()
        var list = mutableListOf<String>()
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

}