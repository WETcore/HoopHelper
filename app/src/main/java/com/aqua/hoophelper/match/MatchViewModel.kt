package com.aqua.hoophelper.match

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.database.Event
import com.google.firebase.firestore.FirebaseFirestore

class MatchViewModel : ViewModel() {

    // Firebase
    val db = FirebaseFirestore.getInstance()

    // shot clock
    private var _shotClock = MutableLiveData<Long>(24L)
    val shotClock: LiveData<Long>
        get() = _shotClock

    // game clock
    private var _gameClockSec = MutableLiveData<Long>(59L)
    val gameClockSec: LiveData<Long>
        get() = _gameClockSec

    private var _gameClockMin = MutableLiveData<Long>(12L)
    val gameClockMin: LiveData<Long>
        get() = _gameClockMin

    // date
    private var _date = MutableLiveData<String>()
    val date: LiveData<String>
        get() = _date

    //////////////////
    var player = -1
    var zone = -1
    // record
    private var _record = MutableLiveData<Boolean>()
    val record: LiveData<Boolean>
        get() = _record
    // event data
    var events = Event()


    var shotClockTimer = object : CountDownTimer(_shotClock.value!! * 1000L, 1000L) {
        override fun onTick(millisUntilFinished: Long) {
//                        Log.d("clock","timer ${_shotClock.value}")
            _shotClock.value = _shotClock.value?.minus(1L) //millisUntilFinished/1000L
        }

        override fun onFinish() {
            //TODO("Not yet implemented")
        }

    }

    val gameClockSecTimer = object : CountDownTimer(_gameClockSec.value!! * 1000L, 1000L) {
        override fun onTick(millisUntilFinished: Long) {
            _gameClockSec.value = _gameClockSec.value?.minus(1L) //millisUntilFinished/1000L
        }

        override fun onFinish() {
            //TODO("Not yet implemented")
        }

    }

    fun setGameClockMin(sec: Long) {
        if (sec == 0L) {
            _gameClockMin.value = _gameClockMin.value?.minus(1L)
        }
    }

    fun selectPlayer(selectedPlayer: Int) {
        player = selectedPlayer
    }

    fun selectZone(selectedZone: Int) {
        zone = selectedZone
    }

    fun setScoreData(selectedPlayer: Int, selectedZone: Int, timerMin: String, timerSec: String) {
        resetData()
        events.matchTimeMin = timerMin
        events.matchTimeSec = timerSec
        _record.value = true
        events.playerId = selectedPlayer.toString()
        record.value?.let { events.score.put(selectedZone, it) }
    }

    private fun resetData() {
        events.score = mutableMapOf()
        false.let {
            events.rebound = it
            events.assist = it
            events.steal = it
            events.block = it
            events.turnover = it
            events.foul = it
        }
    }

    fun setStatData(selectedPlayer: Int, type: DataType, timerMin: String, timerSec: String) {
        resetData()
        events.playerId = selectedPlayer.toString()
        events.matchTimeMin = timerMin
        events.matchTimeSec = timerSec
        _record.value = true
        when(type) {
            DataType.REBOUND -> {
                events.rebound = _record.value!!
            }
            DataType.ASSIST -> {
                events.assist = _record.value!!
            }
            DataType.STEAL -> {
                events.steal = _record.value!!
            }
            DataType.BLOCK -> {
                events.block = _record.value!!
            }
            DataType.TURNOVER -> {
                events.turnover = _record.value!!
            }
            DataType.FOUL -> {
                events.foul = _record.value!!
            }
            DataType.FREETHROW -> {
                events.freeThrow = _record.value!!
            }
        }
    }
}