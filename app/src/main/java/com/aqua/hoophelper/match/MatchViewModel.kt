package com.aqua.hoophelper.match

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.database.Event

class MatchViewModel : ViewModel() {

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

    // score
    private var _twoPt = MutableLiveData<String>()
    val twoPt: LiveData<String>
        get() = _twoPt
    private var _twoPtM = MutableLiveData<String>()
    val twoPtM: LiveData<String>
        get() = _twoPtM
    private var _threePt = MutableLiveData<String>()
    val threePt: LiveData<String>
        get() = _threePt
    private var _threePtM = MutableLiveData<String>()
    val threePtM: LiveData<String>
        get() = _threePtM
    // rebound
    private var _rebound = MutableLiveData<String>()
    val rebound: LiveData<String>
        get() = _rebound
    // assist
    private var _assist = MutableLiveData<String>()
    val assist: LiveData<String>
        get() = _assist
    // steal
    private var _steal = MutableLiveData<String>()
    val steal: LiveData<String>
        get() = _steal
    // block
    private var _block = MutableLiveData<String>()
    val block: LiveData<String>
        get() = _block
    // turnover
    private var _turnover = MutableLiveData<String>()
    val turnover: LiveData<String>
        get() = _turnover
    // foul
    private var _foul = MutableLiveData<String>()
    val foul: LiveData<String>
        get() = _foul
    // freeThrow
    private var _freeThrow = MutableLiveData<String>()
    val freeThrow: LiveData<String>
        get() = _freeThrow
    private var _freeThrowM = MutableLiveData<String>()
    val freeThrowM: LiveData<String>
        get() = _freeThrowM
    // onCourtTime
    private var _onCourtTime = MutableLiveData<String>()
    val onCourtTime: LiveData<String>
        get() = _onCourtTime
    // date
    private var _date = MutableLiveData<String>()
    val date: LiveData<String>
        get() = _date

    //////////////////
    // record
    private var _record = MutableLiveData<Boolean?>()
    val record: LiveData<Boolean?>
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

    fun resetTime() {
        _shotClock.value = 24L
//        _gameClockSec =
    }

    fun setGameClockMin(sec: Long) {
        if (sec == 0L) {
            _gameClockMin.value = _gameClockMin.value?.minus(1L)
        }
    }

    fun setScoreData(zone: Int) {
        events.zone = mutableMapOf()
        _record.value = true
        events.zone.put(zone, record.value)
    }

    fun setFTData() {
        _record.value = true
    }

}