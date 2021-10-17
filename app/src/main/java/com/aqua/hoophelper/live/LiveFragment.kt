package com.aqua.hoophelper.live

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.aqua.hoophelper.R
import com.aqua.hoophelper.databinding.LiveFragmentBinding
import com.aqua.hoophelper.databinding.MatchFragmentBinding
import com.aqua.hoophelper.match.MatchViewModel

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
        return binding.root
    }
}