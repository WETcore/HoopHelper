package com.aqua.hoophelper.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.HoopInfo
import com.aqua.hoophelper.database.Event
import com.aqua.hoophelper.database.Player
import com.aqua.hoophelper.database.PlayerStat
import com.aqua.hoophelper.database.Team
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource
import com.aqua.hoophelper.match.DataType
import com.aqua.hoophelper.match.DetailDataType
import kotlinx.coroutines.*

class HomeViewModel : ViewModel() {

    // 球隊名稱列表
    var teamNameList = mutableListOf<String>()

    // 球隊中球員數據
    private var _teamStat = MutableLiveData<MutableList<PlayerStat>>(mutableListOf())
    val teamStat: LiveData<MutableList<PlayerStat>>
        get() = _teamStat


    // 球隊列表
    private var _teams = MutableLiveData<List<Team>>()
    val teams: LiveData<List<Team>>
        get() = _teams

    // 球員列表
    private var _teamPlayers = MutableLiveData<List<Player>>(listOf(Player()))
    val teamPlayers: LiveData<List<Player>>
        get() = _teamPlayers

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getTeamList()
    }

    fun getTeamList(): List<String> {
        coroutineScope.launch {
            _teams.value = HoopRemoteDataSource.getTeams()
            for (i in teams.value!!.indices) {
                teamNameList.add(teams.value!![i].name)
            }
            HoopInfo.spinnerSelectedTeamId.value = teams.value!!.first().id
            selectedTeam(0)
        }
        return teamNameList
    }

    // 取得球員名單與數據
    fun selectedTeam(pos: Int) {
        HoopInfo.spinnerSelectedTeamId.value = teams.value!![pos].id
        coroutineScope.launch {
            _teamPlayers.value = HoopRemoteDataSource.getSelectedTeamMembers()
            getTeamPlayersData(teamPlayers.value!!, teamPlayers.value!!.size)
        }
    }

    private fun getTeamPlayersData(players: List<Player>, listSize: Int) {
        coroutineScope.launch {
            var data: List<Event>
            var playerStat = PlayerStat()
            val playerStats = mutableListOf<PlayerStat>()
            for (i in 0 until listSize) {
                data = HoopRemoteDataSource.getPlayerData(players[i].id)

                val id = players[i].id
                val num = players[i].number
                val name = players[i].name
                val ast = data.filter { it.assist }.size
                val ptI = data.filter { it.score2 == true }.size
                val ptO = data.filter { it.score2 == false }.size
                val pt3I = data.filter { it.score3 == true }.size
                val pt3O = data.filter { it.score3 == false }.size
                val reb = data.filter { it.rebound }.size
                val stl = data.filter { it.steal }.size
                val blk = data.filter { it.block }.size
                val ftI = data.filter { it.freeThrow == true }.size
                val ftO = data.filter { it.freeThrow == false }.size
                val tov = data.filter { it.turnover }.size
                val foul = data.filter { it.foul }.size

                playerStat = PlayerStat(
                    name,
                    num,
                    pts = (ptI*2 + pt3I*3 + ftI),
                    reb,
                    ast,
                    stl,
                    blk,
                )

                playerStats.add(playerStat)

            }
            _teamStat.value = playerStats
            Log.d("redo","${teamStat.value}")
        }
    }

    fun getLeaderMainData(type: DataType): Pair<String, String> {
        return when(type) {
            DataType.SCORE -> {
                teamStat.value?.sortByDescending {
                    it.pts
                }
                Log.d("stat", "${teamStat.value?.get(0)}")
                val leaderMainData = teamStat.value?.get(0)
                Pair(leaderMainData?.name ?: "MJ", (leaderMainData?.pts ?: 0).toString())
            }
            DataType.ASSIST -> {
                teamStat.value?.sortByDescending { it.ast }
                val leaderMainData = teamStat.value?.get(0)
                Pair(leaderMainData?.name ?: "MJ", (leaderMainData?.ast ?: 0).toString())
            }
            DataType.REBOUND -> {
                teamStat.value?.sortByDescending { it.reb }
                val leaderMainData = teamStat.value?.get(0)
                Pair(leaderMainData?.name ?: "MJ", (leaderMainData?.reb ?: 0).toString())
            }
            DataType.STEAL -> {
                teamStat.value?.sortByDescending { it.stl }
                val leaderMainData = teamStat.value?.get(0)
                Pair(leaderMainData?.name ?: "MJ", (leaderMainData?.stl ?: 0).toString())
            }
            DataType.BLOCK -> {
                teamStat.value?.sortByDescending { it.blk }
                val leaderMainData = teamStat.value?.get(0)
                Pair(leaderMainData?.name ?: "MJ", (leaderMainData?.blk ?: 0).toString())
            }
            else -> Pair("MJ","else")
        }
    }

    fun getLeaderDetailData(detailDataType: DetailDataType, dataType: DataType): String {
        when(dataType) {
            DataType.SCORE -> {
                teamStat.value?.sortByDescending {
                    it.pts
                }
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

        return when(detailDataType) {
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