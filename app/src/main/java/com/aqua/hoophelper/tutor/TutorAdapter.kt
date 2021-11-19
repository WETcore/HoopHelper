package com.aqua.hoophelper.tutor

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class TutorAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = TutorTypeFilter.values().size

    override fun createFragment(position: Int): Fragment {
        return TutorChildFragment(TutorTypeFilter.values()[position])
    }
}