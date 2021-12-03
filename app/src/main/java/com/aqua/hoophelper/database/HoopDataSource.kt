package com.aqua.hoophelper.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

interface HoopDataSource {

    fun getEvents(): LiveData<List<Event>>
    fun getMatches(teamId: String): LiveData<List<Match>>
    fun getRoster(): MutableLiveData<List<Player>>
    fun getInvitations(): MutableLiveData<List<Invitation>>

    suspend fun getTeams(): Result<List<Team>>
    suspend fun getUserInfo(): List<Player>
    suspend fun getSelectedTeamMembers(): Result<List<Player>>
    suspend fun getMatchMembers(): Result<List<Player>>
    suspend fun getPlayerData(playerId: String): Result<List<Event>>
    suspend fun getRule(): Result<Rule>
    suspend fun getPlayer(): Result<Player>
    suspend fun getTeamInfo(): Result<Team>
    suspend fun getTeamEvents(): List<Event>
    suspend fun setTeamInfo(team: Team): Result<Boolean>
    suspend fun setCaptainInfo(player: Player): Result<Boolean>
    suspend fun setMockTeammate(player: Player): Result<Boolean>
    suspend fun deletePlayer(player: Player): Result<Boolean>
    suspend fun setInvitationInfo(invitation: Invitation): Result<Boolean>
    suspend fun updateCaptain(playerId: String): Result<Boolean>
    suspend fun updateJerseyNumbers(number: String): Result<Boolean>
    suspend fun setEvent(event: Event): Result<Boolean>
    suspend fun deleteEvent(): Result<Boolean>
    suspend fun setRule(rule: Rule): Result<Boolean>
    suspend fun updateLineup(subPlayer: Player, startPlayer: Player, position: Int): Result<Boolean>
    suspend fun setMatchInfo(match: Match): Result<Boolean>
    suspend fun updateMatchStatus(match: Match): Result<Boolean>

}