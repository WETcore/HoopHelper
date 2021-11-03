package com.aqua.hoophelper.live

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.database.Event
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource

class LiveViewModel : ViewModel() {

    // date
    private var _events = HoopRemoteDataSource.getEvents()
    val events: LiveData<List<Event>>
        get() = _events


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

}