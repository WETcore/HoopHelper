package com.aqua.hoophelper.team.child.manage

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aqua.hoophelper.R

class ManageFragment : Fragment() {

    companion object {
        fun newInstance() = ManageFragment()
    }

    private lateinit var viewModel: ManageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.manage_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ManageViewModel::class.java)
        // TODO: Use the ViewModel
    }

}