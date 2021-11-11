package com.aqua.hoophelper.team.child.tactic

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.aqua.hoophelper.MainActivityViewModel
import com.aqua.hoophelper.R
import com.aqua.hoophelper.databinding.TacticFragmentBinding
import com.aqua.hoophelper.team.TeamViewModel

class TacticFragment : Fragment() {

    private val viewModel: TacticViewModel by lazy {
        ViewModelProvider(this).get(TacticViewModel::class.java)
    }

    private val mainViewModel: MainActivityViewModel by lazy {
        ViewModelProvider(this).get(MainActivityViewModel::class.java)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // binding
        val binding: TacticFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.tactic_fragment, container,false)

        // simple draw example
        binding.tactic.setOnTouchListener { view, event ->
            false
        }

        binding.switch1.setOnCheckedChangeListener { buttonView, isChecked ->
            Tactic.vPagerSwipe.value = !isChecked
            binding.switch1.text = Tactic.vPagerSwipe.value.toString()
        }

        return binding.root
    }
}

object Tactic {
    val vPagerSwipe = MutableLiveData(true)
}