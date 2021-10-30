package com.aqua.hoophelper.database

import androidx.lifecycle.LiveData

interface HoopRepository {

    fun getEvents(): LiveData<List<Event>>
    fun getMatches(): LiveData<List<Match>>

    suspend fun getTeams(): List<Team>
    suspend fun getUserInfo(): List<Player>
    suspend fun getTeamMembers(): List<Player>
    suspend fun getMatchMembers(): List<Player>
    suspend fun getPlayerData(playerId: String): List<Event>
}