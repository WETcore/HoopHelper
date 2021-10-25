package com.aqua.hoophelper.database.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aqua.hoophelper.database.Event
import com.aqua.hoophelper.database.HoopRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

object HoopRemoteDataSource: HoopRepository {
    override fun getEvents(): LiveData<List<Event>> {
        val db = FirebaseFirestore.getInstance()
        val result = MutableLiveData<List<Event>>()
        result.let {
            db.collection("Events").orderBy("actualTime", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
                it.value = value?.toObjects(Event::class.java) ?: mutableListOf()
            }
        }
        return result
    }

}