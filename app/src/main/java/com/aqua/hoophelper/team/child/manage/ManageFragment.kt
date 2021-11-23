package com.aqua.hoophelper.team.child.manage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aqua.hoophelper.R
import com.aqua.hoophelper.databinding.ManageFragmentBinding
import com.aqua.hoophelper.util.LoadApiStatus

class ManageFragment : Fragment() {

    private val viewModel: ManageViewModel by lazy {
        ViewModelProvider(this).get(ManageViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // binding
        val binding: ManageFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.manage_fragment, container,false)

        viewModel.status.observe(viewLifecycleOwner) {
            when(it) {
                LoadApiStatus.LOADING -> {
                    binding.lottieManage.visibility = View.VISIBLE
                    binding.manageLayout.visibility = View.GONE
                }
                LoadApiStatus.DONE -> {
                    binding.lottieManage.visibility = View.GONE
                    binding.manageLayout.visibility = View.VISIBLE
                }
                LoadApiStatus.ERROR -> {

                }
            }
        }

        binding.apply {
            // lineUp
            viewModel.startPlayer.observe(viewLifecycleOwner) {
                if (!it.isNullOrEmpty()) {
                    start5PgText.setText(it[0].number, false)
                    start5SgText.setText(it[1].number, false)
                    start5SfText.setText(it[2].number, false)
                    start5PfText.setText(it[3].number, false)
                    start5CText.setText(it[4].number, false)
                }
            }

            // sub
            viewModel.substitutionPlayer.observe(viewLifecycleOwner) {
                viewModel.subNum = mutableListOf<String>()
                it.forEach { player ->
                    viewModel.subNum.add(player.number)
                }

                // spinner
                val teamAdapter = ArrayAdapter(requireContext(), R.layout.team_start5_item, viewModel.subNum)
                start5PgText.setAdapter(teamAdapter)
                start5SgText.setAdapter(teamAdapter)
                start5SfText.setAdapter(teamAdapter)
                start5PfText.setAdapter(teamAdapter)
                start5CText.setAdapter(teamAdapter)
            }

            start5PgText.setOnItemClickListener { parent, view, position, id ->
                viewModel.switchLineUp(position, 0)
            }
            start5SgText.setOnItemClickListener { parent, view, position, id ->
                viewModel.switchLineUp(position, 1)
            }
            start5SfText.setOnItemClickListener { parent, view, position, id ->
                viewModel.switchLineUp(position, 2)
            }
            start5PfText.setOnItemClickListener { parent, view, position, id ->
                viewModel.switchLineUp(position, 3)
            }
            start5CText.setOnItemClickListener { parent, view, position, id ->
                viewModel.switchLineUp(position, 4)
            }

        // set rule
            setRuleButton.setOnClickListener {
                viewModel.rule.quarter = quarterEdit.text.toString()
                viewModel.rule.gClock = gameClockEdit.text.toString()
                viewModel.rule.sClock = shotClockEdit.text.toString()
                viewModel.rule.foulOut = foulEdit.text.toString()
                viewModel.rule.to1 = timeoutEdit1.text.toString()
                viewModel.rule.to2 = timeoutEdit2.text.toString()
                viewModel.setRule()
            }
        }

        return binding.root
    }
}