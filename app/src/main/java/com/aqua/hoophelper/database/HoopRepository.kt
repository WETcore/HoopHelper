package com.aqua.hoophelper.database

import androidx.lifecycle.LiveData

interface HoopRepository {

    fun getEvents(): LiveData<List<Event>>

    suspend fun getTeams(): LiveData<List<Team>>
}