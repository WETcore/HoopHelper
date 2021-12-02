package com.aqua.hoophelper.team.child.manage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.database.Player
import com.aqua.hoophelper.database.Result
import com.aqua.hoophelper.database.Rule
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource
import com.aqua.hoophelper.util.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ManageViewModel : ViewModel() {

    private val _status = MutableLiveData<LoadApiStatus?>()
    val status: LiveData<LoadApiStatus?>
        get() = _status

    var rule = Rule()

    // startingPlayer
    private var _startPlayer = MutableLiveData<MutableList<Player>>(mutableListOf())
    val startPlayer: LiveData<MutableList<Player>>
        get() = _startPlayer

    // substitutionPlayer
    private var _substitutionPlayer = MutableLiveData<MutableList<Player>>(mutableListOf())
    val substitutionPlayer: LiveData<MutableList<Player>>
        get() = _substitutionPlayer

    var subNum = mutableListOf<String>()

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        setRoster()
    }

    fun setRule(
        quarter: String,
        gameClock: String,
        shotClock: String,
        foulOut: String,
        turnover1: String,
        turnover2: String
    ) {
        rule.apply {
            this.quarter = quarter
            gClock = gameClock
            sClock = shotClock
            this.foulOut = foulOut
            to1 = turnover1
            to2 = turnover2
        }

        coroutineScope.launch {
            when(val result = HoopRemoteDataSource.setRule(rule)) {
                is Result.Success -> {
                }
                is Result.Error -> {
                    Log.d("status", "error")
                }
            }
        }
    }
    private fun setRoster() {
        _status.value = LoadApiStatus.LOADING
        coroutineScope.launch {
            val starPlayers = mutableListOf<Player>()
            val subPlayers = mutableListOf<Player>()
            when(val result = HoopRemoteDataSource.getMatchMembers()) {
                is Result.Success -> {
                    val lineUp = result.data
                    lineUp.filter { !it.starting5.contains(true) }.forEachIndexed { index, player ->
                        _substitutionPlayer.value?.add(player)
                        subPlayers.add(player)
                    }
                    lineUp.filter { it.starting5.contains(true) }.forEachIndexed { index, player ->
                        starPlayers.add(player)
                    }
                    starPlayers.sortBy { it.starting5.indexOf(true) }
                    _startPlayer.value = starPlayers
                    _substitutionPlayer.value = subPlayers

                    _status.value = LoadApiStatus.DONE
                }
                is Result.Error -> {
                    Log.d("status", "error")
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }

    fun switchLineUp(spinnerPos: Int, pos: Int) {
        val subPlayer = substitutionPlayer.value?.get(spinnerPos) ?: Player()
        val startPlayer = startPlayer.value?.get(pos) ?: Player()

        coroutineScope.launch {
            when(val result = HoopRemoteDataSource.updateLineup(subPlayer, startPlayer, pos)) {
                is Result.Success -> {
                    setRoster()
                }
                is Result.Error -> {
                    Log.d("status", "error")
                }
            }
        }
    }
}