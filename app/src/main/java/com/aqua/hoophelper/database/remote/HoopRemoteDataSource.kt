package com.aqua.hoophelper.database.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aqua.hoophelper.database.*
import com.aqua.hoophelper.util.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object HoopRemoteDataSource : HoopDataSource {

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

    override fun getMatches(teamId: String): LiveData<List<Match>> {
        val result = MutableLiveData<List<Match>>()
        result.let {
            FirebaseFirestore.getInstance()
                .collection(MATCHES)
                .addSnapshotListener { value, error ->
                    val matches = value?.toObjects(Match::class.java)?.sortedBy { it.actualTime }
                    Badge.isGaming.value = if (matches.isNullOrEmpty()) {
                        false
                    } else {
                        matches.lastOrNull { it.teamId == teamId }?.gaming == true
                    }
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
            FirebaseFirestore.getInstance().collection(PLAYERS)
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

    override suspend fun getTeamInfo(): Result<Team> = suspendCoroutine { conti ->
        if (User.account != null) {
            FirebaseFirestore.getInstance()
                .collection(TEAMS)
                .whereEqualTo("id", User.teamId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val result = task.result?.toObjects(Team::class.java)?.first() ?: Team()
                        User.teamJerseyNumbers = result.jerseyNumbers
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

    override suspend fun setTeamInfo(team: Team): Result<Boolean> = suspendCoroutine { conti ->
        val teams = FirebaseFirestore.getInstance().collection(TEAMS)

        team.id = teams.document().id
        User.teamId = team.id

        teams.document()
            .set(team)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    conti.resume(Result.Success(true))
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

    override suspend fun setCaptainInfo(player: Player): Result<Boolean> = suspendCoroutine { conti ->
        val players = FirebaseFirestore.getInstance().collection(PLAYERS)

        player.id = players.document().id
        player.teamId = User.teamId
        User.id = player.id
        User.isCaptain = true

        players.document()
            .set(player)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    conti.resume(Result.Success(true))
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

    override suspend fun setMockTeammate(player: Player): Result<Boolean> = suspendCoroutine { conti ->
        val players = FirebaseFirestore.getInstance().collection(PLAYERS)
        players.document()
            .set(player)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    conti.resume(Result.Success(true))
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

    override suspend fun deletePlayer(player: Player): Result<Boolean> = suspendCoroutine { conti ->
        val players = FirebaseFirestore.getInstance().collection(PLAYERS)
        players.whereEqualTo("id", player.id)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.documents.first().reference.delete()
                    conti.resume(Result.Success(true))
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

    override suspend fun setInvitationInfo(invitation: Invitation): Result<Boolean> = suspendCoroutine { conti ->
        val invitations = FirebaseFirestore.getInstance().collection(INVITATIONS)
        invitation.id = invitations.document().id

        invitations.document()
            .set(invitation)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    conti.resume(Result.Success(true))
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

    override suspend fun updateCaptain(playerId: String): Result<Boolean> = suspendCoroutine { conti ->
        val players = FirebaseFirestore.getInstance().collection(PLAYERS)

        players.whereEqualTo("id",playerId)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.documents.first().reference.update("captain", true)
                    conti.resume(Result.Success(true))
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

    override suspend fun updateJerseyNumbers(number: String): Result<Boolean> = suspendCoroutine { conti ->
        val teams = FirebaseFirestore.getInstance().collection(TEAMS)

        teams.whereEqualTo("id", User.teamId)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.documents.first().reference.update(
                    "jerseyNumbers",
                    FieldValue.arrayRemove(number)
                )
                    conti.resume(Result.Success(true))
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

    override suspend fun setEvent(event: Event): Result<Boolean> = suspendCoroutine { conti ->
        val events = FirebaseFirestore.getInstance().collection(EVENTS)
        event.eventId = events.document().id

        events.document()
            .set(event)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    conti.resume(Result.Success(true))
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

    override suspend fun deleteEvent(): Result<Boolean> = suspendCoroutine { conti ->
        val events = FirebaseFirestore.getInstance().collection(EVENTS)
        events.orderBy("actualTime", Query.Direction.DESCENDING)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.documents.first().reference.delete()
                    conti.resume(Result.Success(true))
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

    override suspend fun setRule(rule: Rule): Result<Boolean> = suspendCoroutine { conti ->
        val rules = FirebaseFirestore.getInstance().collection(RULE)
        rules.document(RULE_DOC)
            .set(rule).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    conti.resume(Result.Success(true))
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

    override suspend fun updateLineup(subPlayer: Player, startPlayer: Player, position: Int): Result<Boolean> = suspendCoroutine { conti ->
        val players = FirebaseFirestore.getInstance().collection(PLAYERS)
        val bufferLineups = mutableListOf(false, false, false, false, false)

        players.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.documents.apply {
                        bufferLineups[position] = true
                        first { it["id"] == subPlayer.id }.reference.update("starting5", bufferLineups)
                        bufferLineups[position] = false
                        first { it["id"] == startPlayer.id }.reference.update("starting5", bufferLineups)
                    }
                    conti.resume(Result.Success(true))
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

    override suspend fun setMatchInfo(match: Match): Result<Boolean> = suspendCoroutine { conti ->
        val matches = FirebaseFirestore.getInstance().collection(MATCHES)
        match.matchId = matches.document().id
        HoopInfo.matchId = match.matchId

        matches.document()
            .set(match).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    conti.resume(Result.Success(true))
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

    override suspend fun updateMatchStatus(match: Match): Result<Boolean> = suspendCoroutine { conti ->
        val matches = FirebaseFirestore.getInstance().collection(MATCHES)

        matches.whereEqualTo("matchId", match.matchId)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.documents.first().reference.update("gaming", false)
                    conti.resume(Result.Success(true))
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

}