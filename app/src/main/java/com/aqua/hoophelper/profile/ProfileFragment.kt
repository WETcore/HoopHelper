package com.aqua.hoophelper.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

        binding.apply {
            fun setAuth() {
                when {
                    User.isCaptain -> {
                        viewModel.getPlayerUserInfo()
                        createTeamLayout.visibility = View.GONE
                        fanText.visibility = View.GONE
                        teamNameText.visibility = View.VISIBLE
                        nicknameText.visibility = View.VISIBLE
                        numberText.visibility = View.VISIBLE
                        viewModel.status.observe(viewLifecycleOwner) {
                            when (it) {
                                LoadApiStatus.LOADING -> {
                                    lottieProfile.visibility = View.VISIBLE
                                    manageRosterLayout.visibility = View.INVISIBLE
                                    userInfoLayout.visibility = View.INVISIBLE
                                }
                                LoadApiStatus.DONE -> {
                                    lottieProfile.visibility = View.INVISIBLE
                                    manageRosterLayout.visibility = View.VISIBLE
                                    userInfoLayout.visibility = View.VISIBLE
                                }
                                LoadApiStatus.ERROR -> {

                                }
                            }
                        }
                    }

                    User.teamId.length > 5 -> {
                        viewModel.getPlayerUserInfo()
                        manageRosterLayout.visibility = View.GONE
                        createTeamLayout.visibility = View.GONE
                        fanText.visibility = View.GONE
                        teamNameText.visibility = View.VISIBLE
                        nicknameText.visibility = View.VISIBLE
                        numberText.visibility = View.VISIBLE
                        viewModel.status.observe(viewLifecycleOwner) {
                            when (it) {
                                LoadApiStatus.LOADING -> {
                                    lottieProfile.visibility = View.VISIBLE
                                    userInfoLayout.visibility = View.INVISIBLE
                                }
                                LoadApiStatus.DONE -> {
                                    lottieProfile.visibility = View.INVISIBLE
                                    userInfoLayout.visibility = View.VISIBLE
                                }
                                LoadApiStatus.ERROR -> {

                                }
                            }
                        }
                    }
                    else -> {
                        lottieProfile.visibility = View.GONE
                        manageRosterLayout.visibility = View.GONE
                        teamNameText.visibility = View.GONE
                        nicknameText.visibility = View.GONE
                        numberText.visibility = View.GONE
                    }
                }
            }

            viewModel.teamInfo.observe(viewLifecycleOwner) {
                teamNameText.text = "Team: " + it.name
            }

            viewModel.userInfo.observe(viewLifecycleOwner) {
                nicknameText.text = "Name: " + it.name
                numberText.text = "Number: " + it.number
            }

            viewModel.roster.observe(viewLifecycleOwner) {
                val nums = mutableListOf<String>()
                it.forEach { player ->
                    nums.add(player.number)
                }
                releaseText.setAdapter(
                    ArrayAdapter(requireContext(), R.layout.team_start5_item, nums)
                )
            }

            viewModel.authToggle.observe(viewLifecycleOwner) {
                setAuth()
            }

            // create team
            createButton.setOnClickListener {
                if (teamNameEdit.text?.length == 0) {
                    teamNameLayout.error = "This is required"
                } else {
                    teamNameLayout.error = null
                }
                if (nicknameEdit.text?.length == 0) {
                    nicknameLayout.error = "This is required"
                } else {
                    nicknameLayout.error = null
                }
                if (playerNumEdit.text?.length == 0) {
                    playerNumLayout.error = "This is required"
                } else {
                    playerNumLayout.error = null
                }
                if (teamNameEdit.text?.length != 0 &&
                    nicknameEdit.text?.length != 0 &&
                    playerNumEdit.text?.length != 0
                ) {
                    viewModel.sendTeamInfo(
                        teamNameEdit.text.toString(),
                        playerNumEdit.text.toString(),
                        nicknameEdit.text.toString()
                    )
                    viewModel.getUserInfo()
                }
            }

            // release player
            releaseText.setOnItemClickListener { parent, view, position, id ->
                viewModel.releasePos = position
            }
            releaseButton.setOnClickListener {
                if (releaseText.text?.length == 0) {
                    releaseInput.error = "This is required"
                } else if (releaseText.text?.length != 0) {
                    releaseInput.error = null
                    changeCaptain()
                }
            }

            // invite
            inviteButton.setOnClickListener {
                if (mailEdit.text?.length == 0) {
                    mailLayout.error = "This is required"
                } else {
                    mailLayout.error = null
                }
                if (inviteNameEdit.text?.length == 0) {
                    inviteNameLayout.error = "This is required"
                } else {
                    inviteNameLayout.error = null
                }
                if (mailEdit.text?.length != 0 &&
                    inviteNameEdit.text?.length != 0
                ) {
                    viewModel.sendInvitation(
                        mailEdit.text.toString(),
                        inviteNameEdit.text.toString()
                    )
                }
            }
        }

        return binding.root
    }

    private fun changeCaptain() {
        if (viewModel.removePlayer()) {
            val playerIds = mutableListOf<String>()
            val playerNums = mutableListOf<String>()
            viewModel.roster.value?.forEach { player ->
                if (player.id != viewModel.userInfo.value?.id) {
                    playerIds.add(player.id)
                    playerNums.add(player.number)
                }
            }
            setAlertDialog(playerIds, playerNums)
        }
    }

    private fun setAlertDialog(
        playerIds: MutableList<String>,
        playerNums: MutableList<String>
    ) {
        if (playerIds.isNotEmpty()) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Choose New Captain")
                .setSingleChoiceItems(playerNums.toTypedArray(), 0) { dialog, which ->
                }
                .setNeutralButton("Cancel") { dialog, which ->
                    dialog.cancel()
                }
                .setPositiveButton("Confirm") { dialog, which ->
                    val position = (dialog as AlertDialog).listView.checkedItemPosition
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
