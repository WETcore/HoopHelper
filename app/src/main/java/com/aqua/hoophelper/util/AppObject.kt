package com.aqua.hoophelper.util

import androidx.lifecycle.MutableLiveData
import com.aqua.hoophelper.database.Player
import com.google.firebase.auth.FirebaseUser

object User {
    var account: FirebaseUser? = null
    var teamId = ""
    var teamMembers = listOf<Player>()
    var isCaptain = false
    var id = ""
    var teamJerseyNumbers = listOf<String>()
}

object HoopInfo {
    var spinnerSelectedTeamId = MutableLiveData("")
    var matchId = ""
}

object Tutor {
    val finished = MutableLiveData(true)
}

object Tactic {
    val vPagerSwipe = MutableLiveData(true)
}

object Arrow {
    var wave = 1f
    var isDash = false
    var isCurl = false
    var isScreen = false
}