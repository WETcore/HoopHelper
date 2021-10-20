package com.aqua.hoophelper.profile

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.aqua.hoophelper.R
import com.aqua.hoophelper.databinding.MatchFragmentBinding
import com.aqua.hoophelper.databinding.ProfileFragmentBinding
import com.aqua.hoophelper.match.MatchViewModel


class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by lazy {
        ViewModelProvider(this).get(ProfileViewModel::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // binding
        val binding: ProfileFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.profile_fragment, container,false)


        binding.chipTest.setOnLongClickListener {

            // 震動
            it.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
            // 拖曳
            val shadowBuilder = View.DragShadowBuilder(it)
            it.startDragAndDrop(null,shadowBuilder, it,0)
            it.visibility = View.INVISIBLE

            return@setOnLongClickListener true
        }

        binding.root.setOnDragListener{ v, event ->
            val action = event.action
            when(action) {
                DragEvent.ACTION_DRAG_LOCATION -> {
//                    Log.d("drag","${x},${y}")
                }
                DragEvent.ACTION_DROP -> {
                    binding.chipTest.x = event.x
                    binding.chipTest.y = event.y
                    binding.chipTest.visibility = View.VISIBLE
                }
            }
            return@setOnDragListener true
        }

        return binding.root
    }
}
