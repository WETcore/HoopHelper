package com.aqua.hoophelper.live

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.database.Event
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource

class LiveViewModel : ViewModel() {

    // date
    private var _events = HoopRemoteDataSource.getEvents()
    val events: LiveData<List<Event>>
        get() = _events

    // TODO 區別動作
    fun filterEventType(event: Event): String {
        return when {
            event.assist -> {
                "assist"
            }
            event.block -> {
                "block"
            }
            event.foul -> {
                "foul"
            }
            event.rebound -> {
                "rebound"
            }
            event.steal -> {
                "steal"
            }
            event.turnover -> {
                "turnover"
            }
            event.score2 -> {
                "2pt"
            }
            event.score3 -> {
                "3pt"
            }
            event.freeThrow == true -> {
                "make a free throw"
            }
            event.freeThrow == false -> {
                "miss a free throw"
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