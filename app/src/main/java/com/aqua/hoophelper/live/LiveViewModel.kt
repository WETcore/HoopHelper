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
                "Number" + event.playerNum + " sent " + "assist" + " in zone" + event.zone
            }
            event.block -> {
                "Number" + event.playerNum + " sent " + "block" + " in zone" + event.zone
            }
            event.foul -> {
                "Number" + event.playerNum + " got " + "foul" + " in zone" + event.zone
            }
            event.rebound -> {
                "Number" + event.playerNum + " got " + "rebound" + " in zone" + event.zone
            }
            event.steal -> {
                "Number" + event.playerNum + " got " + "steal" + " in zone" + event.zone
            }
            event.turnover -> {
                "Number" + event.playerNum + " got " + "turnover" + " in zone" + event.zone
            }
            event.score2 == true -> {
                "Number" + event.playerNum + " got " + "2 points" + " in zone" + event.zone
            }
            event.score2 == false -> {
                "Number" + event.playerNum + " miss " + "2 points" + " in zone" + event.zone
            }
            event.score3 == true -> {
                "Number" + event.playerNum + " got " + "3 points" + " in zone" + event.zone
            }
            event.score3 == false -> {
                "Number" + event.playerNum + " miss " + "3 points" + " in zone" + event.zone
            }
            event.freeThrow == true -> {
                "Number" + event.playerNum + " made a free throw"
            }
            event.freeThrow == false -> {
                "Number" + event.playerNum + " miss a free throw"
            }
            else -> "else"
        }
    }

    fun countFreeThrows(): String {
        return events.value?.filter {
            it.freeThrow == true
        }?.size.toString()
    }
}