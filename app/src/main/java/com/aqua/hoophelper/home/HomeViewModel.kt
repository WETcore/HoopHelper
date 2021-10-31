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
import com.aqua.hoophelper.match.DetailDataType
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
                    "score2I" to "0",
                    "score2O" to "0",
                    "score3I" to "0",
                    "score3O" to "0",
                    "rebound" to "0",
                    "steal" to "0",
                    "block" to "0",
                    "ftI" to "0",
                    "ftO" to "0",
                    "tov" to "0",
                    "foul" to "0",

                    )
                playerStat["id"] = players[i].id
                playerStat["num"] = players[i].number
                playerStat["assist"] = data.filter { it.assist }.size.toString()
                playerStat["score2I"] = (data.filter { it.score2 == true }.size).toString()
                playerStat["score2O"] = (data.filter { it.score2 == false }.size).toString()
                playerStat["score3I"] = (data.filter { it.score3 == true }.size).toString()
                playerStat["score3O"] = (data.filter { it.score3 == false }.size).toString()
                playerStat["rebound"] = data.filter { it.rebound }.size.toString()
                playerStat["steal"] = data.filter { it.steal }.size.toString()
                playerStat["block"] = data.filter { it.block }.size.toString()
                playerStat["ftI"] = data.filter { it.freeThrow == true }.size.toString()
                playerStat["ftO"] = data.filter { it.freeThrow == false }.size.toString()
                playerStat["tov"] = data.filter { it.turnover }.size.toString()
                playerStat["foul"] = data.filter { it.foul }.size.toString()

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

    fun getLeaderboardData(type: DataType): String {
        when(type) {
            DataType.SCORE -> {
                teamStat.value?.sortByDescending {
                    ((it["score2I"]?.toInt()?.times(2))
                        ?.plus(it["score3I"]?.toInt()?.times(3) ?: 0)) }

//                Log.d("playerStat","${teamStat.value?.get(0)?.get("score2I")}")
                val pts = ((teamStat.value?.get(0)?.get("score2I")?.toInt()?.times(2))
                    ?.plus(teamStat.value?.get(0)?.get("score3I")?.toInt()?.times(3) ?: 0))

                return "Number" + teamStat.value?.get(0)?.get("num") + " gets " + pts
            }
            DataType.ASSIST -> {
                teamStat.value?.sortByDescending { it["assist"]?.toInt() }
                return "Number" + teamStat.value?.get(0)?.get("num") + " gets " + teamStat.value?.get(0)?.get("assist")
            }
            DataType.REBOUND -> {
                teamStat.value?.sortByDescending { it["rebound"]?.toInt() }
                return "Number" + teamStat.value?.get(0)?.get("num") + " gets " + teamStat.value?.get(0)?.get("rebound")
            }
            DataType.STEAL -> {
                teamStat.value?.sortByDescending { it["steal"]?.toInt() }
                return "Number" + teamStat.value?.get(0)?.get("num") + " gets " + teamStat.value?.get(0)?.get("steal")
            }
            DataType.BLOCK -> {
                teamStat.value?.sortByDescending { it["block"]?.toInt() }
                return "Number" + teamStat.value?.get(0)?.get("num") + " gets " + teamStat.value?.get(0)?.get("block")
            }
            else -> return "else"
        }
    }


    var leaderData = mutableListOf<String>()
    fun getLeaderDetailData(leaderType: DataType, type: DetailDataType): String {
        var leader = mutableMapOf<String, String>()
        leader = when(leaderType) {
            DataType.SCORE -> {
                teamStat.value?.sortByDescending { ((it["score2I"]?.toInt()?.times(2))
                    ?.plus(it["score3I"]?.toInt()?.times(3) ?: 0)) }
                teamStat.value?.get(0)!!
            }
            DataType.ASSIST -> {
                teamStat.value?.sortByDescending { it["assist"]?.toInt() }
                teamStat.value?.get(0)!!
            }
            DataType.REBOUND -> {
                teamStat.value?.sortByDescending { it["rebound"]?.toInt() }
                teamStat.value?.get(0)!!
            }
            DataType.STEAL -> {
                teamStat.value?.sortByDescending { it["steal"]?.toInt() }
                teamStat.value?.get(0)!!
            }
            DataType.BLOCK -> {
                teamStat.value?.sortByDescending { it["block"]?.toInt() }
                teamStat.value?.get(0)!!
            }
            else ->  mutableMapOf<String, String>()
        }

        return when(type) {
            DetailDataType.PTS -> {
                ((leader["score2I"]?.toInt()?.times(2))
                    ?.plus(leader["score3I"]?.toInt()?.times(3) ?: 0)).toString()
            }
            DetailDataType.FG -> {
                try {
                    val buffer = (((leader["score2I"]?.toDouble() ?: 0.0) + (leader["score3I"]?.toDouble() ?: 0.0)) /
                            ((leader["score2I"]?.toDouble() ?: 0.0) + (leader["score3I"]?.toDouble() ?: 0.0) +
                                    (leader["score2O"]?.toDouble() ?: 0.0) + (leader["score3O"]?.toDouble() ?: 1.0)))
                        String.format("%.2f", buffer)
                } catch (e: ArithmeticException) {
                    "0"
                }
            }
            DetailDataType.ThreeP -> {
                try {
                    val buffer = ((leader["score3I"]?.toDouble() ?: 0.0) /
                            ((leader["score3I"]?.toDouble() ?: 0.0) +
                                    (leader["score3O"]?.toDouble() ?: 1.0)))
                    String.format("%.2f", buffer)
                } catch (e: ArithmeticException) {
                    "0"
                }
            }
            DetailDataType.FT -> {
                try {
                    val buffer = ((leader["ftI"]?.toDouble() ?: 0.0) /
                            ((leader["ftI"]?.toDouble() ?: 0.0) +
                                    (leader["ftO"]?.toDouble() ?: 1.0)))
                    String.format("%.2f", buffer)
                } catch (e: ArithmeticException) {
                    "0"
                }

            }
            DetailDataType.TOV -> {
                leader["TOV"] ?: "0"
            }
            DetailDataType.REB -> {
                leader["rebound"] ?: "0"
            }
            DetailDataType.AST -> {
                leader["assist"] ?: "0"
            }
            DetailDataType.STL -> {
                leader["steal"] ?: "0"
            }
            DetailDataType.BLK -> {
                leader["block"] ?: "0"
            }
            DetailDataType.PF -> {
                leader["foul"] ?: "0"
            }
        }
    }




    ////////////////
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}