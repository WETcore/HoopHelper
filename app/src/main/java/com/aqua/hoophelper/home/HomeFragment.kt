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
    ): View? {

        // binding
        val binding: HomeFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.home_fragment, container,false)

        viewModel.status.observe(viewLifecycleOwner) {
            when(it) {
                LoadApiStatus.LOADING -> {
                    binding.processText.visibility = View.VISIBLE
                    binding.lottieViewpager.visibility = View.VISIBLE
                    binding.textInputLayout.visibility = View.GONE
                    binding.homeViewpager.visibility = View.INVISIBLE
                    binding.textView.visibility = View.INVISIBLE
                }
                LoadApiStatus.DONE -> {
                    binding.processText.visibility = View.GONE
                    binding.lottieViewpager.visibility = View.GONE
                    binding.textInputLayout.visibility = View.VISIBLE
                    binding.homeViewpager.visibility = View.VISIBLE
                    binding.textView.visibility = View.VISIBLE
                }
                LoadApiStatus.ERROR -> {

                }
            }
        }

        viewModel.teams.observe(viewLifecycleOwner) {
            val teamAdapter =
                ArrayAdapter(requireContext(), R.layout.home_team_item, viewModel.teamNameList)
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
            val adapter: HomeVPagerAdapter
            adapter = if (it.size >= 5) {
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
}