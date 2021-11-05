package com.aqua.hoophelper

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.aqua.hoophelper.database.Match
import com.aqua.hoophelper.database.Player
import com.aqua.hoophelper.databinding.ActivityMainBinding
import com.aqua.hoophelper.match.MatchViewModel
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
import com.google.firebase.firestore.Query


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
            viewModel.setMatchInfo()
            navHostFragment.navigate(NavigationDirections.navToMatch(viewModel.match.matchId))
        }

        ///badge
        binding.bottomBar.getOrCreateBadge(R.id.liveFragment).apply {
            viewModel.db.collection("Matches") // TODO to model
                .addSnapshotListener { value, error ->
                    var mlist = value?.toObjects(Match::class.java)?.sortedBy {
                        it.actualTime
                    }
                    isVisible = if (mlist.isNullOrEmpty()) {
                        false
                    } else {
                        mlist.last()?.gaming == true
                    }
                }
        }


        navHostFragment.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.homeFragment -> {
                    binding.toolbar.visibility = View.GONE
                    binding.appBar.behavior.slideUp(binding.appBar)
                }
                R.id.teamFragment -> {
                    binding.toolbar.visibility = View.GONE
                    binding.appBar.behavior.slideUp(binding.appBar)
                }
                R.id.liveFragment -> {//TODO ban fab
                    binding.toolbar.visibility = View.GONE
                    binding.appBar.behavior.slideUp(binding.appBar)
                }
                R.id.profileFragment -> {
                    binding.toolbar.visibility = View.GONE
                    binding.appBar.behavior.slideUp(binding.appBar)
                }
            }
        }
    }

}