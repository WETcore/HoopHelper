package com.aqua.hoophelper.team

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aqua.hoophelper.R
import com.aqua.hoophelper.databinding.TeamFragmentBinding
import com.aqua.hoophelper.util.Tactic
import com.google.android.material.tabs.TabLayoutMediator

class TeamFragment : Fragment() {

    private val viewModel: TeamViewModel by lazy {
        ViewModelProvider(this).get(TeamViewModel::class.java)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // binding
        val binding: TeamFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.team_fragment, container, false)

        // set tab
        val tabLayout = binding.tabLayout
        val vPager = binding.teamViewpager
        val title: List<String> = listOf("Manage", "Chart", "Tactic")
        val titleIcon: List<Drawable> = listOf(
            resources.getDrawable(R.drawable.planning, null),
            resources.getDrawable(R.drawable.bar_chart, null),
            resources.getDrawable(R.drawable.tactic, null),
        )
        vPager.adapter = TeamVPagerAdapter(requireActivity())

        tabLayout.tabTextColors =
            ColorStateList.valueOf(resources.getColor(R.color.basil_red, null))
        tabLayout.tabIconTint = ColorStateList.valueOf(resources.getColor(R.color.basil_red, null))

        TabLayoutMediator(tabLayout, vPager) { tab, position ->
            tab.text = title[position]
            tab.icon = titleIcon[position]
            vPager.offscreenPageLimit = 2 //OFFSCREEN_PAGE_LIMIT_DEFAULT
            vPager.setCurrentItem(tab.position, true)
        }.attach()

        Tactic.vPagerSwipe.observe(viewLifecycleOwner) {
            vPager.isUserInputEnabled = it
        }

        return binding.root
    }

}