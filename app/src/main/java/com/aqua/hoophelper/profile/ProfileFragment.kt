package com.aqua.hoophelper.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aqua.hoophelper.R
import com.aqua.hoophelper.databinding.ProfileFragmentBinding
import com.aqua.hoophelper.util.LoadApiStatus
import com.aqua.hoophelper.util.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by lazy {
        ViewModelProvider(this).get(ProfileViewModel::class.java)
    }

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // binding
        val binding: ProfileFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.profile_fragment, container, false)

        fun setAuth() {
            when {
                User.isCaptain -> {
                    viewModel.getPlayerUserInfo()
                    binding.createTeamLayout.visibility = View.GONE
                    binding.fanText.visibility = View.GONE
                    binding.teamNameText.visibility = View.VISIBLE
                    binding.nicknameText.visibility = View.VISIBLE
                    binding.numberText.visibility = View.VISIBLE
                    viewModel.status.observe(viewLifecycleOwner) {
                        when (it) {
                            LoadApiStatus.LOADING -> {
                                binding.lottieProfile.visibility = View.VISIBLE
                                binding.manageRosterLayout.visibility = View.INVISIBLE
                                binding.userInfoLayout.visibility = View.INVISIBLE
                            }
                            LoadApiStatus.DONE -> {
                                binding.lottieProfile.visibility = View.INVISIBLE
                                binding.manageRosterLayout.visibility = View.VISIBLE
                                binding.userInfoLayout.visibility = View.VISIBLE
                            }
                            LoadApiStatus.ERROR -> {

                            }
                        }
                    }
                }
                User.teamId.length > 5 -> {
                    viewModel.getPlayerUserInfo()
                    binding.manageRosterLayout.visibility = View.GONE
                    binding.createTeamLayout.visibility = View.GONE
                    binding.fanText.visibility = View.GONE
                    binding.teamNameText.visibility = View.VISIBLE
                    binding.nicknameText.visibility = View.VISIBLE
                    binding.numberText.visibility = View.VISIBLE
                    viewModel.status.observe(viewLifecycleOwner) {
                        when (it) {
                            LoadApiStatus.LOADING -> {
                                binding.lottieProfile.visibility = View.VISIBLE
                                binding.userInfoLayout.visibility = View.INVISIBLE
                            }
                            LoadApiStatus.DONE -> {
                                binding.lottieProfile.visibility = View.INVISIBLE
                                binding.userInfoLayout.visibility = View.VISIBLE
                            }
                            LoadApiStatus.ERROR -> {

                            }
                        }
                    }
                }
                else -> {
                    binding.apply {
                        binding.lottieProfile.visibility = View.GONE
                        manageRosterLayout.visibility = View.GONE
                        teamNameText.visibility = View.GONE
                        nicknameText.visibility = View.GONE
                        numberText.visibility = View.GONE
                    }
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
            val nums = mutableListOf<String>()
            it.forEach { player ->
                nums.add(player.number)
            }
            binding.releaseText.setAdapter(
                ArrayAdapter(requireContext(), R.layout.team_start5_item, nums)
            )
        }

        viewModel.authToggle.observe(viewLifecycleOwner) {
            setAuth()
        }

        // create team
        binding.createButton.setOnClickListener {
            if (binding.teamNameEdit.text?.length == 0) {
                binding.teamNameLayout.error = "This is required"
            } else {
                binding.teamNameLayout.error = null
            }
            if (binding.nicknameEdit.text?.length == 0) {
                binding.nicknameLayout.error = "This is required"
            } else {
                binding.nicknameLayout.error = null
            }
            if (binding.playerNumEdit.text?.length == 0) {
                binding.playerNumLayout.error = "This is required"
            } else {
                binding.playerNumLayout.error = null
            }
            if (binding.teamNameEdit.text?.length != 0 &&
                binding.nicknameEdit.text?.length != 0 &&
                binding.playerNumEdit.text?.length != 0
            ) {
                viewModel.sendTeamInfo(
                    binding.teamNameEdit.text.toString(),
                    binding.playerNumEdit.text.toString(),
                    binding.nicknameEdit.text.toString()
                )
                viewModel.getUserInfo()
            }
        }

        // release player
        binding.releaseText.setOnItemClickListener { parent, view, position, id ->
            viewModel.releasePos = position
        }
        binding.releaseButton.setOnClickListener {
            if (binding.releaseText.text?.length == 0) {
                binding.releaseInput.error = "This is required"
            } else if (binding.releaseText.text?.length != 0) {
                binding.releaseInput.error = null
                // change captain
                if (viewModel.removePlayer()) {
                    val playerIds = mutableListOf<String>()
                    val playerNums = mutableListOf<String>()
                    viewModel.roster.value?.forEach { player ->
                        if (player.id != viewModel.userInfo.value?.id) {
                            playerIds.add(player.id)
                            playerNums.add(player.number)
                        }
                    }
                    if (playerIds.isNotEmpty()) {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Choose New Captain")
                            .setSingleChoiceItems(playerNums.toTypedArray(), 0) { dialog, which ->
                            }
                            .setNeutralButton("Cancel") { dialog, which ->
                                dialog.cancel()
                            }
                            .setPositiveButton("Confirm") { dialog, which ->
                                val position = (dialog as androidx.appcompat.app.AlertDialog)
                                    .listView.checkedItemPosition
                                viewModel.setNewCaptain(playerIds, position)
                                viewModel.getPlayerUserInfo()
                                dialog.dismiss()
                            }
                            .show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Need at least one player",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        // invite
        binding.inviteButton.setOnClickListener {
            if (binding.mailEdit.text?.length == 0) {
                binding.mailLayout.error = "This is required"
            } else {
                binding.mailLayout.error = null
            }
            if (binding.inviteNameEdit.text?.length == 0) {
                binding.inviteNameLayout.error = "This is required"
            } else {
                binding.inviteNameLayout.error = null
            }
            if (binding.mailEdit.text?.length != 0 &&
                binding.inviteNameEdit.text?.length != 0
            ) {
                viewModel.sendInvitation(
                    binding.mailEdit.text.toString(),
                    binding.inviteNameEdit.text.toString()
                )
            }
        }

        return binding.root
    }
}
