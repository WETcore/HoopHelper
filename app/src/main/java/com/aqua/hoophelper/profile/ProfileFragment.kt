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
import com.aqua.hoophelper.util.LoadApiStatus
import com.google.android.material.dialog.MaterialAlertDialogBuilder


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
                viewModel.getPlayerUserInfo()
                binding.createTeamLayout.visibility = View.GONE
                binding.fanText.visibility = View.GONE
                viewModel.status.observe(viewLifecycleOwner) {
                    when(it) {
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
                viewModel.status.observe(viewLifecycleOwner) {
                    when(it) {
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
                    binding.playerNumEdit.text.toString()
                )
                viewModel.sendCaptainInfo(
                    binding.playerNumEdit.text.toString(),
                    binding.nicknameEdit.text.toString()
                )
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
                if (viewModel.removePlayer(viewModel.releasePos)) {
                    val numbers = mutableListOf<String>()
                    viewModel.roster.value?.forEach { player ->
                        if (player.number != viewModel.userInfo.value?.number) {
                            numbers.add(player.number)
                        }
                    }
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Choose New Captain")
                        .setSingleChoiceItems(numbers.toTypedArray(), 0) { dialog, which ->
                        }
                        .setNeutralButton("Cancel") { dialog, which ->
                            dialog.cancel()
                        }
                        .setPositiveButton("Confirm") { dialog, which ->
                            val position = (dialog as androidx.appcompat.app.AlertDialog)
                                .listView.checkedItemPosition
                            viewModel.db
                                .collection("Players")
                                .whereEqualTo("number",numbers[position])
                                .get().addOnCompleteListener {
                                    it.result.documents.first().reference.update("captain", true)
                                }
                            dialog.dismiss()
                        }
                        .show()
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
                viewModel.sendInvitation(binding.mailEdit.text.toString(), binding.inviteNameEdit.text.toString())
            }
        }
        return binding.root
    }
}
