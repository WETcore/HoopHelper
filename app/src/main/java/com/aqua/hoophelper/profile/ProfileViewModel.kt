package com.aqua.hoophelper.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aqua.hoophelper.User
import com.aqua.hoophelper.database.Invitation
import com.aqua.hoophelper.database.Player
import com.aqua.hoophelper.database.Team
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.URI
import java.net.URL

const val RC_SIGN_IN = 0

class ProfileViewModel : ViewModel() {

    // Firebase
    val db = FirebaseFirestore.getInstance()

    var player = Player()
    var team = Team()
    var invitation =Invitation()

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

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun sendTeamInfo(teamName: String, playerNum: String) {
        team.name = teamName
        team.jerseyNumbers = mutableListOf(playerNum)
        team.id = db.collection("Teams").document().id
        db.collection("Teams").add(team)
    }

    fun sendCaptainInfo(number: String, name: String) {
        player.email = User.account?.email ?: "error"
        player.id = db.collection("Players").document().id
        player.teamId = team.id
        player.number = number
        player.name = name
        player.captain = true
        player.avatar = Firebase.auth.currentUser?.photoUrl.toString()
        db.collection("Players").add(player)
    }

    fun setRoster() {
        coroutineScope.launch {
            _roster.value = HoopRemoteDataSource.getMatchMembers()
            Log.d("roster", "Hi ${roster.value}")
        }
    }

    var releasePos = 0
    fun removePlayer(spinnerPos: Int) {
        val buffer = roster.value!![spinnerPos]
        db.collection("Players")
            .whereEqualTo("id", buffer.id)
            .get().addOnSuccessListener {
                it.documents.first().reference.delete()
            }
        setRoster()
    }

    fun sendInvitation(mail: String, name: String) {

        coroutineScope.launch {
            invitation.id = db.collection("Invitations").document().id
            invitation.teamId = User.teamId
            invitation.inviteeMail = mail// + binding.mailLayout.suffixText
            invitation.playerName = name
            invitation.existingNumbers = HoopRemoteDataSource.getTeamInfo().jerseyNumbers

            db.collection("Invitations").add(invitation)
        }
    }

    fun getUserInfo() {
        coroutineScope.launch {
            _teamInfo.value = HoopRemoteDataSource.getTeams().firstOrNull { it.id == User.teamId }
            _userInfo.value = HoopRemoteDataSource.getPlayer()
        }
    }

}