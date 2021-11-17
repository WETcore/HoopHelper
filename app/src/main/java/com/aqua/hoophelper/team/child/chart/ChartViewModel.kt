package com.aqua.hoophelper.team.child.chart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.database.Event
import com.aqua.hoophelper.database.Match
import com.aqua.hoophelper.database.Player
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ChartViewModel : ViewModel() {

    private val _roster = MutableLiveData<List<Player>>()
    val roster: LiveData<List<Player>>
    get() = _roster

    private val _matches = HoopRemoteDataSource.getMatches()
    val matches: LiveData<List<Match>>
        get() = _matches

    private val _events = HoopRemoteDataSource.getEvents()
    val events: LiveData<List<Event>>
        get() = _events

    var playerList = listOf<String>("")

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getChartData()
    }

    private fun getChartData() {
        coroutineScope.launch {
            _roster.value = HoopRemoteDataSource.getMatchMembers()
        }
    }

    fun transData(): List<Event>? {
        return events.value?.filter { it.playerId == roster.value?.first()?.id }
    }

    ////////////////
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}