package com.aqua.hoophelper.match

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.User
import com.aqua.hoophelper.database.Event
import com.aqua.hoophelper.database.Match
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*
import kotlin.math.*

const val COURT_TOP = 0.15
const val COURT_BOTTOM = 0.57

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
    // player 當下選擇的球員
    var player = "01"

    // selectPlayerPos 紀錄按那個 chip
    var selectPlayerPos = 0

    // player pos 先發
    var _playerNum = MutableLiveData<MutableList<String>>(
        mutableListOf<String>(
            "01",
            "02",
            "03",
            "04",
            "05",
        )
    )
    val playerNum: LiveData<MutableList<String>>
        get() = _playerNum

    // substitutionPlayer 替補
    private var _substitutionPlayer = MutableLiveData<MutableList<String>>(mutableListOf<String>(
        "11",
        "12",
        "13",
        "14",
        "15",
        "16",
        "17",
        "18",
    ))
    val substitutionPlayer: LiveData<MutableList<String>>
        get() = _substitutionPlayer

    // zone
    private var _zone = MutableLiveData<Int>(0)
    val zone: LiveData<Int>
        get() = _zone

    // record
    private var _record = MutableLiveData<Boolean>(false)
    val record: LiveData<Boolean>
        get() = _record

    // database
    var event = Event()
    var match = Match()

    // record
    private var _lastEvent = HoopRemoteDataSource.getEvents()
    val lastEvent: LiveData<List<Event>>
        get() = _lastEvent


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

    fun selectPlayer(pos: Int): String {
        selectPlayerPos = pos
        player = _playerNum.value!![pos]
        return playerNum.value!![pos]
    }

    fun selectZone(selectedZone: Int) {
        _zone.value = selectedZone
    }

    fun setScoreData(countIn: Boolean, mId: String) {
        resetData()
        event.eventId = db.collection("Events").document().id
        event.matchId = mId
        event.teamId = User.teamId
        event.actualTime = Calendar.getInstance().timeInMillis
        event.matchTimeMin = gameClockMin.value.toString()
        event.matchTimeSec = gameClockSec.value.toString()
        _record.value = countIn
        event.playerNum = player
        event.zone = zone.value!!
        if (zone.value!! in 1..9)  {
            event.score2 = _record.value!!
        }
        else if (zone.value!! in 10..14) {
            event.score3 = _record.value!!
        }
        db.collection("Events").add(event)
    }

    private fun resetData() {
        false.let {
            event.rebound = it
            event.assist = it
            event.steal = it
            event.block = it
            event.turnover = it
            event.foul = it
        }
        event.zone = 0
        event.freeThrow = null
        event.score2 = null
        event.score3 = null
    }

    fun setFreeThrowData(bool: Boolean, mId: String) {
        resetData()
        event.eventId = db.collection("Events").document().id
        event.matchId = mId
        event.teamId = User.teamId
        event.actualTime = Calendar.getInstance().timeInMillis
        event.playerNum = player
        event.matchTimeMin = gameClockMin.value.toString()
        event.matchTimeSec = gameClockSec.value.toString()
        event.freeThrow = bool
        db.collection("Events").add(event)
    }

    fun setStatData(type: DataType, mId: String) {
        resetData()
        event.eventId = db.collection("Events").document().id
        event.matchId = mId
        event.teamId = User.teamId
        event.actualTime = Calendar.getInstance().timeInMillis
        event.playerNum = player
        event.matchTimeMin = gameClockMin.value.toString()
        event.matchTimeSec = gameClockSec.value.toString()
        event.zone = zone.value!!
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
        }
        db.collection("Events").add(event)
    }


    fun getDiameter(x: Float, y: Float, w: Int, h: Int): Boolean {
        Log.i("dia","x: $x y: $y  x/w ${x/w} ${y/h}")
        var dm = sqrt((x-(w/2)).pow(2) + (y-(h*0.2)).pow(2))/2
        var slope = y/(x-(w/2))

        if (y/h in COURT_TOP..COURT_BOTTOM) {
            if (dm/w < 0.083) {
                selectZone(1)
            }
            else if (dm/w < 0.153) {
                if (slope in 0.0..tan((80.0 * PI)/180)) {
                    selectZone(4)
                }
                else if (slope > tan((80.0 * PI)/180) || slope < tan((-80.0 * PI)/180)) {
                    selectZone(3)
                }
                else {
                    selectZone(2)
                }
            }
            else if (dm < (w * 0.241) && x in (0.09 * w)..(0.9 * w)) {
                if(slope in 0.0..tan((65.0 * PI)/180) && (x/w) < 0.9) {
                    selectZone(9)
                }
                else if (slope in tan((65.0 * PI)/180)..tan((80.0 * PI)/180)) {
                    selectZone(8)
                }
                else if (slope > tan((80.0 * PI)/180) || slope < tan((-80.0 * PI)/180)) {
                    selectZone(7)
                }
                else if (slope in tan((-80.0 * PI)/180)..tan((-65.0 * PI)/180)) {
                    selectZone(6)
                }
                else if (slope > tan((-65.0 * PI)/180) && (x/w) > 0.09) {
                    selectZone(5)
                }
                else Log.d("dia","${x} ${w} ${atan(slope)/PI*180} in zone 3 error")
            }
            else if (x/w > 0.9 && y/h <= (COURT_TOP + 0.167)) {
                selectZone(14)
            }
            else if (x/w < 0.09 && y/h <= 0.362) {
                selectZone(10)
            }
            else if(dm/w > 0.241) {
                if (slope in 0.0..tan((80.0 * PI)/180)) {
                    selectZone(13)
                }
                else if (slope > tan((80.0 * PI)/180) || slope < tan((-80.0 * PI)/180)) {
                    selectZone(12)
                }
                else if (slope in tan((-80.0 * PI)/180)..0.0) {
                    selectZone(11)
                }
            }
            return false
        }
        else return true
    }

    fun getSubPlayer(subPlayer: String) {
        _playerNum.value!![selectPlayerPos] = subPlayer
        _playerNum.value = _playerNum.value
        player = _playerNum.value!![selectPlayerPos]
    }

    fun changeSubPlayer(onCourtPlayer: String, spinnerPos: Int) {
        _substitutionPlayer.value!![spinnerPos] = onCourtPlayer
        _substitutionPlayer.value = _substitutionPlayer.value
        Log.d("poss","sub ${spinnerPos} ${_substitutionPlayer.value}")
    }

    fun setHistoryText(it: List<Event>): String {
        it[0].run {
            return when {
                assist -> "assist"
                block -> "block"
                foul -> "foul"
                freeThrow == true -> "FT In"
                freeThrow == false -> "FT out"
                rebound -> "rebound"
                score2 == true -> "2pt In"
                score2 == false -> "2pt Out"
                score3 == true -> "3pt In"
                score3 == false -> "3pt Out"
                steal -> "steal"
                turnover -> "turnover"
                else -> "else"
            }
        }
    }

    fun cancelEvent() {
        db.collection("Events")
            .orderBy("actualTime", Query.Direction.DESCENDING)
            .limit(1).get().addOnSuccessListener {
                it.forEach {
                    it.reference.delete()
                }
            }
    }

}