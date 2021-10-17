package com.aqua.hoophelper.match

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MatchViewModel : ViewModel() {

    // shot clock
    private var _shotClock = MutableLiveData<Long>(60L)
    val shotClock: LiveData<Long>
    get() = _shotClock

    // game clock
    private var _gameClockSec = MutableLiveData<Long>()
    val gameClockSec: LiveData<Long>
        get() = _gameClockSec

    private var _gameClockMin = MutableLiveData<Long>(12L)
    val gameClockMin: LiveData<Long>
        get() = _gameClockMin



    val shotClockTimer = object : CountDownTimer(24000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            _shotClock.value = millisUntilFinished/1000
        }

        override fun onFinish() {
            //TODO("Not yet implemented")
        }

    }

    val gameClockSecTimer = object : CountDownTimer(59000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            _gameClockSec.value = millisUntilFinished/1000
        }

        override fun onFinish() {
            //TODO("Not yet implemented")
        }

    }

    fun setGameClockMin(sec: Long) {
        Log.d("clock","${sec}")
        if (sec == 0L) {
            _gameClockMin.value = _gameClockMin.value?.minus(1L)
        }
    }

}