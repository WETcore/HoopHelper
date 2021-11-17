package com.aqua.hoophelper.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aqua.hoophelper.MainActivityViewModel
import com.aqua.hoophelper.R
import com.aqua.hoophelper.databinding.HomeFragmentBinding
import com.aqua.hoophelper.util.LoadApiStatus

class HomeFragment : Fragment() {


    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    private val mainViewModel: MainActivityViewModel by lazy {
        ViewModelProvider(this).get(MainActivityViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // binding
        val binding: HomeFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.home_fragment, container,false)

        viewModel.status.observe(viewLifecycleOwner) {
            when(it) {
                LoadApiStatus.LOADING -> {
                    binding.lottieViewpager.visibility = View.VISIBLE
                    binding.textInputLayout.visibility = View.GONE
                    binding.homeViewpager.visibility = View.INVISIBLE
                }
                LoadApiStatus.DONE -> {
                    binding.lottieViewpager.visibility = View.GONE
                    binding.textInputLayout.visibility = View.VISIBLE
                    binding.homeViewpager.visibility = View.VISIBLE
                }
                LoadApiStatus.ERROR -> {

                }
            }
        }

        viewModel.teams.observe(viewLifecycleOwner) {
            val teamAdapter =
                ArrayAdapter(requireContext(), R.layout.home_team_item, viewModel.teamNameList)
            binding.teamText.setAdapter(teamAdapter)
            binding.teamText.setText(viewModel.teams.value?.first()?.name, false)
        }

        // set selected Team
        binding.teamText.setOnItemClickListener { parent, view, position, id ->
            viewModel.selectedTeam(position)
        }

        // VPager
        viewModel.teamStat.observe(viewLifecycleOwner) {

            val leaderTypes = listOf("Score", "Rebound", "Assist", "Steal", "Block")
            if (it.size >= 5) {
                val adapter = HomeVPagerAdapter(leaderTypes, requireContext(), viewModel)
                binding.homeViewpager.apply {
                    this.offscreenPageLimit = 4
                    this.adapter = adapter
                    setPadding(190, 100, 100, 150)
                }
            } else {
                val adapter = HomeVPagerAdapter(leaderTypes, requireContext(), null)
                binding.homeViewpager.apply {
                    this.offscreenPageLimit = 4
                    this.adapter = adapter
                    setPadding(190, 100, 100, 150)
                }
            }
        }

        // crashlitics
        binding.crash.setOnClickListener {
            throw RuntimeException("Test Crash") // Force a crash
        }

        return binding.root
    }
}