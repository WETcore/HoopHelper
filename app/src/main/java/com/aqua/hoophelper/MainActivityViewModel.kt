package com.aqua.hoophelper

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.database.*
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource
import com.aqua.hoophelper.util.HoopInfo
import com.aqua.hoophelper.util.LoadApiStatus
import com.aqua.hoophelper.util.MATCHES
import com.aqua.hoophelper.util.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class MainActivityViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    var match = Match()

    private var _matches = MutableLiveData<List<Match>>()
    val matches: LiveData<List<Match>>
        get() = _matches

    private val cal = Calendar.getInstance()

    var player = MutableLiveData<List<Player>>()
    var team = MutableLiveData<List<Team>>()

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun setMatchInfo() {
        match.apply {
            gaming = true
            teamId = User.teamId
            date = cal.get(Calendar.DATE).toString()
            time = cal.get(Calendar.HOUR_OF_DAY).toString()
            actualTime = Calendar.getInstance().timeInMillis
        }
        HoopInfo.matchId = match.matchId

        coroutineScope.launch {
            when(val result = HoopRemoteDataSource.setMatchInfo(match)) {
                is Result.Success -> {
                }
                is Result.Error -> {
                    Log.d("status", "error")
                }
            }
        }
    }

    val badgeSwitch = MutableLiveData<Boolean>(false)

    fun showBadge(teamId: String) {
        badgeSwitch.let {
//            it.value = if (matches.value.isNullOrEmpty()) {
//                false
//            } else {
//                (matches.value ?: listOf()).lastOrNull { it.teamId == teamId }?.gaming == true
//            }

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
        db.collection(MATCHES) //TODO
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