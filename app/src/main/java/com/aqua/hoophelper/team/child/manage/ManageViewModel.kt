package com.aqua.hoophelper.team.child.manage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ManageViewModel : ViewModel() {

    var _start5 = MutableLiveData(
        mutableListOf(
            "1",
            "2",
            "3",
            "4",
            "5",
        )
    )
    val start5: LiveData<MutableList<String>>
        get() = _start5

    var _lineup = MutableLiveData(
        mutableListOf(
        "6",
        "7",
        "8",
        "9",
        "10",
        "11",
        "12",
        "13",
        "14",
        "15",
        )
    )
    val lineup: LiveData<MutableList<String>>
        get() = _lineup

}