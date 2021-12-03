package com.aqua.hoophelper.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.database.*
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource
import com.aqua.hoophelper.util.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: HoopRepository) : ViewModel() {

    var releasePos = 0

    val _status = MutableLiveData<LoadApiStatus?>()
    val status: LiveData<LoadApiStatus?>
        get() = _status

    var player = Player()
    var team = Team()
    var invitation = Invitation()

    // roster
    var _roster = MutableLiveData<List<Player>>()
    val roster: LiveData<List<Player>>
        get() = _roster

    // userInfo
    var _userInfo = MutableLiveData<Player>()
    val userInfo: LiveData<Player>
        get() = _userInfo

    // teamInfo
    var _teamInfo = MutableLiveData<Team>()
    val teamInfo: LiveData<Team>
        get() = _teamInfo

    // authToggle
    var _authToggle = MutableLiveData(false)
    val authToggle: LiveData<Boolean>
        get() = _authToggle

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getUserInfo()
        getTeamJerseyNumbers()
        setRoster()
    }

    fun sendTeamInfo(teamName: String, playerNum: String, playerName: String) {
        _status.value = LoadApiStatus.LOADING
        team.name = teamName
        team.jerseyNumbers = mutableListOf(playerNum, "T1", "T2", "T3", "T4", "T5")

        player.apply {
            email = User.account?.email ?: "error"
            number = playerNum
            this.name = playerName
            captain = true
            avatar = Firebase.auth.currentUser?.photoUrl.toString()
        }

        coroutineScope.launch {
            when(val result = repository.setTeamInfo(team)) {
                is Result.Success -> {
                    sendCaptainInfo(playerNum, playerName)
                }
                is Result.Error -> {
                    Log.d("status", "error")
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }

    private fun sendCaptainInfo(playerNum: String, playerName: String) {
        player.apply {
            email = User.account?.email ?: "error"
            number = playerNum
            name = playerName
            captain = true
            avatar = Firebase.auth.currentUser?.photoUrl.toString()
        }

        coroutineScope.launch {
            when(val result = repository.setCaptainInfo(player)) {
                is Result.Success -> {
                    sendMockTeammate()
//                    _status.value = LoadApiStatus.DONE
                }
                is Result.Error -> {
                    Log.d("status", "error")
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }

    private fun sendMockTeammate() {
        val botPlayer = Player()
        botPlayer.apply {
            avatar = ROBOT_IMAGE
            teamId = team.id
            coroutineScope.launch {
                val mockTeammate = listOf("T1", "T2", "T3", "T4", "T5")
                mockTeammate.forEachIndexed { index, value ->
                    starting5 = Player().starting5
                    starting5[index] = true
                    id = value
                    botPlayer.name = value
                    botPlayer.number = value
                    when(val result = repository.setMockTeammate(this@apply)) {
                        is Result.Success -> {
                            if (index == mockTeammate.lastIndex) _status.value = LoadApiStatus.DONE
                        }
                        is Result.Error -> {
                            Log.d("status", "error")
                            _status.value = LoadApiStatus.ERROR
                        }
                    }
                }
            }
        }
    }


    var initState = true
    private fun setRoster() {
        coroutineScope.launch {
            when (val result = repository.getMatchMembers()) {
                is Result.Success -> {
                    _roster.value = result.data
                    if (!initState) {
                        _status.value = LoadApiStatus.DONE
                    } else initState = !initState
                }
                is Result.Error -> {
                    Log.d("status", "error")
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }

    fun removePlayer(): Boolean {
        _status.value = LoadApiStatus.LOADING
        val byePlayer = roster.value?.get(releasePos) ?: Player()
        return if (byePlayer.id != userInfo.value?.id) { // remove player who is not a captain
            coroutineScope.launch {
                when (val result = repository.deletePlayer(byePlayer)) {
                    is Result.Success -> {
                        _status.value = LoadApiStatus.DONE
                    }
                    is Result.Error -> {
                        Log.d("status", "error")
                        _status.value = LoadApiStatus.ERROR
                    }
                }
            }
            setRoster()
            false
        } else {
            true
        }
    }

    private fun getTeamJerseyNumbers() {
        coroutineScope.launch {
            when(val result = repository.getTeamInfo()) {
                is Result.Success -> {
                    _status.value = LoadApiStatus.DONE
                }
                is Result.Error -> {
                    Log.d("status", "error")
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }

    fun sendInvitation(mail: String, name: String) {
        _status.value = LoadApiStatus.LOADING
        coroutineScope.launch {
            invitation.apply {
                teamId = User.teamId
                inviteeMail = mail
                playerName = name
                existingNumbers = User.teamJerseyNumbers
            }
            when(val result = repository.setInvitationInfo(invitation)) {
                is Result.Success -> {
                    _status.value = LoadApiStatus.DONE
                }
                is Result.Error -> {
                    Log.d("status", "error")
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }

    fun getPlayerUserInfo() {
        _status.value = LoadApiStatus.LOADING
        coroutineScope.launch {
            when (val result = repository.getTeams()) {
                is Result.Success -> {
                    _teamInfo.value = result.data.firstOrNull { it.id == User.teamId }
                }
                is Result.Error -> {
                    Log.d("status", "error")
                    _status.value = LoadApiStatus.ERROR
                }
            }
            when (val result = repository.getPlayer()) {
                is Result.Success -> {
                    _userInfo.value = result.data
                    _status.value = LoadApiStatus.DONE
                }
                is Result.Error -> {
                    Log.d("status", "error")
                    _status.value = LoadApiStatus.ERROR
                }
            }

        }
    }

    fun getUserInfo() {
        _status.value = LoadApiStatus.LOADING
        coroutineScope.launch {
            repository.getUserInfo()
            repository.getMatchMembers()
            _authToggle.value = !(_authToggle.value ?: false)
        }
    }

    fun setNewCaptain(id: MutableList<String>, position: Int) {
        _status.value = LoadApiStatus.LOADING
        coroutineScope.launch {
            when(val result = repository.deletePlayer(roster.value?.get(releasePos) ?: Player())) {
                is Result.Success -> {
                }
                is Result.Error -> {
                    Log.d("status", "error")
                    _status.value = LoadApiStatus.ERROR
                }
            }
            when(val result = repository.updateCaptain(id[position])) {
                is Result.Success -> {
                }
                is Result.Error -> {
                    Log.d("status", "error")
                    _status.value = LoadApiStatus.ERROR
                }
            }
            when(val result = repository.updateJerseyNumbers(roster.value?.get(releasePos)?.number ?: "")) {
                is Result.Success -> {
                    _status.value = LoadApiStatus.DONE
                }
                is Result.Error -> {
                    Log.d("status", "error")
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }

    ////////////////
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}