package com.aqua.hoophelper

import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.database.Match
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class MainActivityViewModel: ViewModel() {

    val db = FirebaseFirestore.getInstance()

    var match = Match()

    val cal = Calendar.getInstance()

    fun getMatchInfo() {
        match.matchId = db.collection("Matches").document().id
        match.date = cal.get(Calendar.DATE).toString()
        match.time = cal.get(Calendar.HOUR_OF_DAY).toString()
    }
}