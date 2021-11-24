package com.aqua.hoophelper.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.User
import com.aqua.hoophelper.database.Invitation
import com.aqua.hoophelper.database.Player
import com.aqua.hoophelper.database.Result
import com.aqua.hoophelper.database.Team
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource
import com.aqua.hoophelper.util.INVITATIONS
import com.aqua.hoophelper.util.LoadApiStatus
import com.aqua.hoophelper.util.PLAYERS
import com.aqua.hoophelper.util.TEAMS
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    val _status = MutableLiveData<LoadApiStatus?>()
    val status: LiveData<LoadApiStatus?>
        get() = _status

    // Firebase
    val db = FirebaseFirestore.getInstance()

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
    var _authToggle = MutableLiveData<Boolean>(false)
    val authToggle: LiveData<Boolean>
        get() = _authToggle

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getUserInfo()
        setRoster()
    }

    fun sendTeamInfo(teamName: String, playerNum: String) {
        team.name = teamName
        team.jerseyNumbers = mutableListOf(playerNum, "T1", "T2", "T3", "T4", "T5")
        team.id = db.collection(TEAMS).document().id
        db.collection(TEAMS).add(team)
    }

    fun sendCaptainInfo(number: String, name: String) {
        player.email = User.account?.email ?: "error"
        player.id = db.collection(PLAYERS).document().id
        player.teamId = team.id
        player.number = number
        player.name = name
        player.captain = true
        player.avatar = Firebase.auth.currentUser?.photoUrl.toString()
        db.collection(PLAYERS).add(player)
        val botPlayer = Player()
        botPlayer.apply {
            avatar =
                "https://images.unsplash.com/photo-1546776310-eef45dd6d63c?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1410&q=80"
            teamId = team.id
            listOf("T1", "T2", "T3", "T4", "T5").forEachIndexed { index, s ->
                starting5[index] = true
                id = s
                botPlayer.name = s
                botPlayer.number = s
                db.collection(PLAYERS).add(botPlayer)
            }
        }
    }


    var initState = true
    fun setRoster() {
        _status.value = LoadApiStatus.LOADING
        coroutineScope.launch {
            when (val result = HoopRemoteDataSource.getMatchMembers()) { //TODO
                is Result.Success -> {
                    _roster.value = result.data ?: listOf()
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

    var releasePos = 0
    fun removePlayer(): Boolean {
        val buffer = roster.value?.get(releasePos) ?: Player()
        return if (buffer.id != userInfo.value?.id) { // remove normal player
            db.collection(PLAYERS)
                .whereEqualTo("id", buffer.id)
                .get().addOnSuccessListener {
                    it.documents.first().reference.delete()
                }
            setRoster()
            false
        } else {
            true
        }
    }

    fun sendInvitation(mail: String, name: String) {

        coroutineScope.launch {
            invitation.id = db.collection(INVITATIONS).document().id
            invitation.teamId = User.teamId
            invitation.inviteeMail = mail
            invitation.playerName = name
            invitation.existingNumbers = HoopRemoteDataSource.getTeamInfo().jerseyNumbers

            db.collection(INVITATIONS).add(invitation)
        }
    }

    fun getPlayerUserInfo() {
        _status.value = LoadApiStatus.LOADING
        coroutineScope.launch {
            when (val result = HoopRemoteDataSource.getTeams()) {
                is Result.Success -> {
                    _teamInfo.value = result.data.firstOrNull { it.id == User.teamId }
                }
                is Result.Error -> {
                    Log.d("status", "error")
                    _status.value = LoadApiStatus.ERROR
                }
            }
            when (val result = HoopRemoteDataSource.getPlayer()) {
                is Result.Success -> {
                    _userInfo.value = result.data ?: Player()
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
        coroutineScope.launch {
            HoopRemoteDataSource.getUserInfo()
            HoopRemoteDataSource.getMatchMembers()
            _authToggle.value = !(_authToggle.value ?: false)
        }
    }

    fun setNewCaptain(id: MutableList<String>, position: Int) {
        db.collection(PLAYERS)
            .whereEqualTo("id", roster.value?.get(releasePos)?.id)
            .get().addOnSuccessListener {
                it.documents.first().reference.delete()
            }
        db.collection(PLAYERS)
            .whereEqualTo("id", id[position])
            .get().addOnSuccessListener {
                it.documents.first().reference.update("captain", true)
            }
        db.collection(TEAMS)
            .whereEqualTo("id", User.teamId)
            .get().addOnSuccessListener {
                it.documents.first().reference.update(
                    "jerseyNumbers",
                    FieldValue.arrayRemove(roster.value?.get(releasePos)?.number)
                )
            }
    }

}