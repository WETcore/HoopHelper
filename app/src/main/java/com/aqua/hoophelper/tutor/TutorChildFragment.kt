package com.aqua.hoophelper.tutor

import android.annotation.SuppressLint
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

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // binding
        val binding: TutorChildFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.tutor_child_fragment, container, false)

        when (tutorTypeFilter) {
            TutorTypeFilter.CREATE_TEAM -> {
                binding.tutorText.text = "Create and Manage Your Team"
                binding.tutorImage.setAnimation(R.raw.create_team)
                binding.tutorImage.apply {
                    scaleX = 0.8f
                    scaleY = 0.8f
                }
            }
            TutorTypeFilter.RECORD -> {
                binding.tutorText.text = "Quickly Record Game Stats"
                binding.tutorMatch.setImageDrawable(resources.getDrawable(R.drawable.recording))
                binding.tutorMatch.apply {
                    scaleX = 0.8f
                    scaleY = 0.8f
                }
            }
            TutorTypeFilter.CHART -> {
                binding.tutorText.text = "Check Hot Spot Score Chart"
                binding.tutorImage.setAnimation(R.raw.chart)
            }
            TutorTypeFilter.TACTIC -> {
                binding.tutorText.text = "Draw basketball Tactics"
                binding.tutorImage.scaleX = 0.8f
                binding.tutorImage.scaleY = 0.8f
                binding.tutorImage.setAnimation(R.raw.tactic)
            }
            TutorTypeFilter.LIVE -> {
                binding.tutorText.text = "Watch Live Stream"
                binding.tutorImage.setAnimation(R.raw.live)
            }
            TutorTypeFilter.LEADERBOARD -> {
                binding.tutorText.text = "Check leader of Stats"
                binding.tutorImage.setAnimation(R.raw.ranking)
                binding.tutorImage.apply {
                    speed = 2.0f
                    scaleX = 0.8f
                    scaleY = 0.8f
                }
                binding.lottieSlide.visibility = View.INVISIBLE
                binding.startButton.visibility = View.VISIBLE
            }
        }

        binding.startButton.setOnClickListener {
            Tutor.finished.value = true
        }

        return binding.root
    }
}