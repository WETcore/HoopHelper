package com.aqua.hoophelper.match

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.database.Event
import com.aqua.hoophelper.database.Match
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.pow
import kotlin.math.sqrt

class MatchViewModel : ViewModel() {

    // Firebase
    val db = FirebaseFirestore.getInstance()

    // shot clock
     var _shotClock = MutableLiveData<Long>(24L)
    val shotClock: LiveData<Long>
        get() = _shotClock

    // game clock
     var _gameClockSec = MutableLiveData<Long>(60L)
    val gameClockSec: LiveData<Long>
        get() = _gameClockSec

     var _gameClockMin = MutableLiveData<Long>(12L)
    val gameClockMin: LiveData<Long>
        get() = _gameClockMin

    // quarter
     var _quarter = MutableLiveData<Int>(1)
    val quarter: LiveData<Int>
        get() = _quarter

    // date
    private var _date = MutableLiveData<String>()
    val date: LiveData<String>
        get() = _date

    //////////////////
    var player = -1

    // zone
    private var _zone = MutableLiveData<String>()
    val zone: LiveData<String>
        get() = _zone

    // record
    private var _record = MutableLiveData<Boolean>(false)
    val record: LiveData<Boolean>
        get() = _record

    // database
    var event = Event()
    var match = Match()




    val shotClockTimer = object : CountDownTimer(Long.MAX_VALUE, 10L) {
        override fun onTick(millisUntilFinished: Long) {
            if (_shotClock.value!! == 0L) {} else {
                _shotClock.value = _shotClock.value?.minus(1L)
            }
        }
        override fun onFinish() {}
    }

    val gameClockSecTimer = object : CountDownTimer(Long.MAX_VALUE, 10L) {
        override fun onTick(millisUntilFinished: Long) {
            if (_gameClockSec.value!! == 0L) {} else {
                _gameClockSec.value = _gameClockSec.value?.minus(1L)
            }
        }
        override fun onFinish() {}
    }

    fun setGameClockMin(sec: Long) {
        if ( sec >= 60) {
            _gameClockMin.value = _gameClockMin.value?.minus(1L)
        }
    }

    fun selectPlayer(selectedPlayer: Int) {
        player = selectedPlayer
    }

    fun selectZone(selectedZone: Int) {
        _zone.value = selectedZone.toString()
    }

    fun setScoreData(selectedPlayer: Int, selectedZone: Int, timerMin: String, timerSec: String) {
        resetData()
        event.matchTimeMin = timerMin
        event.matchTimeSec = timerSec
        _record.value = true
        event.playerNum = selectedPlayer.toString()
        event.score[selectedZone.toString()] = _record.value!!
    }

    private fun resetData() {
        event.score = mutableMapOf()
        false.let {
            event.rebound = it
            event.assist = it
            event.steal = it
            event.block = it
            event.turnover = it
            event.foul = it
        }
    }

    fun setStatData(selectedPlayer: Int, type: DataType, timerMin: Long?, timerSec: Long?) {
        resetData()
        event.playerNum = selectedPlayer.toString()
        event.matchTimeMin = timerMin.toString()
        event.matchTimeSec = timerSec.toString()
        _record.value = true
        when(type) {
            DataType.REBOUND -> {
                event.rebound = _record.value!!
            }
            DataType.ASSIST -> {
                event.assist = _record.value!!
            }
            DataType.STEAL -> {
                event.steal = _record.value!!
            }
            DataType.BLOCK -> {
                event.block = _record.value!!
            }
            DataType.TURNOVER -> {
                event.turnover = _record.value!!
            }
            DataType.FOUL -> {
                event.foul = _record.value!!
            }
            DataType.FREETHROW -> {
                event.freeThrow = _record.value!!
            }
        }
    }

    fun getChipPos(x: Float, y: Float, w: Int, h: Int) {
        if (x > w/2) {
            selectZone(2)
        } else {
            selectZone(1)
        }
    }

    fun getDiameter(x: Float, y: Float, w: Int, h: Int): Double {
        var dm = sqrt((x-(w*0.5)).pow(2) + (y-(h*0.22)).pow(2))/2
        if (dm < (w * 0.083)) {
            Log.d("dia","in zone 1")
        } else if (dm < (w * 0.153)) {
            Log.d("dia","in zone 2")
        } else if (dm < (w * 0.241)) {
            Log.d("dia","in zone 3")
        }
        return dm
    }
}