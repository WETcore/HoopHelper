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
import com.aqua.hoophelper.R
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

        //set game clock
        viewModel.setGameClockMin(binding.gameClockSec.text.toString().toLong())
        viewModel.gameClockSecTimer.start()
        viewModel.gameClockSec.observe(viewLifecycleOwner) {
            binding.gameClockSec.text = it.toString()
            viewModel.gameClockSec.value?.let { sec -> viewModel.setGameClockMin(sec) }
        }

        viewModel.gameClockMin.observe(viewLifecycleOwner) {
            binding.gameClockMin.text = it.toString()
        }

        return binding.root
    }


}