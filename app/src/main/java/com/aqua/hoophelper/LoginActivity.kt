package com.aqua.hoophelper

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Path
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.animation.PathInterpolator
import androidx.annotation.RequiresApi
import androidx.core.animation.doOnEnd
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.aqua.hoophelper.database.Player
import com.aqua.hoophelper.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

const val RC_SIGN_IN = 0

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private val viewModel: LoginActivityViewModel by lazy {
        ViewModelProvider(this).get(LoginActivityViewModel::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // binding
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // getting the value of gso inside the GoogleSignInClient
        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        // animation & login
        val displayMetrics = DisplayMetrics()
        this@LoginActivity.display?.getRealMetrics(displayMetrics)
        val width = displayMetrics.widthPixels.toFloat()
        val height = displayMetrics.heightPixels.toFloat()
        val path = Path()
        path.moveTo(32f, height-32f)

        binding.googleSignIn.setOnClickListener {
            viewModel.signIn(googleSignInClient,this)
        }

        binding.loginFab.setOnClickListener {
            path.apply {
                quadTo(width*0.25f, -1000f,width*0.5f - 120f,height* 0.5f - 60f)
            }
            val animator = ObjectAnimator.ofFloat(binding.loginFab, View.X, View.Y, path).apply {
                duration = 1500
                start()
            }
            animator.doOnEnd {
                viewModel.signIn(googleSignInClient,this)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Initialize Firebase Auth
        auth = Firebase.auth
        // Check if user is signed in (non-null) and update UI accordingly.
        val user = auth.currentUser
        User.account = user
        Log.d("currentUser","${user?.email}")
        viewModel.getUserInfo()
        updateUI(user)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Log.d("login", "firebaseAuthWithGoogle:" + account.email)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("login", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("login", "signInWithCredential:success")
                    val user = auth.currentUser
                    User.account = user
                    viewModel.getUserInfo()
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("login", "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}

object User {
    var account: FirebaseUser? = null
    var teamId = ""
    var teamMembers = listOf<Player>()
    var isCaptain = false
    var id = ""
}

object HoopInfo {
    var spinnerSelectedTeamId = MutableLiveData("")
    var matchId = ""
}