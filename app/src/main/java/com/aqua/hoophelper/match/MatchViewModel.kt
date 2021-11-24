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
import com.aqua.hoophelper.database.Result
import com.aqua.hoophelper.database.Rule
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource
import com.aqua.hoophelper.util.LoadApiStatus
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
const val THREE_LINE_LEFT = 0.09
const val THREE_LINE_RIGHT = 0.9
const val CORNER_BOTTOM_BOUND = 0.167
const val ROUND_DEGREE = 180
const val SLOPE_80 = 80.0
const val SLOPE_65 = 65.0
const val DIAMETER1 = 0.083
const val DIAMETER2 = 0.153
const val DIAMETER3 = 0.241

class MatchViewModel : ViewModel() {

    val _status = MutableLiveData<LoadApiStatus?>()
    val status: LiveData<LoadApiStatus?>
        get() = _status

    // Firebase
    private val db = FirebaseFirestore.getInstance()

    // database
    var event = Event()
    var match = Match()
    var rule = Rule()


    var quarterLimit = 4
    var foulLimit = 6
    var timeOut1Limit = 2
    var timeOut2Limit = 3

    // time out
    var timeOut = 0

    var _shotClockLimit = MutableLiveData<Long>(24L)
    val shotClockLimit: LiveData<Long>
        get() = _shotClockLimit

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
    var playerNum = ""
    var playerName = ""
    var playerImage = ""

    // selectPlayer chip position
    var selectPlayerPos = 0

    // roster
    private var _roster = MutableLiveData<List<Player>>()
    private val roster: LiveData<List<Player>>
        get() = _roster

    // startingPlayer
    private var _startPlayer = MutableLiveData<MutableList<Player>>(mutableListOf())
    val startPlayer: LiveData<MutableList<Player>>
        get() = _startPlayer

    // substitutionPlayer
    private var _substitutionPlayer = MutableLiveData<MutableList<Player>>(mutableListOf())
    val substitutionPlayer: LiveData<MutableList<Player>>
        get() = _substitutionPlayer

    var subNum = mutableListOf<String>()

    // zone
    private var _zone = MutableLiveData<Int>(0)
    val zone: LiveData<Int>
        get() = _zone

    // last event
    private var _lastEvent = HoopRemoteDataSource.getEvents()
    val lastEvent: LiveData<List<Event>>
        get() = _lastEvent

    // foul
    private var _foul = HoopRemoteDataSource.getEvents()
    private val foul: LiveData<List<Event>>
        get() = _foul

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getMatchRule()
        setRoster()
    }

    val shotClockTimer = object : CountDownTimer(Long.MAX_VALUE, 1000L) {
        override fun onTick(millisUntilFinished: Long) {
            if (shotClock.value!! != 0L) {
                _shotClock.value = _shotClock.value?.minus(1L)
            }
        }

        override fun onFinish() {}
    }

    val gameClockSecTimer = object : CountDownTimer(Long.MAX_VALUE, 1000L) {
        override fun onTick(millisUntilFinished: Long) {
            if (gameClockSec.value!! != 0L) {
                _gameClockSec.value = _gameClockSec.value?.minus(1L)
            }
        }

        override fun onFinish() {}
    }

    fun setGameClockMin(sec: Long) {
        if (sec >= 60) {
            _gameClockMin.value = _gameClockMin.value?.minus(1L)
        }
    }

    fun selectPlayer(pos: Int): String {
        selectPlayerPos = pos
        playerNum = startPlayer.value!![pos].number
        playerName = startPlayer.value!![pos].name
        playerImage = startPlayer.value!![pos].avatar
        return startPlayer.value!![pos].number
    }

    private fun selectZone(selectedZone: Int) {
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

    ///////////////////////////////////////////////////////////////////////////////
    fun setEventData(mId: String, type: DataType, isCount: Boolean) {
        resetData()
        event.apply {
            eventId = db.collection("Events").document().id
            matchId = mId
            teamId = User.teamId
            playerId = startPlayer.value?.get(selectPlayerPos)?.id ?: ""
            actualTime = Calendar.getInstance().timeInMillis
            this.playerNum = (this@MatchViewModel).playerNum
            this.playerName = (this@MatchViewModel).playerName
            this.playerImage = (this@MatchViewModel).playerImage
            matchTimeMin = gameClockMin.value.toString()
            matchTimeSec = gameClockSec.value.toString()
            this.quarter = (this@MatchViewModel).quarter.value.toString()
            if (type != DataType.FREE_THROW) {
                this.zone = (this@MatchViewModel).zone.value!!
            }
        }
        when (type) {
            DataType.SCORE -> {
                if (zone.value!! in 1..9) {
                    event.score2 = isCount
                } else if (zone.value!! in 10..14) {
                    event.score3 = isCount
                }
            }
            DataType.REBOUND -> {
                event.rebound = true
            }
            DataType.ASSIST -> {
                event.assist = true
            }
            DataType.STEAL -> {
                event.steal = true
            }
            DataType.BLOCK -> {
                event.block = true
            }
            DataType.TURNOVER -> {
                event.turnover = true
            }
            DataType.FOUL -> {
                event.foul = true
            }
            DataType.FREE_THROW -> {
                event.zone = -1
                event.freeThrow = isCount
            }
        }
        db.collection("Events").add(event)
    }
    ///////////////////////////////////////////////////////////////////////////////

    fun getDiameter(x: Float, y: Float, w: Int, h: Int): Boolean {
        Log.i("dia", "x: $x y: $y  x/w ${x / w} ${y / h}")
        val dm = sqrt((x - (w / 2)).pow(2) + (y - (h * 0.2)).pow(2)) / 2
        val slope = y / (x - (w / 2))

        if (y / h in COURT_TOP..COURT_BOTTOM) {
            if (dm / w < DIAMETER1) {
                selectZone(1)
            } else if (dm / w < DIAMETER2) {
                if (slope in 0.0..tan((SLOPE_80 * PI) / ROUND_DEGREE)) {
                    selectZone(4)
                } else if (slope > tan((SLOPE_80 * PI) / ROUND_DEGREE) || slope < tan((-SLOPE_80 * PI) / ROUND_DEGREE)) {
                    selectZone(3)
                } else {
                    selectZone(2)
                }
            } else if (dm < (w * DIAMETER3) && x in (THREE_LINE_LEFT * w)..(THREE_LINE_RIGHT * w)) {
                if (slope in 0.0..tan((SLOPE_65 * PI) / ROUND_DEGREE) && (x / w) < THREE_LINE_RIGHT) {
                    selectZone(9)
                } else if (slope in tan((SLOPE_65 * PI) / ROUND_DEGREE)..tan((SLOPE_80 * PI) / ROUND_DEGREE)) {
                    selectZone(8)
                } else if (slope > tan((SLOPE_80 * PI) / ROUND_DEGREE) || slope < tan((-SLOPE_80 * PI) / ROUND_DEGREE)) {
                    selectZone(7)
                } else if (slope in tan((-SLOPE_80 * PI) / ROUND_DEGREE)..tan((-SLOPE_65 * PI) / ROUND_DEGREE)) {
                    selectZone(6)
                } else if (slope > tan((-SLOPE_65 * PI) / ROUND_DEGREE) && (x / w) > THREE_LINE_LEFT) {
                    selectZone(5)
                } else Log.d("dia", "${x} ${w} ${atan(slope) / PI * ROUND_DEGREE} in zone 3 error")
            } else if (x / w > THREE_LINE_RIGHT && y / h <= (COURT_TOP + CORNER_BOTTOM_BOUND)) {
                selectZone(14)
            } else if (x / w < THREE_LINE_LEFT && y / h <= (COURT_TOP + CORNER_BOTTOM_BOUND)) {
                selectZone(10)
            } else if (dm / w > DIAMETER3) {
                if (slope in 0.0..tan((SLOPE_80 * PI) / ROUND_DEGREE)) {
                    selectZone(13)
                } else if (slope > tan((SLOPE_80 * PI) / ROUND_DEGREE) || slope < tan((-SLOPE_80 * PI) / ROUND_DEGREE)) {
                    selectZone(12)
                } else if (slope in tan((-SLOPE_80 * PI) / ROUND_DEGREE)..0.0) {
                    selectZone(11)
                }
            }
            return false
        } else return true
    }

    fun getSubPlayer2Starting(position: Int) {
        _startPlayer.value!![selectPlayerPos] = substitutionPlayer.value!![position]
        _startPlayer.value = _startPlayer.value
        playerNum = _startPlayer.value!![selectPlayerPos].number
        playerName = _startPlayer.value!![selectPlayerPos].name
        playerImage = _startPlayer.value!![selectPlayerPos].avatar
    }

    fun changeSubPlayer(onCourtPlayer: Player, spinnerPos: Int) {
        _substitutionPlayer.value!![spinnerPos] = onCourtPlayer
        _substitutionPlayer.value = _substitutionPlayer.value
    }

    fun setHistoryText(it: List<Event>): String {
        if (it.isNullOrEmpty()) {
            return "latest event"
        } else {
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

    private fun setRoster() {
        _status.value = LoadApiStatus.LOADING
        coroutineScope.launch {
            val startPlayers = mutableListOf<Player>()
            val subPlayers = mutableListOf<Player>()
            when (val result = HoopRemoteDataSource.getMatchMembers()) {
                is Result.Success -> {
                    _roster.value = result.data!!
                    val lineUp = roster.value!!
                    lineUp.filter {
                        !it.starting5.contains(true)
                    }.forEachIndexed { index, player ->
                        _substitutionPlayer.value!!.add(player)
                        subPlayers.add(player)
                    }
                    lineUp.filter {
                        it.starting5.contains(true)
                    }.forEachIndexed { index, player ->
                        startPlayers.add(player)
                    }
                    startPlayers.sortBy { it.starting5.indexOf(true) }
                    _startPlayer.value = startPlayers
                    _substitutionPlayer.value = subPlayers

                    _status.value = LoadApiStatus.DONE
                }
                is Result.Error -> {
                    Log.d("status", "error")
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }

    fun getFoulCount(playerNum: String, onCourtPlayer: Player) {
        val count = foul.value?.filter {
            it.playerNum == playerNum && it.matchId == HoopInfo.matchId
        }?.size?.plus(1)
        if (count != null) {
            if (count >= foulLimit) {
                getSubPlayer2Starting(0)
                changeSubPlayer(onCourtPlayer, 0)
            }
        }
    }

    private fun getMatchRule() {
        _status.value = LoadApiStatus.LOADING
        coroutineScope.launch {
            when (val result = HoopRemoteDataSource.getRule()) {
                is Result.Success -> {
                    rule = result.data!!
                    quarterLimit = rule.quarter.toInt()
                    foulLimit = rule.foulOut.toInt()
                    timeOut1Limit = rule.to1.toInt()
                    timeOut2Limit = rule.to2.toInt()
                    _shotClockLimit.value = rule.sClock.toLong()
                    _gameClockLimit.value = rule.gClock.toLong()
                    _shotClock.value = _shotClockLimit.value
                    _gameClockMin.value = _gameClockLimit.value!! - 1
                    shotClockTimer.start()
                    gameClockSecTimer.start()
                    _status.value = LoadApiStatus.DONE
                }
                is Result.Error -> {
                    Log.d("status", "error")
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }

    fun setTimeOutCount(): Boolean {
        timeOut += 1
        return if (quarter.value!! <= quarterLimit / 2) {
            timeOut > timeOut1Limit
        } else {
            timeOut > timeOut2Limit
        }
    }

    fun setFirstPlayer(player: Player) {
        if (playerNum == "") playerNum = player.number
        if (playerName == "") playerName = player.name
        if (playerImage == "") playerImage = player.avatar
    }

    ////////////////
    override fun onCleared() {
        super.onCleared()
        shotClockTimer.cancel()
        gameClockSecTimer.cancel()
        viewModelJob.cancel()
    }
}