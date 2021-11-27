package com.aqua.hoophelper.team.child.chart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.database.Event
import com.aqua.hoophelper.database.Player
import com.aqua.hoophelper.database.Result
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource
import com.aqua.hoophelper.util.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ChartViewModel : ViewModel() {

    val _status = MutableLiveData<LoadApiStatus?>()
    val status: LiveData<LoadApiStatus?>
        get() = _status

    private val _roster = MutableLiveData<List<Player>>()
    val roster: LiveData<List<Player>>
    get() = _roster

    private val _events = MutableLiveData<List<Event>>()
    private val events: LiveData<List<Event>>
        get() = _events

    private val _selectedPlayerData = MutableLiveData<List<Event>>()
    val selectedPlayerData: LiveData<List<Event>>
        get() = _selectedPlayerData

    // spinner
    var players = mutableListOf<String>()

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getChartData()
    }

    private fun getChartData() {
        _status.value = LoadApiStatus.LOADING
        coroutineScope.launch {
            when(val result = HoopRemoteDataSource.getMatchMembers()) {
                is Result.Success -> {
                    _roster.value = result.data
                    getPlayerStats(roster.value?.first()?.id ?: "")
                }
                is Result.Error -> {
                    Log.d("status", "error")
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }

    fun getPlayerStats(id: String) {
        coroutineScope.launch {
            _events.value = HoopRemoteDataSource.getTeamEvents()
            _selectedPlayerData.value = events.value?.filter { it.playerId == id }
        }
    }

    ////////////////
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}