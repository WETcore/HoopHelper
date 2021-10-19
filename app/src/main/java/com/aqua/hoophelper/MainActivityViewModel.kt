package com.aqua.hoophelper

import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.database.Match
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class MainActivityViewModel: ViewModel() {

    val db = FirebaseFirestore.getInstance()

    var match = Match()

    fun getMatchInfo() {
        val cal = Calendar.getInstance()
        match.date = cal.get(Calendar.DATE).toString()
        match.time = cal.get(Calendar.HOUR_OF_DAY).toString()
    }
}