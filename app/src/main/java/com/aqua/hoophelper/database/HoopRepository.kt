package com.aqua.hoophelper.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

interface HoopRepository {

    fun getEvents(): LiveData<List<Event>>
    fun getMatches(): LiveData<List<Match>>
    fun getRoster(): MutableLiveData<List<Player>>
    fun getInvitations(): MutableLiveData<List<Invitation>>

    suspend fun getTeams(): List<Team>
    suspend fun getUserInfo(): List<Player>
    suspend fun getTeamMembers(): List<Player>
    suspend fun getMatchMembers(): List<Player>
    suspend fun getPlayerData(playerId: String): List<Event>
    suspend fun getRule(): Rule
    suspend fun getPlayer(): Player

}