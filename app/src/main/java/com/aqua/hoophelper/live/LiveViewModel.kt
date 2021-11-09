package com.aqua.hoophelper.live

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.database.Event
import com.aqua.hoophelper.database.Player
import com.aqua.hoophelper.database.PlayerStat
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LiveViewModel : ViewModel() {

    // date
    private var _events = HoopRemoteDataSource.getEvents()
    val events: LiveData<List<Event>>
        get() = _events

    // 球員數據
    private var _playerStat = MutableLiveData<PlayerStat>(PlayerStat())
    val playerStat: LiveData<PlayerStat>
        get() = _playerStat

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    // 區別動作
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

        val buffer = events.filter { it.playerId == id }

        val ast = buffer.filter { it.assist }.size
        val ptI = buffer.filter { it.score2 == true }.size
        val ptO = buffer.filter { it.score2 == false }.size
        val pt3I = buffer.filter { it.score3 == true }.size
        val pt3O = buffer.filter { it.score3 == false }.size
        val reb = buffer.filter { it.rebound }.size
        val stl = buffer.filter { it.steal }.size
        val blk = buffer.filter { it.block }.size
        val ftI = buffer.filter { it.freeThrow == true }.size
        val ftO = buffer.filter { it.freeThrow == false }.size
        val tov = buffer.filter { it.turnover }.size
        val foul = buffer.filter { it.foul }.size

        return PlayerStat(
            "name",
            "num",
            pts = (ptI*2 + pt3I*3 + ftI),
            reb,
            ast,
            stl,
            blk,
        )
    }

}