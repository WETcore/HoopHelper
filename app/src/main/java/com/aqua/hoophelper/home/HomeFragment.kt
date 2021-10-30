package com.aqua.hoophelper.home

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.transition.TransitionManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.aqua.hoophelper.R
import com.aqua.hoophelper.databinding.HomeFragmentBinding
import com.aqua.hoophelper.match.DataType

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

        // spinner
        viewModel.getTeamList()
        viewModel.teams.observe(viewLifecycleOwner) {
            // TODO 設定預設球隊
            val teamAdapter =
                ArrayAdapter(requireContext(), R.layout.home_team_item, viewModel.teamNameList)
            binding.teamText.setAdapter(teamAdapter)
        }

        // set selected Team
        binding.teamText.setOnItemClickListener { parent, view, position, id ->
            viewModel.selectedTeam(position)
        }

        // expand card view
        binding.scoreLeaderCard.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.scoreLeaderCard)
        }

        viewModel.teamStat.observe(viewLifecycleOwner) {
            binding.scoreLeaderNum.text = viewModel.getLeaderboardData(DataType.SCORE)
            binding.assistLeaderNum.text = viewModel.getLeaderboardData(DataType.ASSIST)
            binding.reboundLeaderNum.text = viewModel.getLeaderboardData(DataType.REBOUND)
            binding.stealLeaderNum.text = viewModel.getLeaderboardData(DataType.STEAL)
            binding.blockLeaderNum.text = viewModel.getLeaderboardData(DataType.BLOCK)
        }

        return binding.root
    }

}