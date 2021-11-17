package com.aqua.hoophelper.team.child.tactic

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.database.Player
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TacticViewModel : ViewModel() {

    var avatarNum = 1

    // roster
    private var _roster = MutableLiveData<List<Player>>()
    val roster: LiveData<List<Player>>
        get() = _roster

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

    fun setRoster() {
        coroutineScope.launch {
            val starPlayerList = mutableListOf<Player>()
            _roster.value = HoopRemoteDataSource.getMatchMembers()
            val lineUp = _roster.value!!
            lineUp.filter { it.starting5.contains(true) }.forEachIndexed { index, player ->
                starPlayerList.add(player)
            }
            starPlayerList.sortBy { it.starting5.indexOf(true) }
            _startPlayer.value = starPlayerList
//            Log.d("subPlayer4", "${_startPlayer.value}")
        }
    }
}

object Tactic {
    val vPagerSwipe = MutableLiveData(true)
}