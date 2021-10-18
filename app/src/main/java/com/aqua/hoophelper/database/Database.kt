package com.aqua.hoophelper.database

data class Team(
    var id: String = "",
    var name: String = "",
)

data class Invitation(
    var teamId: String = "",
    var playerId: String = "",
    )

data class Player(
    var id: String = "",
    var name: String = "",
    var teamId: String = "",
)

data class Match(
    var matchId: String = "",
    var teamId: String = "",
    var date: String = "",
    var time: String = "",
    var currentGameClock: String = "",
    var currentShotClock: String = "",
    var currentQuarter: String = "",
    var RemainingTimeout: String = "",
    )


data class Event(
    var playerId: String = "",
    var time: String = "",
    var zone: MutableMap<Int, Boolean?> = mutableMapOf(),

)
