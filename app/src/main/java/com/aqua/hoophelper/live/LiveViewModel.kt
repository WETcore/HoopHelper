package com.aqua.hoophelper.live

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.database.Event
import com.aqua.hoophelper.database.PlayerStat
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource
import com.aqua.hoophelper.util.HoopInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class LiveViewModel : ViewModel() {

    // date
    private var _events = HoopRemoteDataSource.getEvents()
    val events: LiveData<List<Event>>
        get() = _events

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    // recognize event
    fun filterEventType(event: Event): String {
        return when {
            event.assist -> {
                LiveMessage.AST.value
            }
            event.block -> {
                LiveMessage.BLK.value
            }
            event.foul -> {
                LiveMessage.FOUL.value
            }
            event.rebound -> {
                LiveMessage.REB.value
            }
            event.steal -> {
                LiveMessage.STL.value
            }
            event.turnover -> {
                LiveMessage.TOV.value
            }
            event.score2 == true -> {
                LiveMessage.IN_2.value
            }
            event.score2 == false -> {
                LiveMessage.OUT_2.value
            }
            event.score3 == true -> {
                LiveMessage.IN_3.value
            }
            event.score3 == false -> {
                LiveMessage.OUT_3.value
            }
            event.freeThrow == true -> {
                LiveMessage.FT_IN.value
            }
            event.freeThrow == false -> {
                LiveMessage.FT_OUT.value
            }
            else -> LiveMessage.ELSE.value
        }
    }

    fun getTeamPlayerData(id: String, events: List<Event>): PlayerStat {

        val playerEvents = events.filter { it.playerId == id }

        val ptI = playerEvents.filter { it.score2 == true }.size
        val pt3I = playerEvents.filter { it.score3 == true }.size
        val ftI = playerEvents.filter { it.freeThrow == true }.size

        return PlayerStat(
            "name",
            "num",
            pts = (ptI * 2 + pt3I * 3 + ftI),
            playerEvents.filter { it.rebound }.size,
            playerEvents.filter { it.assist }.size,
            playerEvents.filter { it.steal }.size,
            playerEvents.filter { it.block }.size,
        )
    }

    fun filterEvents(its: List<Event>) =
        its.filter {
            it.matchId == its.first().matchId
                    && it.teamId == HoopInfo.spinnerSelectedTeamId.value
        }

}