package com.aqua.hoophelper.profile

import android.annotation.SuppressLint
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.aqua.hoophelper.R
import com.aqua.hoophelper.databinding.ProfileFragmentBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth


class ProfileFragment : BottomSheetDialogFragment() {

    private lateinit var auth: FirebaseAuth

    private val viewModel: ProfileViewModel by lazy {
        ViewModelProvider(this).get(ProfileViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // binding
        val binding: ProfileFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.profile_fragment, container,false)

        binding.mailLayout.suffixText = "example@gmail.com"

//        // TODO login
//        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // getting the value of gso inside the GoogleSignInClient
        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        binding.googleSignIn.setOnClickListener {
            viewModel.signIn(googleSignInClient,requireActivity())

        }

        binding.createButton.setOnClickListener {
            viewModel.sendTeamInfo(binding.teamNameEdit.text.toString())
            viewModel.sendCaptainInfo(binding.playerNumEdit.text.toString())
        }



        return binding.root
    }
}
