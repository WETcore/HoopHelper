package com.aqua.hoophelper.team.child.manage

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Spanned
import android.text.style.ImageSpan
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
import com.aqua.hoophelper.team.TeamViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable

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

        // spinner
        val teamAdapter = ArrayAdapter(requireContext(), R.layout.team_start5_item, viewModel.lineup.value!!)
        binding.apply {
            viewModel.lineup.observe(viewLifecycleOwner) {
                teamAdapter
                start5PgText.setAdapter(teamAdapter)
                start5SgText.setAdapter(teamAdapter)
                start5SfText.setAdapter(teamAdapter)
                start5PfText.setAdapter(teamAdapter)
                start5CText.setAdapter(teamAdapter)
            }

            var buffer = ""

            start5PgText.setOnItemClickListener { parent, view, position, id ->
                Log.d("lineup","${viewModel.start5.value!![0]} to ${parent.getItemAtPosition(position)}")
                buffer = viewModel._start5.value!![0]
                viewModel._start5.value!![0] = parent.getItemAtPosition(position).toString()
                viewModel._lineup.value!![position] = buffer
            }
            start5SgText.setOnItemClickListener { parent, view, position, id ->
                Log.d("lineup","${viewModel.start5.value!![1]} to ${parent.getItemAtPosition(position)}")
                buffer = viewModel._start5.value!![1]
                viewModel._start5.value!![1] = parent.getItemAtPosition(position).toString()
                viewModel._lineup.value!![position] = buffer
            }
            start5SfText.setOnItemClickListener { parent, view, position, id ->
                Log.d("lineup","${viewModel.start5.value!![2]} to ${parent.getItemAtPosition(position)}")
                buffer = viewModel._start5.value!![2]
                viewModel._start5.value!![2] = parent.getItemAtPosition(position).toString()
                viewModel._lineup.value!![position] = buffer
            }
            start5PfText.setOnItemClickListener { parent, view, position, id ->
                Log.d("lineup","${viewModel.start5.value!![3]} to ${parent.getItemAtPosition(position)}")
                buffer = viewModel._start5.value!![3]
                viewModel._start5.value!![3] = parent.getItemAtPosition(position).toString()
                viewModel._lineup.value!![position] = buffer
            }
            start5CText.setOnItemClickListener { parent, view, position, id ->
                Log.d("lineup","${viewModel.start5.value!![4]} to ${parent.getItemAtPosition(position)}")
                buffer = viewModel._start5.value!![4]
                viewModel._start5.value!![4] = parent.getItemAtPosition(position).toString()
                viewModel._lineup.value!![position] = buffer
            }

        }


        return binding.root
    }
}