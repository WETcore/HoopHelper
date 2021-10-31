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
import com.aqua.hoophelper.match.DetailDataType

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
            if (binding.scoreLeaderDetail.visibility == View.GONE) {
                binding.scoreLeaderDetail.visibility = View.VISIBLE
            } else {
                binding.scoreLeaderDetail.visibility = View.GONE
            }
        }

        viewModel.teamStat.observe(viewLifecycleOwner) {
            binding.scoreLeaderNum.text = viewModel.getLeaderboardData(DataType.SCORE)
            binding.assistLeaderNum.text = viewModel.getLeaderboardData(DataType.ASSIST)
            binding.reboundLeaderNum.text = viewModel.getLeaderboardData(DataType.REBOUND)
            binding.stealLeaderNum.text = viewModel.getLeaderboardData(DataType.STEAL)
            binding.blockLeaderNum.text = viewModel.getLeaderboardData(DataType.BLOCK)

            binding.scoreDetailPts.text =
                viewModel.getLeaderDetailData(DataType.SCORE, DetailDataType.PTS)
            binding.scoreDetailFg.text =
                viewModel.getLeaderDetailData(DataType.SCORE, DetailDataType.FG)
            binding.scoreDetail3p.text =
                viewModel.getLeaderDetailData(DataType.SCORE, DetailDataType.ThreeP)
            binding.scoreDetailFt.text =
                viewModel.getLeaderDetailData(DataType.SCORE, DetailDataType.FT)
            binding.scoreDetailTov.text =
                viewModel.getLeaderDetailData(DataType.SCORE, DetailDataType.TOV)
            binding.scoreDetailReb.text =
                viewModel.getLeaderDetailData(DataType.SCORE, DetailDataType.REB)
            binding.scoreDetailAst.text =
                viewModel.getLeaderDetailData(DataType.SCORE, DetailDataType.AST)
            binding.scoreDetailStl.text =
                viewModel.getLeaderDetailData(DataType.SCORE, DetailDataType.STL)
            binding.scoreDetailBlk.text =
                viewModel.getLeaderDetailData(DataType.SCORE, DetailDataType.BLK)
            binding.scoreDetailPf.text =
                viewModel.getLeaderDetailData(DataType.SCORE, DetailDataType.PF)
        }

        return binding.root
    }

}