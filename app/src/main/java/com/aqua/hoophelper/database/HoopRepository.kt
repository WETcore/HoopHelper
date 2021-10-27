package com.aqua.hoophelper.database

import androidx.lifecycle.LiveData

interface HoopRepository {

    fun getEvents(): LiveData<List<Event>>
    fun getMatches(): LiveData<List<Match>>

    suspend fun getTeams(): LiveData<List<Team>>
    suspend fun getUserInfo(): LiveData<List<Player>>
}