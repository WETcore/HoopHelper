package com.aqua.hoophelper.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

interface HoopRepository {

    fun getEvents(): LiveData<List<Event>>
    fun getMatches(): LiveData<List<Match>>
    fun getRoster(): MutableLiveData<List<Player>>
    fun getInvitations(): MutableLiveData<List<Invitation>>

    suspend fun getTeams(): Result<List<Team>>
    suspend fun getUserInfo(): List<Player>
    suspend fun getSelectedTeamMembers(): Result<List<Player>>
    suspend fun getMatchMembers(): Result<List<Player>>
    suspend fun getPlayerData(playerId: String): Result<List<Event>>
    suspend fun getRule(): Result<Rule>
    suspend fun getPlayer(): Result<Player>
    suspend fun getTeamInfo(): Team
    suspend fun getTeamEvents(): List<Event>

}