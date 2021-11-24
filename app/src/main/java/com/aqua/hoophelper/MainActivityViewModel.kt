package com.aqua.hoophelper

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.database.Match
import com.aqua.hoophelper.database.Player
import com.aqua.hoophelper.database.Team
import com.aqua.hoophelper.util.MATCHES
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.util.*

class MainActivityViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    var match = Match()

    private val cal = Calendar.getInstance()

    var player = MutableLiveData<List<Player>>()
    var team = MutableLiveData<List<Team>>()

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun setMatchInfo() {
        match.gaming = true
        match.matchId = db.collection(MATCHES).document().id
        HoopInfo.matchId = match.matchId
        match.teamId = User.teamId
        match.date = cal.get(Calendar.DATE).toString()
        match.time = cal.get(Calendar.HOUR_OF_DAY).toString()
        match.actualTime = Calendar.getInstance().timeInMillis
        db.collection(MATCHES).add(match)
    }

    val badgeSwitch = MutableLiveData<Boolean>(false)

    fun showBadge(teamId: String) {
        badgeSwitch.let {
            db.collection(MATCHES) // TODO to model
                .addSnapshotListener { value, error ->
                    val matches = value?.toObjects(Match::class.java)?.sortedBy { it.actualTime }
                    it.value = if (matches.isNullOrEmpty()) {
                        false
                    } else {
                        matches.lastOrNull { it.teamId == teamId }?.gaming == true
                    }
                }
        }
    }

    fun exitMatch() {
        db.collection(MATCHES)
            .whereEqualTo("matchId", match.matchId)
            .get()
            .addOnSuccessListener {
                it.forEach {
                    it.reference.update("gaming", false)
                }
            }
    }

    ////////////////
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}