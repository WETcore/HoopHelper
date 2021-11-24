package com.aqua.hoophelper

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.aqua.hoophelper.databinding.ActivityMainBinding
import com.aqua.hoophelper.util.HoopService
import com.aqua.hoophelper.util.RestartBroadcastReceiver
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

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

        // badge
        HoopInfo.spinnerSelectedTeamId.observe(this) {
            viewModel.showBadge(it)
        }
        viewModel.badgeSwitch.observe(this) {
            binding.bottomBar.getOrCreateBadge(R.id.liveFragment).isVisible = it
        }

        // nav host
        val navHostFragment = findNavController(R.id.nav_host)
        binding.bottomBar.setupWithNavController(navHostFragment)

        // FAB nav to match
        binding.fab.setOnClickListener {
            when {
                User.teamId.length <= 5 -> {
                    Snackbar.make(binding.root, "No team no game~", Snackbar.LENGTH_SHORT)
                        .setAction("create team") {
                            binding.bottomBar.menu.getItem(4).isChecked = true
                            navHostFragment.navigate(NavigationDirections.navToProfile())
                        }.apply {
                            setTextColor(resources.getColor(R.color.basil_orange, null))
                            setActionTextColor(resources.getColor(R.color.basil_green_dark, null))
                            setBackgroundTint(resources.getColor(R.color.basil_bg, null))
                        }.show()
                }
                User.teamMembers.size <= 5 -> {
                    Snackbar.make(binding.root, "At least 5 players", Snackbar.LENGTH_SHORT)
                        .setAction("assemble") {
                            binding.bottomBar.menu.getItem(4).isChecked = true
                            navHostFragment.navigate(NavigationDirections.navToProfile())
                        }.apply {
                            setTextColor(resources.getColor(R.color.basil_orange, null))
                            setActionTextColor(resources.getColor(R.color.basil_green_dark, null))
                            setBackgroundTint(resources.getColor(R.color.basil_bg, null))
                        }.show()
                }
                else -> {
                    binding.appBar.behavior.slideDown(binding.appBar)
                    viewModel.setMatchInfo()
                    binding.fab.isClickable = false
                    binding.bottomBar.menu.getItem(2).isChecked = true
                    navHostFragment.navigate(NavigationDirections.navToMatch(viewModel.match.matchId))
                }
            }
        }

        binding.bottomBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> {
                    binding.fab.isClickable = true
                    binding.appBar.behavior.slideUp(binding.appBar)
                    navHostFragment.navigate(NavigationDirections.navToHome())
                    true
                }
                R.id.teamFragment -> {
                    when {
                        User.teamId.length <= 5 -> {
                            binding.fab.isClickable = true
                            Snackbar.make(binding.root, "No team no game~", Snackbar.LENGTH_SHORT)
                                .setAction("create team") {
                                    binding.bottomBar.menu.getItem(4).isChecked = true
                                    navHostFragment.navigate(NavigationDirections.navToProfile())
                                }.apply {
                                    setTextColor(resources.getColor(R.color.basil_orange, null))
                                    setActionTextColor(
                                        resources.getColor(
                                            R.color.basil_green_dark,
                                            null
                                        )
                                    )
                                    setBackgroundTint(resources.getColor(R.color.basil_bg, null))
                                }.show()
                            false
                        }
                        User.teamMembers.size <= 5 -> {
                            binding.fab.isClickable = true
                            Snackbar.make(binding.root, "At least 5 players", Snackbar.LENGTH_SHORT)
                                .setAction("assemble") {
                                    binding.bottomBar.menu.getItem(4).isChecked = true
                                    navHostFragment.navigate(NavigationDirections.navToProfile())
                                }.apply {
                                    setTextColor(resources.getColor(R.color.basil_orange, null))
                                    setActionTextColor(
                                        resources.getColor(
                                            R.color.basil_green_dark,
                                            null
                                        )
                                    )
                                    setBackgroundTint(resources.getColor(R.color.basil_bg, null))
                                }.show()
                            false
                        }
                        else -> {
                            binding.fab.isClickable = true
                            binding.appBar.behavior.slideUp(binding.appBar)
                            navHostFragment.navigate(NavigationDirections.navToTeam())
                            true
                        }
                    }
                }
                R.id.liveFragment -> {
                    binding.fab.isClickable = false
                    binding.appBar.behavior.slideUp(binding.appBar)
                    navHostFragment.navigate(NavigationDirections.navToLive(viewModel.badgeSwitch.value ?: false))
                    true
                }
                R.id.profileFragment -> {
                    binding.fab.isClickable = true
                    binding.appBar.behavior.slideUp(binding.appBar)
                    navHostFragment.navigate(NavigationDirections.navToProfile())
                    true
                }
                else -> false
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


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        findViewById<View>(R.id.fab).isClickable = true
        findViewById<BottomAppBar>(R.id.app_bar).let {
            it.behavior.slideUp(it)
        }
        if (findViewById<BottomNavigationView>(R.id.bottom_bar).menu.getItem(2).isChecked) {
            viewModel.exitMatch()
        }
        return super.onKeyDown(keyCode, event)
    }
}