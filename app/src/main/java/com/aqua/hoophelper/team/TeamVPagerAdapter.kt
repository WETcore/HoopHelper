package com.aqua.hoophelper.team

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.aqua.hoophelper.team.child.chart.ChartFragment
import com.aqua.hoophelper.team.child.manage.ManageFragment
import com.aqua.hoophelper.team.child.tactic.TacticFragment

class TeamVPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 ->  ChartFragment()
            1 ->  ManageFragment()
            2 ->  TacticFragment()
            else -> TacticFragment()
        }
    }
}