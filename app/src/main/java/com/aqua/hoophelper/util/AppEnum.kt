package com.aqua.hoophelper.util

enum class LoadApiStatus {
    LOADING,
    ERROR,
    DONE
}

enum class Zone(val value: String) {
    AROUND_RIM("Around Rim"),
    L_ELBOW("Left Elbow"),
    MID_STR("Mid Straight"),
    R_ELBOW("Right Elbow"),
    L_BASELINE("Left Baseline"),
    L_WING("Left Wing"),
    LONG_STR("Long Straight"),
    R_WING("Right Wing"),
    R_BASELINE("Right Baseline"),
    L_CORNER("Left Corner"),
    L_3PT("Left 3Points"),
    ARC("Top of Arc"),
    R_3PT("Right 3Points"),
    R_CORNER("Right Corner"),
    FT("FreeThrow Line"),
}

enum class DataType {
    SCORE, REBOUND, ASSIST, STEAL, BLOCK, TURNOVER, FOUL, FREE_THROW
}

enum class DetailDataType {
    PTS, REB, AST, STL, BLK
}