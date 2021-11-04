package com.aqua.hoophelper

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.database.Event
import com.aqua.hoophelper.database.Match
import com.aqua.hoophelper.database.Player
import com.aqua.hoophelper.database.Team
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class MainActivityViewModel: ViewModel() {

    val db = FirebaseFirestore.getInstance()

    var match = Match()

    private val cal = Calendar.getInstance()

    var player = MutableLiveData<List<Player>>()
    var team = MutableLiveData<List<Team>>()

    private var _loginState = MutableLiveData<Boolean>()
    val loginState: LiveData<Boolean>
    get() = _loginState

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun setMatchInfo() {
        match.gaming = true
        match.matchId = db.collection("Matches").document().id
        HoopInfo.matchId = match.matchId
        match.teamId = User.teamId
        match.date = cal.get(Calendar.DATE).toString()
        match.time = cal.get(Calendar.HOUR_OF_DAY).toString()
        match.actualTime = Calendar.getInstance().timeInMillis
        db.collection("Matches").add(match)
    }

    fun getUserInfo() {
        coroutineScope.launch {
            HoopRemoteDataSource.getUserInfo()
        }
    }

    fun checkLogin(state: Any?) {
        _loginState.value = (state != null)
    }

    ////////////////
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}