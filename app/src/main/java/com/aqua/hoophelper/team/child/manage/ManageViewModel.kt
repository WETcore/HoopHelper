package com.aqua.hoophelper.team.child.manage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.database.Player
import com.aqua.hoophelper.database.Rule
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ManageViewModel : ViewModel() {

    val db = FirebaseFirestore.getInstance()

    var rule = Rule()

    // startingPlayer
    var _startPlayer = MutableLiveData<MutableList<Player>>(mutableListOf())
    val startPlayer: LiveData<MutableList<Player>>
        get() = _startPlayer

    // substitutionPlayer 替補
    var _substitutionPlayer = MutableLiveData<MutableList<Player>>(mutableListOf())
    val substitutionPlayer: LiveData<MutableList<Player>>
        get() = _substitutionPlayer
    var subNum = mutableListOf<String>()

    // roster
    var _roster = MutableLiveData<List<Player>>()
    val roster: LiveData<List<Player>>
        get() = _roster

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun setRule() {
        db.collection("Rule").document("rule").set(rule)
    }
    init {
        setRoster()
    }
    fun setRoster() {
        coroutineScope.launch {
            val starPlayerList = mutableListOf<Player>()
            val subPlayerList = mutableListOf<Player>()
            _roster.value = HoopRemoteDataSource.getMatchMembers()
            Log.d("roster", "${_roster.value}")
            val lineUp = _roster.value!!

            lineUp.filter {
                !it.starting5
            }.forEachIndexed { index, player ->
                _substitutionPlayer.value!!.add(player)
                subPlayerList.add(player)
            }

            lineUp.filter {
                it.starting5
            }.forEachIndexed { index, player ->
                starPlayerList.add(player)
            }
            _startPlayer.value = starPlayerList
            _substitutionPlayer.value = subPlayerList
//            Log.d("subPlayer4", "${_startPlayer.value}")
            Log.d("subPlayer5", "${_substitutionPlayer.value}")
        }
    }

    fun refresh() {
        _roster.value = _roster.value
        _startPlayer.value = _startPlayer.value
        _substitutionPlayer.value = _substitutionPlayer.value
    }

    fun switchLineUp(spinnerPos: Int, pos: Int) {
        var buffer = substitutionPlayer.value!![spinnerPos]
        val id = roster.value?.filter { it.starting5 }?.get(pos)?.id

        db.collection("Players")
            .whereEqualTo("id", id)
            .get().addOnSuccessListener {
                it.documents.first().reference.update("starting5", false)
            }

        db.collection("Players")
            .whereEqualTo("id", buffer.id)
            .get().addOnSuccessListener {
                it.documents.first().reference.update("starting5", true)
            }

        setRoster()
    }

    var releasePos = 0
    fun removePlayer(spinnerPos: Int) {

        var buffer = roster.value!![spinnerPos]

        db.collection("Players")
            .whereEqualTo("id", buffer.id)
            .get().addOnSuccessListener {
                it.documents[0].reference.delete()
            }
        setRoster()
    }
}