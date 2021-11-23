package com.aqua.hoophelper.team.child.manage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.database.Player
import com.aqua.hoophelper.database.Result
import com.aqua.hoophelper.database.Rule
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource
import com.aqua.hoophelper.util.LoadApiStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ManageViewModel : ViewModel() {

    private val _status = MutableLiveData<LoadApiStatus?>()
    val status: LiveData<LoadApiStatus?>
        get() = _status

    private val db = FirebaseFirestore.getInstance()

    var rule = Rule()

    // startingPlayer
    private var _startPlayer = MutableLiveData<MutableList<Player>>(mutableListOf())
    val startPlayer: LiveData<MutableList<Player>>
        get() = _startPlayer

    // substitutionPlayer
    private var _substitutionPlayer = MutableLiveData<MutableList<Player>>(mutableListOf())
    val substitutionPlayer: LiveData<MutableList<Player>>
        get() = _substitutionPlayer

    var subNum = mutableListOf<String>()

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        setRoster()
    }

    fun setRule() {
        db.collection("Rule").document("rule").set(rule)
    }
    private fun setRoster() {
        _status.value = LoadApiStatus.LOADING
        coroutineScope.launch {
            val starPlayerList = mutableListOf<Player>()
            val subPlayerList = mutableListOf<Player>()
            when(val result = HoopRemoteDataSource.getMatchMembers()) {
                is Result.Success -> {
                    val lineUp = result.data
                    lineUp.filter { !it.starting5.contains(true) }.forEachIndexed { index, player ->
                        _substitutionPlayer.value!!.add(player)
                        subPlayerList.add(player)
                    }
                    lineUp.filter { it.starting5.contains(true) }.forEachIndexed { index, player ->
                        starPlayerList.add(player)
                    }
                    starPlayerList.sortBy { it.starting5.indexOf(true) }
                    _startPlayer.value = starPlayerList
                    _substitutionPlayer.value = subPlayerList

                    _status.value = LoadApiStatus.DONE
                }
                is Result.Error -> {
                    Log.d("status", "error")
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }

    fun switchLineUp(spinnerPos: Int, pos: Int) {
        val bufferPlayer = substitutionPlayer.value!![spinnerPos]
        val startPlayer = startPlayer.value!![pos]

        val bufferLineupList = mutableListOf(false, false, false, false, false)

        db.collection("Players")
            .get().addOnCompleteListener {
                it.result.documents.apply {
                    bufferLineupList[pos] = true
                    first { it["id"] == bufferPlayer.id }.reference.update("starting5", bufferLineupList)
                    bufferLineupList[pos] = false
                    first { it["id"] == startPlayer.id }.reference.update("starting5", bufferLineupList)

                    setRoster()
                }
            }
    }

}