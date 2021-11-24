package com.aqua.hoophelper.match

enum class Court(val value: Double) {
    COURT_TOP(0.15),
    COURT_BOTTOM(0.57),
    THREE_LINE_LEFT(0.09),
    THREE_LINE_RIGHT(0.9),
    CORNER_BOTTOM_BOUND(0.167),
    ROUND_DEGREE(180.0),
    SLOPE_80(80.0),
    SLOPE_65(65.0),
    DIAMETER1(0.083),
    DIAMETER2(0.153),
    DIAMETER3(0.241),
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