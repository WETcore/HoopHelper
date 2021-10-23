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
    fun filterEventType(pos: Int) {
        events.value?.forEachIndexed { index, event ->
        }
    }

    fun countFreeThrows(): String {
        return events.value?.filter {
            it.freeThrow == true
        }?.size.toString()
    }
}