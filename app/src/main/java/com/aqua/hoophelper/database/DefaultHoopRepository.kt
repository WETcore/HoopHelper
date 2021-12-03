package com.aqua.hoophelper.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class DefaultHoopRepository(private val remoteDataSource: HoopDataSource
) : HoopRepository {
    override fun getEvents(): LiveData<List<Event>> {
        return remoteDataSource.getEvents()
    }

    override fun getMatches(teamId: String): LiveData<List<Match>> {
        return remoteDataSource.getMatches(teamId)
    }

    override fun getRoster(): MutableLiveData<List<Player>> {
        return remoteDataSource.getRoster()
    }

    override fun getInvitations(): MutableLiveData<List<Invitation>> {
        return remoteDataSource.getInvitations()
    }

    override suspend fun getTeams(): Result<List<Team>> {
        return remoteDataSource.getTeams()
    }

    override suspend fun getUserInfo(): List<Player> {
        return remoteDataSource.getUserInfo()
    }

    override suspend fun getSelectedTeamMembers(): Result<List<Player>> {
        return remoteDataSource.getSelectedTeamMembers()
    }

    override suspend fun getMatchMembers(): Result<List<Player>> {
        return remoteDataSource.getMatchMembers()
    }

    override suspend fun getPlayerData(playerId: String): Result<List<Event>> {
        return remoteDataSource.getPlayerData(playerId)
    }

    override suspend fun getRule(): Result<Rule> {
        return remoteDataSource.getRule()
    }

    override suspend fun getPlayer(): Result<Player> {
        return remoteDataSource.getPlayer()
    }

    override suspend fun getTeamInfo(): Result<Team> {
        return remoteDataSource.getTeamInfo()
    }

    override suspend fun getTeamEvents(): List<Event> {
        return remoteDataSource.getTeamEvents()
    }

    override suspend fun setTeamInfo(team: Team): Result<Boolean> {
        return remoteDataSource.setTeamInfo(team)
    }

    override suspend fun setCaptainInfo(player: Player): Result<Boolean> {
        return remoteDataSource.setCaptainInfo(player)
    }

    override suspend fun setMockTeammate(player: Player): Result<Boolean> {
        return remoteDataSource.setMockTeammate(player)
    }

    override suspend fun deletePlayer(player: Player): Result<Boolean> {
        return remoteDataSource.deletePlayer(player)
    }

    override suspend fun setInvitationInfo(invitation: Invitation): Result<Boolean> {
        return remoteDataSource.setInvitationInfo(invitation)
    }

    override suspend fun updateCaptain(playerId: String): Result<Boolean> {
        return remoteDataSource.updateCaptain(playerId)
    }

    override suspend fun updateJerseyNumbers(number: String): Result<Boolean> {
        return remoteDataSource.updateJerseyNumbers(number)
    }

    override suspend fun setEvent(event: Event): Result<Boolean> {
        return remoteDataSource.setEvent(event)
    }

    override suspend fun deleteEvent(): Result<Boolean> {
        return remoteDataSource.deleteEvent()
    }

    override suspend fun setRule(rule: Rule): Result<Boolean> {
        return remoteDataSource.setRule(rule)
    }

    override suspend fun updateLineup(
        subPlayer: Player,
        startPlayer: Player,
        position: Int
    ): Result<Boolean> {
        return remoteDataSource.updateLineup(subPlayer, startPlayer, position)
    }

    override suspend fun setMatchInfo(match: Match): Result<Boolean> {
        return remoteDataSource.setMatchInfo(match)
    }

    override suspend fun updateMatchStatus(match: Match): Result<Boolean> {
        return remoteDataSource.updateMatchStatus(match)
    }

}
