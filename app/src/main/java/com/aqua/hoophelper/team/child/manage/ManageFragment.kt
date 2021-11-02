package com.aqua.hoophelper.team.child.manage

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.aqua.hoophelper.R
import com.aqua.hoophelper.databinding.ManageFragmentBinding

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






        binding.apply {

            // lineUp
            viewModel.startPlayer.observe(viewLifecycleOwner) {
                if (!it.isNullOrEmpty()) {
                    Log.d("lineUp","${it[0].number}")
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
                Log.d("subPlayer4", "${viewModel.subNum}")
            }

            start5PgText.setOnItemClickListener { parent, view, position, id ->

                viewModel.switchLineUp(position, 0)

                Log.d("lineup","${viewModel.startPlayer.value}")

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

        // set rule
        binding.apply {
            setRuleButton.setOnClickListener { //TODO 初始值從db拿，以下放回VM
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