package com.aqua.hoophelper.match

enum class ZoneMark(val value: String) {
    AROUND_RIM("A"),
    L_ELBOW("B"),
    MID_STR("C"),
    R_ELBOW("D"),
    L_BASELINE("E"),
    L_WING("F"),
    LONG_STR("G"),
    R_WING("H"),
    R_BASELINE("I"),
    L_CORNER("J"),
    L_3PT("K"),
    ARC("L"),
    R_3PT("M"),
    R_CORNER("N"),
    ELSE("O")
}

enum class EventType(val value: String) {
    AST("assist"),
    BLK("block"),
    FOUL("foul"),
    REB("rebound"),
    STL("steal"),
    TOV("turnover"),
    IN_2("2pt In"),
    OUT_2("2pt Out"),
    IN_3("3pt In"),
    OUT_3("3pt Out"),
    FT_IN("FT In"),
    FT_OUT("FT Out"),
    ELSE("else"),
    INIT("latest event")
}