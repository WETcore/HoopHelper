package com.aqua.hoophelper.login

import android.animation.Animator
import android.animation.ObjectAnimator
import android.graphics.Path
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.animation.doOnEnd
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aqua.hoophelper.LoginActivityViewModel
import com.aqua.hoophelper.R
import com.aqua.hoophelper.databinding.LoginFragmentBinding
import com.aqua.hoophelper.tutor.Tutor
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class LoginFragment : Fragment() {

    private val loginViewModel: LoginActivityViewModel by lazy {
        ViewModelProvider(this).get(LoginActivityViewModel::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // binding
        val binding: LoginFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.login_fragment, container,false)

        binding.lottiePlay.addAnimatorListener(object : Animator.AnimatorListener{
            var animateSwitch = false
            override fun onAnimationStart(animation: Animator?) {
                // TODO("Not yet implemented")
            }

            override fun onAnimationEnd(animation: Animator?) {
                animateSwitch = !animateSwitch
                if (animateSwitch) {
                    binding.lottiePlay.setAnimation(R.raw.post_up)
                } else {
                    binding.lottiePlay.setAnimation(R.raw.isolation)
                }
                binding.lottiePlay.playAnimation()
                // TODO("Not yet implemented")
            }

            override fun onAnimationCancel(animation: Animator?) {
                // TODO("Not yet implemented")
            }

            override fun onAnimationRepeat(animation: Animator?) {
                // TODO("Not yet implemented")
            }
        })

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // getting the value of gso inside the GoogleSignInClient
        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // animation & login
        val displayMetrics = DisplayMetrics()
        requireActivity().display?.getRealMetrics(displayMetrics)
        val width = displayMetrics.widthPixels.toFloat()
        val height = displayMetrics.heightPixels.toFloat()
        val path = Path()
        path.moveTo(32f, height-104f)

        binding.googleSignIn.setOnClickListener {
            Tutor.finished.value = false
            loginViewModel.signIn(googleSignInClient,requireActivity())
        }

        binding.loginFab.setOnClickListener {
            Tutor.finished.value = false
            path.apply {
                quadTo(width*0.25f, 0f,width*0.5f,height* 0.145f)
            }
            val animator = ObjectAnimator.ofFloat(binding.loginFab, View.X, View.Y, path).apply {
                duration = 500
                start()
            }
            animator.doOnEnd {
                binding.loginFab.isClickable = false
                loginViewModel.signIn(googleSignInClient,requireActivity())
            }
        }

        return binding.root
    }
}