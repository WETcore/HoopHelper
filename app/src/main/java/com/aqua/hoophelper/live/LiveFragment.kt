package com.aqua.hoophelper.live

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aqua.hoophelper.R

class LiveFragment : Fragment() {

    companion object {
        fun newInstance() = LiveFragment()
    }

    private lateinit var viewModel: LiveViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.live_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LiveViewModel::class.java)
        // TODO: Use the ViewModel
    }

}