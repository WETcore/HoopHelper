package com.aqua.hoophelper.live

enum class LiveMessage(val value: String) {
    AST("sent assist"),
    BLK("sent block"),
    FOUL("got foul"),
    REB("got rebound"),
    STL("got steal"),
    TOV("got turnover"),
    IN_2("made 2 points"),
    OUT_2("miss 2 points"),
    IN_3("made 3 points"),
    OUT_3("miss 3 points"),
    FT_IN("made a free throw"),
    FT_OUT("miss a free throw"),
    ELSE("else")
}