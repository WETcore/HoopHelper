package com.aqua.hoophelper.match

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.HoopInfo
import com.aqua.hoophelper.User
import com.aqua.hoophelper.database.Event
import com.aqua.hoophelper.database.Match
import com.aqua.hoophelper.database.Player
import com.aqua.hoophelper.database.Rule
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.*

const val COURT_TOP = 0.15
const val COURT_BOTTOM = 0.57

class MatchViewModel : ViewModel() {

    // Firebase
    val db = FirebaseFirestore.getInstance()

    // database
    var event = Event()
    var match = Match()
    var rule = Rule()


    var quarterLimit = 4
    var foulLimit = 6

    var _shotClockLimit = MutableLiveData<Long>(24L)
    val shotClockLimit: LiveData<Long>
    get() = _shotClockLimit

//    var gameClockLimit = 12L
    var _gameClockLimit = MutableLiveData<Long>(12L)
    val gameClockLimit: LiveData<Long>
        get() = _gameClockLimit

    // shot clock
    var _shotClock = MutableLiveData<Long>(shotClockLimit.value)
    val shotClock: LiveData<Long>
        get() = _shotClock

    // game clock
    var _gameClockSec = MutableLiveData<Long>(60L)
    val gameClockSec: LiveData<Long>
        get() = _gameClockSec

    var _gameClockMin = MutableLiveData<Long>(gameClockLimit.value!!)
    val gameClockMin: LiveData<Long>
        get() = _gameClockMin

    // quarter
    var _quarter = MutableLiveData<Int>(1)
    val quarter: LiveData<Int>
        get() = _quarter



    //////////////////
    // player num send to db
    var player = ""

    // selectPlayerPos 紀錄按那個 chip
    var selectPlayerPos = 0

    // roster
    private var _roster = MutableLiveData<List<Player>>()
    val roster: LiveData<List<Player>>
        get() = _roster

    // startingPlayer
    private var _startPlayer = MutableLiveData<MutableList<Player>>(mutableListOf())
    val startPlayer: LiveData<MutableList<Player>>
        get() = _startPlayer

    // substitutionPlayer 替補
    private var _substitutionPlayer = MutableLiveData<MutableList<Player>>(mutableListOf())
    val substitutionPlayer: LiveData<MutableList<Player>>
        get() = _substitutionPlayer

    var subNum = mutableListOf<String>()

    // zone
    private var _zone = MutableLiveData<Int>(0)
    val zone: LiveData<Int>
        get() = _zone

    // record
    private var _record = MutableLiveData<Boolean>(false)
    val record: LiveData<Boolean>
        get() = _record

    // last event
    private var _lastEvent = HoopRemoteDataSource.getEvents()
    val lastEvent: LiveData<List<Event>>
        get() = _lastEvent

    // foul
    private var _foul = HoopRemoteDataSource.getEvents()
    val foul: LiveData<List<Event>>
        get() = _foul

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getMatchRule()
    }

    val shotClockTimer = object : CountDownTimer(Long.MAX_VALUE, 1000L) {
        override fun onTick(millisUntilFinished: Long) {
            Log.d("timer", "start")
            if (shotClock.value!! == 0L) {} else {
                _shotClock.value = _shotClock.value?.minus(1L)
            }
        }
        override fun onFinish() {}
    }

    val gameClockSecTimer = object : CountDownTimer(Long.MAX_VALUE, 1000L) {
        override fun onTick(millisUntilFinished: Long) {
            if (gameClockSec.value!! == 0L) {} else {
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
        player = _startPlayer.value!![pos].number
        return _startPlayer.value!![pos].number
    }

    fun selectZone(selectedZone: Int) {
        _zone.value = selectedZone
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

    fun setScoreData(countIn: Boolean, mId: String) {
        resetData()
        event.eventId = db.collection("Events").document().id
        event.matchId = mId
        event.teamId = User.teamId
        event.playerId = startPlayer.value?.get(selectPlayerPos)?.id ?: ""
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

    fun setFreeThrowData(bool: Boolean, mId: String) {
        resetData()
        event.eventId = db.collection("Events").document().id
        event.matchId = mId
        event.teamId = User.teamId
        event.playerId = startPlayer.value?.get(selectPlayerPos)?.id ?: ""
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
        event.playerId = startPlayer.value?.get(selectPlayerPos)?.id ?: ""
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
        val dm = sqrt((x-(w/2)).pow(2) + (y-(h*0.2)).pow(2))/2
        val slope = y/(x-(w/2))

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

    fun getSubPlayer2Starting(position: Int) {
        _startPlayer.value!![selectPlayerPos] = substitutionPlayer.value!![position]
        _startPlayer.value = _startPlayer.value
        player = _startPlayer.value!![selectPlayerPos].number
    }

    fun changeSubPlayer(onCourtPlayer: Player, spinnerPos: Int) {
        _substitutionPlayer.value!![spinnerPos] = onCourtPlayer
        _substitutionPlayer.value = _substitutionPlayer.value
        Log.d("poss","sub ${spinnerPos} ${substitutionPlayer.value}")
    }

    fun setHistoryText(it: List<Event>): String {
        if (it.isNullOrEmpty()){
            return "latest event"
        }
        else{
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
            Log.d("subPlayer2", "${_startPlayer.value}")
            Log.d("subPlayer3", "${_substitutionPlayer.value}")
        }
    }

    fun getFoulCount(playerNum: String, onCourtPlayer: Player) {
        var count = _foul.value?.filter {
            it.playerNum == playerNum && it.matchId == HoopInfo.matchId
        }?.size?.plus(1)
        if (count != null) {
            if(count >= foulLimit) {
                getSubPlayer2Starting(0)
                changeSubPlayer(onCourtPlayer, 0)
//                player = subNum[0]
                Log.d("foulLog","Hi $count")
            }
        }
    }

    fun getMatchRule() {
        coroutineScope.launch {
            rule = HoopRemoteDataSource.getRule()
            quarterLimit = rule.quarter.toInt() // TODO TO
            foulLimit = rule.foulOut.toInt()
            _shotClockLimit.value = rule.sClock.toLong()
            _gameClockLimit.value = rule.gClock.toLong()
            _shotClock.value = _shotClockLimit.value
            _gameClockMin.value = _gameClockLimit.value!! - 1
            shotClockTimer.start()
            gameClockSecTimer.start()
        }
    }

    ////////////////
    override fun onCleared() {
        super.onCleared()
        shotClockTimer.cancel()
        gameClockSecTimer.cancel()
        viewModelJob.cancel()
    }
}