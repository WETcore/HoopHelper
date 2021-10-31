package com.aqua.hoophelper.team.child.manage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.database.Rule
import com.google.firebase.firestore.FirebaseFirestore

class ManageViewModel : ViewModel() {

    val db = FirebaseFirestore.getInstance()

    var rule = Rule()

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

    var _subLineup = MutableLiveData(
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
    val subLineup: LiveData<MutableList<String>>
        get() = _subLineup

    var _lineup = MutableLiveData<MutableList<String>>(
        (_subLineup.value!! + _start5.value!!).sortedBy { it.toInt() }.toMutableList()
    )
    val lineup: LiveData<MutableList<String>>
        get() = _lineup

    fun setRule() {
        db.collection("Rule").document("rule").set(rule)
    }
}