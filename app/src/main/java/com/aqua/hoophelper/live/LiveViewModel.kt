package com.aqua.hoophelper.live

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.database.Event
import com.aqua.hoophelper.database.PlayerStat
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource
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
                "sent " + "assist"
            }
            event.block -> {
                "sent " + "block"
            }
            event.foul -> {
                "got " + "foul"
            }
            event.rebound -> {
                "got " + "rebound"
            }
            event.steal -> {
                "got " + "steal"
            }
            event.turnover -> {
                "got " + "turnover"
            }
            event.score2 == true -> {
                "made " + "2 points"
            }
            event.score2 == false -> {
                "miss " + "2 points"
            }
            event.score3 == true -> {
                "made " + "3 points"
            }
            event.score3 == false -> {
                "miss " + "3 points"
            }
            event.freeThrow == true -> {
                "made a free throw"
            }
            event.freeThrow == false -> {
                "miss a free throw"
            }
            else -> "else"
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

}