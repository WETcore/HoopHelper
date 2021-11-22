package com.aqua.hoophelper

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Path
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.PathInterpolator
import androidx.annotation.RequiresApi
import androidx.core.animation.doOnEnd
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.airbnb.lottie.LottieAnimationView
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

        // nav host TODO
        val navHostFragment = findNavController(R.id.login_nav_host)

//        navHostFragment.navigate(R.id.action_loginFragment_to_tutorFragment)

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
//        enterTutorUI(user)
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
            findNavController(R.id.login_nav_host).navigate(R.id.action_loginFragment_to_tutorFragment)
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