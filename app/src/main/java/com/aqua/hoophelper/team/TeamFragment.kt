package com.aqua.hoophelper.team

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.aqua.hoophelper.R
import com.aqua.hoophelper.databinding.TeamFragmentBinding
import com.google.android.material.tabs.TabLayoutMediator

class TeamFragment : Fragment() {

    private val viewModel: TeamViewModel by lazy {
        ViewModelProvider(this).get(TeamViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // binding
        val binding: TeamFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.team_fragment, container,false)

        val tabLayout = binding.tabLayout
        val vPager = binding.teamViewpager

        val title: List<String> = listOf("Chart", "Manage", "Tactic")

        vPager.adapter = TeamVPagerAdapter(requireActivity())

        TabLayoutMediator(tabLayout, vPager) { tab, position ->
            tab.text = title[position]
            vPager.setCurrentItem(tab.position, true)
        }.attach()

        return binding.root
    }
}