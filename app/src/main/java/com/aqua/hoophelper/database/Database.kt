package com.aqua.hoophelper.database

data class Team(
    var id: String = "",
    var name: String = "",
    var jerseyNumbers: MutableList<String> = mutableListOf(),
)

data class Invitation(
    var id: String = "",
    var teamId: String = "",
    var inviteeMail: String = "",
    var playerName: String = "",
    var existingNumbers: List<String> = listOf(),
)

data class Player(
    var id: String = "",
    var number: String = "",
    var name: String = "",
    var teamId: String = "",
    var email: String = "",
    var starting5: MutableList<Boolean> = mutableListOf(false, false, false, false, false),
    var captain: Boolean = false,
    var avatar: String = "",
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
    var playerName: String = "MJ",
    var playerImage: String = "",
    var matchTimeMin: String = "",
    var matchTimeSec: String = "",
    var quarter: String = "",
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

data class Rule(
    var quarter: String = "4",
    var gClock: String = "12",
    var sClock: String = "24",
    var foulOut: String = "6",
    var to1: String = "2",
    var to2: String = "3",
)

data class PlayerStat(
    var name: String = "",
    var number: String = "",
    var pts: Int = 0,
    var reb: Int = 0,
    var ast: Int = 0,
    var stl: Int = 0,
    var blk: Int = 0,
)
