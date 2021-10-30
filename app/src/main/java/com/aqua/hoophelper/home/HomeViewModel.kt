package com.aqua.hoophelper.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.HoopInfo
import com.aqua.hoophelper.database.Event
import com.aqua.hoophelper.database.Player
import com.aqua.hoophelper.database.Team
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource
import com.aqua.hoophelper.match.DataType
import kotlinx.coroutines.*

class HomeViewModel : ViewModel() {

    // 球隊名稱列表
    var teamNameList = mutableListOf<String>()

    // 球隊中球員數據
//    var teamStat = mutableListOf<MutableMap<String, String>>()
    private var _teamStat = MutableLiveData<MutableList<MutableMap<String, String>>>()
    val teamStat: LiveData<MutableList<MutableMap<String, String>>>
        get() = _teamStat


    // 球隊列表
    private var _teams = MutableLiveData<List<Team>>()
    val teams: LiveData<List<Team>>
        get() = _teams

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun getTeamList(): List<String> {
        coroutineScope.launch {
            _teams.value = HoopRemoteDataSource.getTeams()
            for (i in teams.value!!.indices) {
                teamNameList.add(teams.value!![i].name)
            }
            HoopInfo.spinnerSelectedTeamId = _teams.value!![0].id
        }
        return teamNameList
    }

    // 取得球員名單與數據
    fun selectedTeam(pos: Int) {
        HoopInfo.spinnerSelectedTeamId = _teams.value!![pos].id
        coroutineScope.launch {
            val players = HoopRemoteDataSource.getTeamMembers()
            getTeamPlayersData(players, players.size)
        }
    }

    private fun getTeamPlayersData(players: List<Player>, listSize: Int) {
        coroutineScope.launch {
            var data: List<Event>
            val bufferlist = mutableListOf<MutableMap<String, String>>()
            for (i in 0 until listSize) {
                data = HoopRemoteDataSource.getPlayerData(players[i].id)
                var playerStat = mutableMapOf(
                    "id" to "",
                    "num" to "00",
                    "assist" to "0",
                    "score" to "0",
                    "rebound" to "0",
                    "steal" to "0",
                    "block" to "0",
                )
                playerStat["id"] = players[i].id
                playerStat["num"] = players[i].number
                playerStat["score"] = data.filter { it.score2 == true }.size.toString()
                playerStat["assist"] = data.filter { it.assist }.size.toString()
                playerStat["rebound"] = data.filter { it.rebound }.size.toString()
                playerStat["steal"] = data.filter { it.steal }.size.toString()
                playerStat["block"] = data.filter { it.block }.size.toString()

//                Log.d("playerStat","${playerStat}")
                bufferlist.add(i, playerStat)
            }
            _teamStat.value = bufferlist
            Log.d("playerStat2","${_teamStat.value}")
//            var buffer = teamStat
//            buffer.sortByDescending { it["score"]?.toInt() }
//            Log.d("playerStat","${buffer}")
        }
    }

    fun getLeaderboardData(type: DataType): String? {
        when(type) {
            DataType.SCORE -> {
                teamStat.value?.sortByDescending { it["score"]?.toInt() }
                Log.d("playerStat","${teamStat.value?.get(0)?.get("score")}")
                return teamStat.value?.get(0)?.get("num") + " " + teamStat.value?.get(0)?.get("score")
            }
            DataType.ASSIST -> {
                teamStat.value?.sortByDescending { it["assist"]?.toInt() }
                return teamStat.value?.get(0)?.get("num") + " " + teamStat.value?.get(0)?.get("assist")
            }
            DataType.REBOUND -> {
                teamStat.value?.sortByDescending { it["rebound"]?.toInt() }
                return teamStat.value?.get(0)?.get("num") + " " + teamStat.value?.get(0)?.get("rebound")
            }
            DataType.STEAL -> {
                teamStat.value?.sortByDescending { it["steal"]?.toInt() }
                return teamStat.value?.get(0)?.get("num") + " " + teamStat.value?.get(0)?.get("steal")
            }
            DataType.BLOCK -> {
                teamStat.value?.sortByDescending { it["block"]?.toInt() }
                return teamStat.value?.get(0)?.get("num") + " " + teamStat.value?.get(0)?.get("block")
            }
            else -> return "else"
        }
    }




    ////////////////
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}