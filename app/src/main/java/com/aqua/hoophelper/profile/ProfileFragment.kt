package com.aqua.hoophelper.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.setRoster()

        // binding
        val binding: ProfileFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.profile_fragment, container,false)

        when {
            User.isCaptain -> {
                Log.d("vivi","Hi1")
                binding.apply {
                    createTeamLayout.visibility = View.GONE
                    fanText.visibility = View.GONE
                    viewModel.getUserInfo()
                }
            }
            User.teamId.length > 5 -> {
                Log.d("vivi","Hi2")
                binding.apply {
                    createTeamLayout.visibility = View.GONE
                    manageRosterLayout.visibility = View.GONE
                    fanText.visibility = View.GONE
                    viewModel.getUserInfo()
                }
            }
            else -> {
                Log.d("vivi","Hi3")
                binding.apply {
                    manageRosterLayout.visibility = View.GONE
                    teamNameText.visibility = View.GONE
                    nicknameText.visibility = View.GONE
                    numberText.visibility = View.GONE
                }
            }
        }

        viewModel.teamInfo.observe(viewLifecycleOwner) {
            binding.teamNameText.text = "Team: " + it.name
        }

        viewModel.userInfo.observe(viewLifecycleOwner) {
            binding.nicknameText.text = "Name: " + it.name
            binding.numberText.text = "Number: " + it.number
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

        // create team
        binding.createButton.setOnClickListener {
            viewModel.sendTeamInfo(binding.teamNameEdit.text.toString())
            viewModel.sendCaptainInfo(binding.playerNumEdit.text.toString())
        }

        // release player
        binding.releaseText.setOnItemClickListener { parent, view, position, id ->
            viewModel.releasePos = position
        }
        binding.releaseButton.setOnClickListener {
            viewModel.removePlayer(viewModel.releasePos)
        }

        // invite
        binding.mailLayout.suffixText = "@gmail.com"
        binding.inviteButton.setOnClickListener {
            viewModel.invitation.id = viewModel.db.collection("Invitations").document().id
            viewModel.invitation.teamId = User.teamId
            viewModel.invitation.inviteeMail = binding.mailEdit.text.toString() + binding.mailLayout.suffixText//"huangaqua457@gmail.com"
            viewModel.invitation.playerName = binding.inviteNameEdit.text.toString()//"Andrew"

            viewModel.sendInvitation()

        }

        return binding.root
    }
}
