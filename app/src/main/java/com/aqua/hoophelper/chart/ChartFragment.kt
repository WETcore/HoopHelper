package com.aqua.hoophelper.chart

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.aqua.hoophelper.R
import com.aqua.hoophelper.databinding.ChartFragmentBinding
import com.aqua.hoophelper.databinding.MatchFragmentBinding
import com.aqua.hoophelper.match.MatchViewModel

class ChartFragment : Fragment() {

    private val viewModel: ChartViewModel by lazy {
        ViewModelProvider(this).get(ChartViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // binding
        val binding: ChartFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.chart_fragment, container,false)
        return binding.root
    }
}