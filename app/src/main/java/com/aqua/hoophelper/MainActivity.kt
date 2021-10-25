package com.aqua.hoophelper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.aqua.hoophelper.databinding.ActivityMainBinding
import com.aqua.hoophelper.match.MatchViewModel
import com.aqua.hoophelper.profile.RC_SIGN_IN
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth


    private val viewModel: MainActivityViewModel by lazy {
        ViewModelProvider(this).get(MainActivityViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // binding
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // nav host
        val navHostFragment = findNavController(R.id.nav_host)
        NavigationUI.setupWithNavController(binding.bottomBar,navHostFragment)
        binding.bottomBar.menu.getItem(2).isCheckable = false

        // FAB nav to match
        binding.fab.setOnClickListener {
            binding.toolbar.visibility = View.GONE
            binding.appBar.behavior.slideDown(binding.appBar)
            viewModel.getMatchInfo()
            viewModel.db.collection("Matches").add(viewModel.match)
            navHostFragment.navigate(NavigationDirections.navToMatch(viewModel.match.matchId))
        }

        navHostFragment.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.homeFragment -> {
                    binding.toolbar.visibility = View.VISIBLE
                    binding.appBar.behavior.slideUp(binding.appBar)
                }
                R.id.teamFragment -> {
                    binding.toolbar.visibility = View.VISIBLE
                    binding.appBar.behavior.slideUp(binding.appBar)
                }
                R.id.liveFragment -> {
                    binding.toolbar.visibility = View.VISIBLE
                    binding.appBar.behavior.slideUp(binding.appBar)
                }
                R.id.profileFragment -> {
                    binding.toolbar.visibility = View.VISIBLE
                    binding.appBar.behavior.slideUp(binding.appBar)
                }
            }
        }

//        // Configure Google Sign In
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//
//        // getting the value of gso inside the GoogleSignInClient
//        val googleSignInClient = GoogleSignIn.getClient(this, gso)

    }

    override fun onStart() {
        super.onStart()
        // Initialize Firebase Auth
        auth = Firebase.auth
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
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
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("login", "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {

    }
}