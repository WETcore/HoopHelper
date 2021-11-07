package com.aqua.hoophelper.home

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import android.view.HapticFeedbackConstants
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.aqua.hoophelper.NavigationDirections
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
            Log.d("redo2","Hi ${it}")

            val adapter = HomeVPagerAdapter(viewModel.teamStat.value!!, requireContext(), viewModel)

            binding.homeViewpager.apply {
                this.offscreenPageLimit = 4
                this.adapter?.notifyDataSetChanged()
                this.adapter = adapter
                setPadding(190, 100, 100, 150)
            }
        }
        return binding.root
    }
}