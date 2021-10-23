package com.aqua.hoophelper.live

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.aqua.hoophelper.R
import com.aqua.hoophelper.databinding.LiveFragmentBinding

class LiveFragment : Fragment() {

    private val viewModel: LiveViewModel by lazy {
        ViewModelProvider(this).get(LiveViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // binding
        val binding: LiveFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.live_fragment, container,false)

        // adapt recycler
        val adapter = LiveEventAdapter(viewModel)
        binding.liveRecycler.adapter = adapter

        viewModel.events.observe(viewLifecycleOwner) {
            Log.d("db","${it}")
            adapter.submitList(it)
        }

        return binding.root
    }
}