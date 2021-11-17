package com.aqua.hoophelper.team.child.tactic

import android.annotation.SuppressLint
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.aqua.hoophelper.MainActivityViewModel
import com.aqua.hoophelper.R
import com.aqua.hoophelper.databinding.TacticFragmentBinding
import com.bumptech.glide.Glide

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

        // arrow style
        binding.arrowGroup.check(R.id.arrow_normal)
        false.let {
            Arrow.isDash = it
            Arrow.isCurl = it
            Arrow.isScreen = it
        }
        binding.arrowGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            when (checkedId) {
                R.id.arrow_normal -> {
                    false.let {
                        Arrow.isDash = it
                        Arrow.isCurl = it
                        Arrow.isScreen = it
                    }
                }
                R.id.arrow_dash -> {
                    Arrow.isDash = true
                    false.let {
                        Arrow.isCurl = it
                        Arrow.isScreen = it
                    }
                }
                R.id.arrow_curl -> {
                    Arrow.isCurl = true
                    false.let {
                        Arrow.isDash = it
                        Arrow.isScreen = it
                    }
                }
                R.id.arrow_screen -> {
                    Arrow.isScreen = true
                    false.let {
                        Arrow.isDash = it
                        Arrow.isCurl = it
                    }
                }
            }
        }

        binding.resetCanvas.setOnClickListener {
            val canvas = Canvas()
            val paint = Paint()
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            canvas.drawRect(0f,0f,500f,500f,paint)
//            binding.tactic.draw(canvas)
            binding.tactic.clear()
        }

        viewModel.startPlayer.observe(viewLifecycleOwner) {
            if (it.size > 0) {
                Glide
                    .with(binding.root)
                    .load(it[0].avatar.toUri())
                    .into(binding.player1Avatar)
                Glide
                    .with(binding.root)
                    .load(it[1].avatar.toUri())
                    .into(binding.player2Avatar)
                Glide
                    .with(binding.root)
                    .load(it[2].avatar.toUri())
                    .into(binding.player3Avatar)
                Glide
                    .with(binding.root)
                    .load(it[3].avatar.toUri())
                    .into(binding.player4Avatar)
                Glide
                    .with(binding.root)
                    .load(it[4].avatar.toUri())
                    .into(binding.player5Avatar)
            }
        }

        binding.apply {
            tacticFab.setOnLongClickListener {
                viewModel.avatarNum = 0
                val shadow = View.DragShadowBuilder(it)
                it.startDragAndDrop(null, shadow, it,0)
            }
            player1Avatar.setOnLongClickListener {
                viewModel.avatarNum = 1
                val shadow = View.DragShadowBuilder(it)
                it.startDragAndDrop(null, shadow, it,0)
            }
            player2Avatar.setOnLongClickListener {
                viewModel.avatarNum = 2
                val shadow = View.DragShadowBuilder(it)
                it.startDragAndDrop(null, shadow, it,0)
            }
            player3Avatar.setOnLongClickListener {
                viewModel.avatarNum = 3
                val shadow = View.DragShadowBuilder(it)
                it.startDragAndDrop(null, shadow, it,0)
            }
            player4Avatar.setOnLongClickListener {
                viewModel.avatarNum = 4
                val shadow = View.DragShadowBuilder(it)
                it.startDragAndDrop(null, shadow, it,0)
            }
            player5Avatar.setOnLongClickListener {
                viewModel.avatarNum = 5
                val shadow = View.DragShadowBuilder(it)
                it.startDragAndDrop(null, shadow, it,0)
            }
            root.setOnDragListener{ v, event ->
                when(event.action) {
                    DragEvent.ACTION_DROP -> {
                        when(viewModel.avatarNum) {
                            0 -> {
                                tacticFab.x = (event.x - 30)
                                tacticFab.y = (event.y - 30)
                            }
                            1 -> {
                                player1Avatar.x = (event.x - 60)
                                player1Avatar.y = (event.y - 60)
                            }
                            2 -> {
                                player2Avatar.x = (event.x - 60)
                                player2Avatar.y = (event.y - 60)
                            }
                            3 -> {
                                player3Avatar.x = (event.x - 60)
                                player3Avatar.y = (event.y - 60)
                            }
                            4 -> {
                                player4Avatar.x = (event.x - 60)
                                player4Avatar.y = (event.y - 60)
                            }
                            5 -> {
                                player5Avatar.x = (event.x - 60)
                                player5Avatar.y = (event.y - 60)
                            }
                        }
                    }
                }
                true
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}