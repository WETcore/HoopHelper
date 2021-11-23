package com.aqua.hoophelper.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.HoopInfo
import com.aqua.hoophelper.database.*
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource
import com.aqua.hoophelper.match.DataType
import com.aqua.hoophelper.match.DetailDataType
import com.aqua.hoophelper.util.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    val leaderTypes = listOf("Score", "Rebound", "Assist", "Steal", "Block")

    val _status = MutableLiveData<LoadApiStatus?>()
    val status: LiveData<LoadApiStatus?>
        get() = _status

    // team name list
    var teamNameList = mutableListOf<String>()

    // stat of team player
    private var _teamStat = MutableLiveData<MutableList<PlayerStat>>(mutableListOf())
    val teamStat: LiveData<MutableList<PlayerStat>>
        get() = _teamStat


    // team list
    private var _teams = MutableLiveData<List<Team>>()
    val teams: LiveData<List<Team>>
        get() = _teams

    // player list
    private var _teamPlayers = MutableLiveData(listOf(Player()))
    private val teamPlayers: LiveData<List<Player>>
        get() = _teamPlayers

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getTeamList()
    }

    private fun getTeamList(): List<String> {
        _status.value = LoadApiStatus.LOADING
        coroutineScope.launch {
            when (val result = HoopRemoteDataSource.getTeams()) {
                is Result.Success -> {
                    _teams.value = result.data.filter { it.jerseyNumbers.size >= 5 }
                    for (i in teams.value!!.indices) {
                        teamNameList.add(teams.value!![i].name)
                    }
                    HoopInfo.spinnerSelectedTeamId.value = teams.value!!.first().id
                    selectedTeam(0)
                }
                is Result.Error -> {
                    Log.d("status", "error")
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
        return teamNameList
    }

    // get player list
    fun selectedTeam(pos: Int) {
        _status.value = LoadApiStatus.LOADING
        HoopInfo.spinnerSelectedTeamId.value = teams.value!![pos].id
        coroutineScope.launch {
            when (val result = HoopRemoteDataSource.getSelectedTeamMembers()) {
                is Result.Success -> {
                    _teamPlayers.value = result.data
                    getTeamPlayersData(teamPlayers.value!!, teamPlayers.value!!.size)
                }
                is Result.Error -> {
                    Log.d("status", "error")
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }

    private fun getTeamPlayersData(players: List<Player>, listSize: Int) {
        coroutineScope.launch {
            var data: List<Event>
            var playerStat: PlayerStat
            val playerStats = mutableListOf<PlayerStat>()
            for (i in 0 until listSize) {
                when (val result = HoopRemoteDataSource.getPlayerData(players[i].id)) {
                    is Result.Success -> {
                        data = result.data
                        val ptI = data.filter { it.score2 == true }.size
                        val pt3I = data.filter { it.score3 == true }.size
                        val ftI = data.filter { it.freeThrow == true }.size
                        playerStat = PlayerStat(
                            players[i].name,
                            players[i].number,
                            pts = (ptI * 2 + pt3I * 3 + ftI),
                            data.filter { it.rebound }.size,
                            data.filter { it.assist }.size,
                            data.filter { it.steal }.size,
                            data.filter { it.block }.size,
                        )
                        playerStats.add(playerStat)
                        _teamStat.value = playerStats
                    }
                    is Result.Error -> {
                        _status.value = LoadApiStatus.ERROR
                    }
                }
            }


        }
    }

    fun getLeaderMainData(type: DataType): Pair<String, String> {
        return when (type) {
            DataType.SCORE -> {
                teamStat.value?.sortByDescending { it.pts }
                val leaderMainData = teamStat.value?.get(0)
                Pair(leaderMainData?.name ?: "", (leaderMainData?.pts ?: 0).toString())
            }
            DataType.ASSIST -> {
                teamStat.value?.sortByDescending { it.ast }
                val leaderMainData = teamStat.value?.get(0)
                Pair(leaderMainData?.name ?: "", (leaderMainData?.ast ?: 0).toString())
            }
            DataType.REBOUND -> {
                teamStat.value?.sortByDescending { it.reb }
                val leaderMainData = teamStat.value?.get(0)
                Pair(leaderMainData?.name ?: "", (leaderMainData?.reb ?: 0).toString())
            }
            DataType.STEAL -> {
                teamStat.value?.sortByDescending { it.stl }
                val leaderMainData = teamStat.value?.get(0)
                Pair(leaderMainData?.name ?: "", (leaderMainData?.stl ?: 0).toString())
            }
            DataType.BLOCK -> {
                teamStat.value?.sortByDescending { it.blk }
                val leaderMainData = teamStat.value?.get(0)
                Pair(leaderMainData?.name ?: "", (leaderMainData?.blk ?: 0).toString())
            }
            else -> Pair("", "else")
        }
    }

    fun getLeaderDetailData(detailDataType: DetailDataType, dataType: DataType): String {
        when (dataType) {
            DataType.SCORE -> {
                teamStat.value?.sortByDescending { it.pts }
            }
            DataType.ASSIST -> {
                teamStat.value?.sortByDescending { it.ast }
            }
            DataType.REBOUND -> {
                teamStat.value?.sortByDescending { it.reb }
            }
            DataType.STEAL -> {
                teamStat.value?.sortByDescending { it.stl }
            }
            DataType.BLOCK -> {
                teamStat.value?.sortByDescending { it.blk }
            }
            else -> teamStat.value
        }

        return when (detailDataType) {
            DetailDataType.PTS -> {
                (teamStat.value?.get(0)?.pts ?: 0).toString()
            }
            DetailDataType.AST -> {
                (teamStat.value?.get(0)?.ast ?: 0).toString()
            }
            DetailDataType.REB -> {
                (teamStat.value?.get(0)?.reb ?: 0).toString()
            }
            DetailDataType.STL -> {
                (teamStat.value?.get(0)?.stl ?: 0).toString()
            }
            DetailDataType.BLK -> {
                (teamStat.value?.get(0)?.blk ?: 0).toString()
            }
            else -> ""
        }
    }

    ////////////////
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}