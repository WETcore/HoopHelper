package com.aqua.hoophelper.match

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.database.Event
import com.aqua.hoophelper.database.Match
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.*

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
        var slope = y/(x-(w/2))

        if (y/h < 0.585 && y/h > 0.175) {
            if (dm < (w * 0.083)) {
                selectZone(1)
                Log.d("dia","in zone 1")
            }
            else if (dm < (w * 0.153)) {
                if (slope < tan((80.0 * PI)/180) && slope > tan((0.0 * PI)/180)) {
                    selectZone(4)
                    Log.d("dia","in zone 2-1 ${atan(slope)/PI*180}")
                }
                else if (slope > tan((80.0 * PI)/180) || slope < tan((-80.0 * PI)/180)) {
                    selectZone(3)
                    Log.d("dia","in zone 2-2 ${atan(slope)/PI*180}")
                }
                else {
                    selectZone(2)
                    Log.d("dia","in zone 2-3 ${atan(slope)/PI*180}")
                }
            }
            else if (dm < (w * 0.241) && x < (0.9 * w) && x > (0.09 * w)) {
                if(slope < tan((65.0 * PI)/180) && slope > tan((0.0 * PI)/180) && (x/w) < 0.9) {
                    selectZone(5)
                    Log.d("dia","in zone 3-1 ${atan(slope)/PI*180}")
                }
                else if (slope < tan((80.0 * PI)/180) && slope > tan((65.0 * PI)/180)) {
                    selectZone(9)
                    Log.d("dia","in zone 3-2 ${atan(slope)/PI*180}")
                }
                else if (slope > tan((80.0 * PI)/180) || slope < tan((-80.0 * PI)/180)) {
                    selectZone(8)
                    Log.d("dia","in zone 3-3 ${atan(slope)/PI*180}")
                }
                else if (slope > tan((-80.0 * PI)/180) && slope < tan((-65.0 * PI)/180)) {
                    selectZone(7)
                    Log.d("dia","in zone 3-4 ${atan(slope)/PI*180}")
                }
                else if (slope > tan((-65.0 * PI)/180) && (x/w) > 0.09) {
                    selectZone(6)
                    Log.d("dia","in zone 3-5 ${atan(slope)/PI*180}")
                }
                else Log.d("dia","${x} ${w} ${atan(slope)/PI*180} in zone 3 error")
            }
            else if (x > (0.9 * w) && y <= h * 0.342) {
                selectZone(14)
                Log.d("dia","in zone 4-1 ${x}")
            }
            else if (x < (0.09 * w) && y <= h * 0.342) {
                selectZone(10)
                Log.d("dia","in zone 4-5 ${x}")
            }
            else if(dm > (w * 0.241)) {
                if (slope < tan((80.0 * PI)/180) && slope > tan((0.0 * PI)/180)) {
                    selectZone(13)
                    Log.d("dia","in zone 4-2 ${atan(slope)/PI*180}")
                }
                else if (slope > tan((80.0 * PI)/180) || slope < tan((-80.0 * PI)/180)) {
                    selectZone(12)
                    Log.d("dia","in zone 4-3 ${atan(slope)/PI*180}")
                }
                else if (slope > tan((-80.0 * PI)/180) && slope < tan((0.0 * PI)/180)) {
                    selectZone(11)
                    Log.d("dia","in zone 4-4 ${atan(slope)/PI*180}")
                }
            }
        }
        return dm
    }
}