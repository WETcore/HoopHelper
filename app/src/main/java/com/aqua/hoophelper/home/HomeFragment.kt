package com.aqua.hoophelper.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aqua.hoophelper.R
import com.aqua.hoophelper.databinding.HomeFragmentBinding
import com.aqua.hoophelper.util.LoadApiStatus

class HomeFragment : Fragment() {


    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // binding
        val binding: HomeFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)

        viewModel.status.observe(viewLifecycleOwner) {
            when (it) {
                LoadApiStatus.LOADING -> {
                    setLoadingVisibility(binding)
                }
                LoadApiStatus.DONE -> {
                    setDoneVisibility(binding)
                }
                LoadApiStatus.ERROR -> {

                }
            }
        }

        viewModel.teams.observe(viewLifecycleOwner) {
            val teamAdapter =
                ArrayAdapter(requireContext(), R.layout.home_team_item, viewModel.teamNames)

            binding.teamText.apply {
                setAdapter(teamAdapter)
                setText(viewModel.teams.value?.first()?.name, false)
            }
        }

        // set selected Team
        binding.teamText.setOnItemClickListener { parent, view, position, id ->
            viewModel.selectedTeam(position)
        }

        // VPager
        viewModel.teamStat.observe(viewLifecycleOwner) {
            val adapter = if (it.size >= 5) {
                HomeVPagerAdapter(viewModel.leaderTypes, requireContext(), viewModel)
            } else {
                HomeVPagerAdapter(viewModel.leaderTypes, requireContext(), null)
            }

            binding.homeViewpager.apply {
                this.offscreenPageLimit = 4
                this.adapter = adapter
                setPadding(190, 100, 100, 150)
            }
        }

        // crashlytics
        binding.crash.setOnClickListener {
            // Force a crash
            throw RuntimeException("Test Crash")
        }

        return binding.root
    }

    private fun setDoneVisibility(binding: HomeFragmentBinding) {
        binding.apply {
            processText.visibility = View.GONE
            lottieViewpager.visibility = View.GONE
            textInputLayout.visibility = View.VISIBLE
            homeViewpager.visibility = View.VISIBLE
            textView.visibility = View.VISIBLE
        }
    }

    private fun setLoadingVisibility(binding: HomeFragmentBinding) {
        binding.apply {
            processText.visibility = View.VISIBLE
            lottieViewpager.visibility = View.VISIBLE
            textInputLayout.visibility = View.GONE
            homeViewpager.visibility = View.INVISIBLE
            textView.visibility = View.INVISIBLE
        }
    }
}