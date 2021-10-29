package com.aqua.hoophelper.database

data class Team(
    var id: String = "",
    var name: String = "",
    var jerseyNumber: String = "",
)

data class Invitation(
    var teamId: String = "",
    var playerId: String = "",
    )

data class Player(
    var id: String = "",
    var number: String = "",
    var teamId: String = "",
    var email: String = "",
    var starting5: Boolean = false,
)

data class Match(
    var matchId: String = "",
    var teamId: String = "",
    var date: String = "",
    var time: String = "",
    var gaming: Boolean = false,
    var actualTime: Long = 0L,
    )


data class Event(
    var teamId: String = "",
    var playerId: String = "",
    var matchId: String = "",
    var eventId: String = "",
    var actualTime: Long = 0L,
    var playerNum: String = "0",
    var matchTimeMin: String = "",
    var matchTimeSec: String = "",
    var zone: Int = 0,
    var score2: Boolean? = null,
    var score3: Boolean? = null,
    var rebound: Boolean = false,
    var assist: Boolean = false,
    var steal: Boolean = false,
    var block: Boolean = false,
    var turnover: Boolean = false,
    var foul: Boolean = false,
    var freeThrow: Boolean? = null,
    )
