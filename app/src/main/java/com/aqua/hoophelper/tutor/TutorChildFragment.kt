package com.aqua.hoophelper.tutor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aqua.hoophelper.R
import com.aqua.hoophelper.databinding.TutorChildFragmentBinding

class TutorChildFragment(private val tutorTypeFilter: TutorTypeFilter) : Fragment() {

    private val viewModel: TutorChildViewModel by lazy {
        ViewModelProvider(this).get(TutorChildViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // binding
        val binding: TutorChildFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.tutor_child_fragment, container,false)

        when(tutorTypeFilter) {
            TutorTypeFilter.CREATE_TEAM -> {
                binding.startButton.visibility = View.GONE
            }
            TutorTypeFilter.MANAGE_TEAM -> {
                binding.startButton.visibility = View.GONE
            }
            TutorTypeFilter.RECORD -> {
                binding.startButton.visibility = View.GONE
            }
            TutorTypeFilter.CHART -> {
                binding.startButton.visibility = View.GONE
            }
            TutorTypeFilter.TACTIC -> {
                binding.startButton.visibility = View.GONE
            }
            TutorTypeFilter.LIVE -> {
                binding.startButton.visibility = View.GONE
            }
            TutorTypeFilter.LEADERBOARD -> {
                binding.lottieSlide.visibility = View.GONE
                binding.startButton.visibility = View.VISIBLE
            }
        }

        binding.startButton.setOnClickListener {
            Tutor.finished.value = true
        }

        return binding.root
    }
}