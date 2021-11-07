package com.aqua.hoophelper

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.aqua.hoophelper.database.Match
import com.aqua.hoophelper.databinding.ActivityMainBinding
import com.aqua.hoophelper.component.HoopService
import com.aqua.hoophelper.component.RestartBroadcastReceiver
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private val viewModel: MainActivityViewModel by lazy {
        ViewModelProvider(this).get(MainActivityViewModel::class.java)
    }

    @SuppressLint("RemoteViewLayout")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        stopService(Intent(applicationContext, HoopService::class.java))
        startService(Intent(applicationContext, HoopService::class.java))

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

        // badge
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

    override fun onDestroy() {
        stopService(Intent(applicationContext, HoopService::class.java))
        val brIntent = Intent()
        brIntent.action = "reStartService"
        brIntent.setClass(this, RestartBroadcastReceiver::class.java)
        this.sendBroadcast(brIntent)
        super.onDestroy()
    }

}