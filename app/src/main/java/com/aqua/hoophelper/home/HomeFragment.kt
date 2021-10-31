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
        binding.reboundLeaderCard.setOnClickListener {
            if (binding.reboundLeaderDetail.visibility == View.GONE) {
                binding.reboundLeaderDetail.visibility = View.VISIBLE
            } else {
                binding.reboundLeaderDetail.visibility = View.GONE
            }
        }
        binding.assistLeaderCard.setOnClickListener {
            if (binding.assistLeaderDetail.visibility == View.GONE) {
                binding.assistLeaderDetail.visibility = View.VISIBLE
            } else {
                binding.assistLeaderDetail.visibility = View.GONE
            }
        }
        binding.stealLeaderCard.setOnClickListener {
            if (binding.stealLeaderDetail.visibility == View.GONE) {
                binding.stealLeaderDetail.visibility = View.VISIBLE
            } else {
                binding.stealLeaderDetail.visibility = View.GONE
            }
        }
        binding.blockLeaderCard.setOnClickListener {
            if (binding.blockLeaderDetail.visibility == View.GONE) {
                binding.blockLeaderDetail.visibility = View.VISIBLE
            } else {
                binding.blockLeaderDetail.visibility = View.GONE
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

            binding.reboundDetailPts.text =
                viewModel.getLeaderDetailData(DataType.REBOUND, DetailDataType.PTS)
            binding.reboundDetailFg.text =
                viewModel.getLeaderDetailData(DataType.REBOUND, DetailDataType.FG)
            binding.reboundDetail3p.text =
                viewModel.getLeaderDetailData(DataType.REBOUND, DetailDataType.ThreeP)
            binding.reboundDetailFt.text =
                viewModel.getLeaderDetailData(DataType.REBOUND, DetailDataType.FT)
            binding.reboundDetailTov.text =
                viewModel.getLeaderDetailData(DataType.REBOUND, DetailDataType.TOV)
            binding.reboundDetailReb.text =
                viewModel.getLeaderDetailData(DataType.REBOUND, DetailDataType.REB)
            binding.reboundDetailAst.text =
                viewModel.getLeaderDetailData(DataType.REBOUND, DetailDataType.AST)
            binding.reboundDetailStl.text =
                viewModel.getLeaderDetailData(DataType.REBOUND, DetailDataType.STL)
            binding.reboundDetailBlk.text =
                viewModel.getLeaderDetailData(DataType.REBOUND, DetailDataType.BLK)
            binding.reboundDetailPf.text =
                viewModel.getLeaderDetailData(DataType.REBOUND, DetailDataType.PF)

            binding.assistDetailPts.text =
                viewModel.getLeaderDetailData(DataType.ASSIST, DetailDataType.PTS)
            binding.assistDetailFg.text =
                viewModel.getLeaderDetailData(DataType.ASSIST, DetailDataType.FG)
            binding.assistDetail3p.text =
                viewModel.getLeaderDetailData(DataType.ASSIST, DetailDataType.ThreeP)
            binding.assistDetailFt.text =
                viewModel.getLeaderDetailData(DataType.ASSIST, DetailDataType.FT)
            binding.assistDetailTov.text =
                viewModel.getLeaderDetailData(DataType.ASSIST, DetailDataType.TOV)
            binding.assistDetailReb.text =
                viewModel.getLeaderDetailData(DataType.ASSIST, DetailDataType.REB)
            binding.assistDetailAst.text =
                viewModel.getLeaderDetailData(DataType.ASSIST, DetailDataType.AST)
            binding.assistDetailStl.text =
                viewModel.getLeaderDetailData(DataType.ASSIST, DetailDataType.STL)
            binding.assistDetailBlk.text =
                viewModel.getLeaderDetailData(DataType.ASSIST, DetailDataType.BLK)
            binding.assistDetailPf.text =
                viewModel.getLeaderDetailData(DataType.ASSIST, DetailDataType.PF)

            binding.stealDetailPts.text =
                viewModel.getLeaderDetailData(DataType.STEAL, DetailDataType.PTS)
            binding.stealDetailFg.text =
                viewModel.getLeaderDetailData(DataType.STEAL, DetailDataType.FG)
            binding.stealDetail3p.text =
                viewModel.getLeaderDetailData(DataType.STEAL, DetailDataType.ThreeP)
            binding.stealDetailFt.text =
                viewModel.getLeaderDetailData(DataType.STEAL, DetailDataType.FT)
            binding.stealDetailTov.text =
                viewModel.getLeaderDetailData(DataType.STEAL, DetailDataType.TOV)
            binding.stealDetailReb.text =
                viewModel.getLeaderDetailData(DataType.STEAL, DetailDataType.REB)
            binding.stealDetailAst.text =
                viewModel.getLeaderDetailData(DataType.STEAL, DetailDataType.AST)
            binding.stealDetailStl.text =
                viewModel.getLeaderDetailData(DataType.STEAL, DetailDataType.STL)
            binding.stealDetailBlk.text =
                viewModel.getLeaderDetailData(DataType.STEAL, DetailDataType.BLK)
            binding.stealDetailPf.text =
                viewModel.getLeaderDetailData(DataType.STEAL, DetailDataType.PF)

            binding.blockDetailPts.text =
                viewModel.getLeaderDetailData(DataType.BLOCK, DetailDataType.PTS)
            binding.blockDetailFg.text =
                viewModel.getLeaderDetailData(DataType.BLOCK, DetailDataType.FG)
            binding.blockDetail3p.text =
                viewModel.getLeaderDetailData(DataType.BLOCK, DetailDataType.ThreeP)
            binding.blockDetailFt.text =
                viewModel.getLeaderDetailData(DataType.BLOCK, DetailDataType.FT)
            binding.blockDetailTov.text =
                viewModel.getLeaderDetailData(DataType.BLOCK, DetailDataType.TOV)
            binding.blockDetailReb.text =
                viewModel.getLeaderDetailData(DataType.BLOCK, DetailDataType.REB)
            binding.blockDetailAst.text =
                viewModel.getLeaderDetailData(DataType.BLOCK, DetailDataType.AST)
            binding.blockDetailStl.text =
                viewModel.getLeaderDetailData(DataType.BLOCK, DetailDataType.STL)
            binding.blockDetailBlk.text =
                viewModel.getLeaderDetailData(DataType.BLOCK, DetailDataType.BLK)
            binding.blockDetailPf.text =
                viewModel.getLeaderDetailData(DataType.BLOCK, DetailDataType.PF)
        }

        return binding.root
    }

}