package com.aqua.hoophelper.match

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.aqua.hoophelper.NavigationDirections
import com.aqua.hoophelper.R
import com.aqua.hoophelper.database.Event
import com.aqua.hoophelper.databinding.HomeFragmentBinding
import com.aqua.hoophelper.databinding.MatchFragmentBinding
import com.aqua.hoophelper.home.HomeViewModel

class MatchFragment : Fragment() {


    private val viewModel: MatchViewModel by lazy {
        ViewModelProvider(this).get(MatchViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // binding
        val binding: MatchFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.match_fragment, container,false)

        // set shot clock
        viewModel.shotClockTimer.start()
        viewModel.shotClock.observe(viewLifecycleOwner) {
            binding.shotClock.text = it.toString()
        }

        // set game clock
        viewModel.setGameClockMin(binding.gameClockSec.text.toString().toLong())
        viewModel.gameClockSecTimer.start()
        viewModel.gameClockSec.observe(viewLifecycleOwner) {
            binding.gameClockSec.text = it.toString()
            viewModel.gameClockSec.value?.let { sec -> viewModel.setGameClockMin(sec) }
        }

        viewModel.gameClockMin.observe(viewLifecycleOwner) {
            binding.gameClockMin.text = it.toString()
        }

        // set pause
        binding.pauseMatchChip.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                viewModel.shotClockTimer.cancel()
                viewModel.gameClockSecTimer.cancel()
            } else {
                viewModel.shotClockTimer.start()
                viewModel.gameClockSecTimer.start()
            }
            binding.shotClock.text = viewModel.shotClock.value.toString()
            binding.gameClockSec.text = viewModel.gameClockSec.value.toString()
        }
        
        // count free throw
        binding.freeThrowChip.setOnClickListener {

        }

        // set exit
        binding.exitMatchButton.setOnClickListener {
            viewModel.shotClockTimer.cancel()
            viewModel.gameClockSecTimer.cancel()
            findNavController().navigate(NavigationDirections.navToHome())
        }


        // record data
        binding.zone1Button.setOnClickListener {
            viewModel.setScoreData(1)
            Log.d("record","zone1 ${viewModel.events.zone}")
        }

        binding.zone2Button.setOnClickListener {
            viewModel.setScoreData(2)
            Log.d("record","zone2 ${viewModel.events.zone}")
        }

        return binding.root
    }


}