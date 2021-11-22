package com.aqua.hoophelper.tutor

import android.animation.Animator
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.aqua.hoophelper.MainActivity
import com.aqua.hoophelper.R
import com.aqua.hoophelper.databinding.ProfileFragmentBinding
import com.aqua.hoophelper.databinding.TutorFragmentBinding
import com.aqua.hoophelper.profile.ProfileViewModel

class TutorFragment : Fragment() {

    private val viewModel: TutorViewModel by lazy {
        ViewModelProvider(this).get(TutorViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // binding
        val binding: TutorFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.tutor_fragment, container,false)

        // VPager
        val adapter = TutorAdapter(requireActivity())
        binding.tutorPager.apply {
            this.adapter = adapter
        }

        Tutor.finished.observe(viewLifecycleOwner) {
            if (it) {
                binding.tutorPager.visibility = View.GONE
                val intent = Intent(requireContext(), MainActivity::class.java)
                val lottie = binding.loginLottie
                lottie.speed = 1.5f
                lottie.playAnimation()

                lottie.addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator?) {
                        // TODO("Not yet implemented")
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        startActivity(intent)
                        requireActivity().finish()
                        // TODO("Not yet implemented")
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                        // TODO("Not yet implemented")
                    }

                    override fun onAnimationRepeat(animation: Animator?) {
                        // TODO("Not yet implemented")
                    }
                })
            }
        }

        return binding.root
    }
}