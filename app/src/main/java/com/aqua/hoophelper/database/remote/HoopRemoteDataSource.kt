package com.aqua.hoophelper.database.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aqua.hoophelper.database.*
import com.aqua.hoophelper.util.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object HoopRemoteDataSource : HoopRepository {

    override fun getEvents(): LiveData<List<Event>> {
        val result = MutableLiveData<List<Event>>()
        result.let {
            FirebaseFirestore.getInstance()
                .collection(EVENTS)
                .orderBy("actualTime", Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    it.value = value?.toObjects(Event::class.java) ?: mutableListOf()
                }
        }
        return result
    }

    override fun getMatches(): LiveData<List<Match>> {
        val result = MutableLiveData<List<Match>>()
        result.let {
            FirebaseFirestore.getInstance()
                .collection(MATCHES)
                .orderBy("actualTime", Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    it.value = value?.toObjects(Match::class.java) ?: mutableListOf()
                }
        }
        return result
    }

    override fun getRoster(): MutableLiveData<List<Player>> {
        val result = MutableLiveData<List<Player>>()
        result.let {
            FirebaseFirestore.getInstance()
                .collection(PLAYERS)
                .whereEqualTo("teamId", User.teamId)
                .addSnapshotListener { value, error ->
                    it.value = value?.toObjects(Player::class.java) ?: mutableListOf()
                }
        }
        return result
    }

    override fun getInvitations(): MutableLiveData<List<Invitation>> {
        val result = MutableLiveData<List<Invitation>>()
        result.let {
            FirebaseFirestore.getInstance()
                .collection(INVITATIONS)
                .whereEqualTo("inviteeMail", Firebase.auth.currentUser?.email)
                .addSnapshotListener { value, error ->
                    it.value = value?.toObjects(Invitation::class.java) ?: mutableListOf()
                }
        }
        return result
    }

    // captain participate the match. find captain's member
    override suspend fun getMatchMembers(): Result<List<Player>> = suspendCoroutine { conti ->
        val db = FirebaseFirestore.getInstance()
        db.collection(PLAYERS)
            .whereEqualTo("teamId", User.teamId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result?.toObjects(Player::class.java) ?: mutableListOf()
                    User.teamMembers = result
                    conti.resume(Result.Success(result))
                } else {
                    task.exception?.let {
                        Log.d(
                            "error",
                            "[${this::class.simpleName}] Error getting documents. ${it.message}"
                        )
                        conti.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                }
            }
    }

    override suspend fun getTeams(): Result<List<Team>> = suspendCoroutine { conti ->
        FirebaseFirestore.getInstance()
            .collection(TEAMS)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result?.toObjects(Team::class.java) ?: mutableListOf()
                    conti.resume(Result.Success(result))
                } else {
                    task.exception?.let {
                        Log.d(
                            "error",
                            "[${this::class.simpleName}] Error getting documents. ${it.message}"
                        )
                        conti.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                }
            }
    }

    override suspend fun getSelectedTeamMembers(): Result<List<Player>> =
        suspendCoroutine { conti ->
            FirebaseFirestore.getInstance()
                .collection(PLAYERS)
                .whereEqualTo("teamId", HoopInfo.spinnerSelectedTeamId.value)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val result = task.result?.toObjects(Player::class.java) ?: mutableListOf()
                        conti.resume(Result.Success(result))
                    } else {
                        task.exception?.let {
                            Log.d(
                                "error",
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                            conti.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                    }
                }
        }

    override suspend fun getPlayerData(playerId: String): Result<List<Event>> =
        suspendCoroutine { conti ->
            FirebaseFirestore.getInstance()
                .collection(EVENTS)
                .whereEqualTo("playerId", playerId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val result = task.result?.toObjects(Event::class.java) ?: mutableListOf()
                        conti.resume(Result.Success(result))
                    } else {
                        task.exception?.let {
                            Log.d(
                                "error",
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                            conti.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                    }
                }
        }

    override suspend fun getUserInfo(): List<Player> = suspendCoroutine { conti ->
        if (User.account != null) {
            FirebaseFirestore.getInstance()
                .collection(PLAYERS)
                .whereEqualTo("email", User.account?.email)
                .get()
                .addOnCompleteListener {
                    val result = it.result?.toObjects(Player::class.java) ?: mutableListOf()
                    if (result.size != 0) {
                        User.teamId = result.first().teamId
                        User.isCaptain = result.first().captain
                        User.id = result.first().id
                    }
                    Log.d("userInfo", "${User.account?.email}")
                    conti.resume(result)
                }
        }
    }

    override suspend fun getTeamInfo(): Team = suspendCoroutine { conti ->
        if (User.account != null) {
            FirebaseFirestore.getInstance()
                .collection(TEAMS)
                .whereEqualTo("id", User.teamId)
                .get()
                .addOnCompleteListener {
                    val result = it.result?.toObjects(Team::class.java)?.first() ?: Team()
                    conti.resume(result)
                }
        }
    }

    override suspend fun getRule(): Result<Rule> = suspendCoroutine { conti ->
        FirebaseFirestore.getInstance()
            .collection(RULE)
            .document(RULE_DOC)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result?.toObject(Rule::class.java) ?: Rule()
                    conti.resume(Result.Success(result))
                } else {
                    task.exception?.let {
                        Log.d(
                            "error",
                            "[${this::class.simpleName}] Error getting documents. ${it.message}"
                        )
                        conti.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                }
            }
    }

    override suspend fun getPlayer(): Result<Player> = suspendCoroutine { conti ->
        FirebaseFirestore.getInstance()
            .collection(PLAYERS)
            .whereEqualTo("id", User.id)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result.first().toObject(Player::class.java)
                    conti.resume(Result.Success(result))
                } else {
                    task.exception?.let {
                        Log.d(
                            "error",
                            "[${this::class.simpleName}] Error getting documents. ${it.message}"
                        )
                        conti.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                }
            }
    }

    override suspend fun getTeamEvents(): List<Event> = suspendCoroutine { conti ->
        FirebaseFirestore.getInstance()
            .collection(EVENTS)
            .whereEqualTo("teamId", User.teamId)
            .get()
            .addOnCompleteListener {
                val result = it.result.toObjects(Event::class.java)
                conti.resume(result)
            }
    }

}