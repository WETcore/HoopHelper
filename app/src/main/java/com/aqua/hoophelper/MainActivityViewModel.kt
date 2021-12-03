package com.aqua.hoophelper

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.database.*
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource
import com.aqua.hoophelper.util.HoopInfo
import com.aqua.hoophelper.util.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class MainActivityViewModel(private val repository: HoopRepository) : ViewModel() {

    var match = Match()

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
            when(val result = repository.setMatchInfo(match)) {
                is Result.Success -> {
                }
                is Result.Error -> {
                    Log.d("status", "error")
                }
            }
        }
    }

    fun showBadge(teamId: String) {
        repository.getMatches(teamId)
    }

    fun exitMatch() {
        coroutineScope.launch {
            when(val result = repository.updateMatchStatus(match)) {
                is Result.Success -> {
                }
                is Result.Error -> {
                    Log.d("status", "error")
                }
            }
        }
    }

    ////////////////
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}