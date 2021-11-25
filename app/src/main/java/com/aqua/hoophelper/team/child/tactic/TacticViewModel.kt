package com.aqua.hoophelper.team.child.tactic

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.database.Player
import com.aqua.hoophelper.database.Result
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource
import com.aqua.hoophelper.util.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TacticViewModel : ViewModel() {

    private val _status = MutableLiveData<LoadApiStatus?>()
    val status: LiveData<LoadApiStatus?>
        get() = _status

    var avatarNum = 1

    // startingPlayer
    private var _startPlayer = MutableLiveData<MutableList<Player>>(mutableListOf())
    val startPlayer: LiveData<MutableList<Player>>
        get() = _startPlayer

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        setRoster()
    }

    private fun setRoster() {
        _status.value = LoadApiStatus.LOADING
        coroutineScope.launch {
            val starPlayers = mutableListOf<Player>()
            when(val result = HoopRemoteDataSource.getMatchMembers()) {
                is Result.Success -> {
                    val lineUp = result.data
                    lineUp.filter { it.starting5.contains(true) }.forEachIndexed { index, player ->
                        starPlayers.add(player)
                    }
                    starPlayers.sortBy { it.starting5.indexOf(true) }
                    _startPlayer.value = starPlayers
                    _status.value = LoadApiStatus.DONE
                }
                is Result.Error -> {
                    Log.d("status", "error")
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }
}