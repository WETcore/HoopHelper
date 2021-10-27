package com.aqua.hoophelper.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.HoopInfo
import com.aqua.hoophelper.User
import com.aqua.hoophelper.database.Player
import com.aqua.hoophelper.database.Team
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    // mock team list
    var teamList = mutableListOf<String>()  //("Lakers","Nets")

    private var _teams = MutableLiveData<List<Team>>() //HoopRemoteDataSource.getTeams()
    val teams: LiveData<List<Team>>
        get() = _teams

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun setTeamList(): List<String> {
        coroutineScope.launch {
            _teams = HoopRemoteDataSource.getTeams() as MutableLiveData<List<Team>>
            for (i in teams.value!!.indices) {
                teamList.add(teams.value!![i].name)
            }
        }
        return teamList
    }

    fun selectedTeam(pos: Int) {
        HoopInfo.spinnerSelectedTeamId = _teams.value!![pos].id
    }




    ////////////////
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}