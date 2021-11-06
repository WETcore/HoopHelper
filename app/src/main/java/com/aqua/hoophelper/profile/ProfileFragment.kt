package com.aqua.hoophelper.profile

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aqua.hoophelper.MainActivityViewModel
import com.aqua.hoophelper.R
import com.aqua.hoophelper.User
import com.aqua.hoophelper.databinding.ProfileFragmentBinding


class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by lazy {
        ViewModelProvider(this).get(ProfileViewModel::class.java)
    }

    private val mViewModel: MainActivityViewModel by lazy {
        ViewModelProvider(this).get(MainActivityViewModel::class.java)
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.setRoster()

        // binding
        val binding: ProfileFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.profile_fragment, container,false)

        binding.mailLayout.suffixText = "example@gmail.com"

        binding.createButton.setOnClickListener {
            viewModel.sendTeamInfo(binding.teamNameEdit.text.toString())
            viewModel.sendCaptainInfo(binding.playerNumEdit.text.toString())
        }

        viewModel.roster.observe(viewLifecycleOwner) {
            var num = mutableListOf<String>()
            it.forEach { player ->
                num.add(player.number)
            }
            binding.releaseText.setAdapter(
                ArrayAdapter(requireContext(), R.layout.team_start5_item, num)
            )
        }

        // release player
        binding.releaseText.setOnItemClickListener { parent, view, position, id ->
            viewModel.releasePos = position
        }
        binding.releaseButton.setOnClickListener {
//            viewModel.removePlayer(viewModel.releasePos)
        }

        // invite
        binding.inviteButton.setOnClickListener {
            viewModel.invitation.id = viewModel.db.collection("Invitations").document().id
            viewModel.invitation.teamId = User.teamId
            viewModel.invitation.inviteeMail = "huangaqua457@gmail.com"

            viewModel.sendInvitation()

        }

        return binding.root
    }
}
