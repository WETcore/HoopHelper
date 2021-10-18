package com.aqua.hoophelper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.aqua.hoophelper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // binding
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // nav host
        val navHostFragment = findNavController(R.id.nav_host)
        NavigationUI.setupWithNavController(binding.bottomBar,navHostFragment)

        binding.bottomBar.menu.getItem(2).isCheckable = false
//        binding.bottomBar.menu.getItem(2).isVisible = false

        // nav to match
        binding.fab.setOnClickListener {
            binding.appBar.behavior.slideDown(binding.appBar)
            navHostFragment.navigate(NavigationDirections.navToMatch())
        }

        navHostFragment.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.homeFragment -> {
                    binding.appBar.behavior.slideUp(binding.appBar)
                }
                R.id.chartFragment -> {
                    binding.appBar.behavior.slideUp(binding.appBar)
                }
                R.id.liveFragment -> {
                    binding.appBar.behavior.slideUp(binding.appBar)
                }
                R.id.profileFragment -> {
                    binding.appBar.behavior.slideUp(binding.appBar)
                }
            }
        }
    }
}