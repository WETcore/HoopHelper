package com.aqua.hoophelper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.aqua.hoophelper.databinding.ActivityMainBinding
import com.aqua.hoophelper.match.MatchViewModel

class MainActivity : AppCompatActivity() {

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
            navHostFragment.navigate(NavigationDirections.navToMatch())
            binding.toolbar.visibility = View.GONE
            binding.appBar.behavior.slideDown(binding.appBar)
            viewModel.getMatchInfo()
//            viewModel.db.collection("Matches").add(viewModel.match)
        }

        navHostFragment.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.homeFragment -> {
                    binding.toolbar.visibility = View.VISIBLE
                    binding.appBar.behavior.slideUp(binding.appBar)
                }
                R.id.chartFragment -> {
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
    }
}