package com.aqua.hoophelper.live

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aqua.hoophelper.R
import com.aqua.hoophelper.database.Event
import com.aqua.hoophelper.databinding.LiveEventItemBinding


class LiveEventAdapter(val viewModel: LiveViewModel, val events: List<Event>): ListAdapter<Event, LiveEventAdapter.EventViewHolder>(DiffCallback) {

    class EventViewHolder(var binding: LiveEventItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event) {
            binding.event = event
            // TODO sealed class
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.playerNum == newItem.playerNum
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventViewHolder {
        return EventViewHolder(LiveEventItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)

        // get player data
        val playerStat = viewModel.getTeamPlayerData(event.playerId, events)

        Log.d("zzz","${events}")
        holder.binding.liveName.text = event.playerName
        holder.binding.liveNameB.text = event.playerName
        holder.binding.liveEventTypeText.text = viewModel.filterEventType(event)

        holder.binding.apply {
            livePtsChip.text
        }

        val condition = viewModel.filterEventType(event)
        if(condition == "got turnover" ||
            condition == "miss 2 points" ||
            condition == "miss 3 points" ||
            condition == "miss a free throw" ||
            condition == "got foul"
                ) {
            holder.binding.apply {
                liveName.setTextColor(Color.parseColor("#FD5523"))
                liveEventTypeText.setTextColor(Color.parseColor("#FD5523"))
                view2.setBackgroundColor(Color.parseColor("#FD5523"))
                view3.setBackgroundColor(Color.parseColor("#FD5523"))
                liveTimeChip.setTextColor(Color.parseColor("#FD5523"))
                liveTimeChip.setChipStrokeColorResource(R.color.basil_orange)
                liveZoneChip.setTextColor(Color.parseColor("#FD5523"))
                liveZoneChip.setChipStrokeColorResource(R.color.basil_orange)
                liveMessageCard.strokeColor = Color.parseColor("#FD5523")

                liveNameB.setTextColor(Color.parseColor("#FD5523"))
                view2B.setBackgroundColor(Color.parseColor("#FD5523"))
                livePtsChip.setTextColor(Color.parseColor("#FD5523"))
                livePtsChip.setChipStrokeColorResource(R.color.basil_orange)
                liveRebChip.setTextColor(Color.parseColor("#FD5523"))
                liveRebChip.setChipStrokeColorResource(R.color.basil_orange)
                liveAstChip.setTextColor(Color.parseColor("#FD5523"))
                liveAstChip.setChipStrokeColorResource(R.color.basil_orange)
                liveStlChip.setTextColor(Color.parseColor("#FD5523"))
                liveStlChip.setChipStrokeColorResource(R.color.basil_orange)
                liveBlkChip.setTextColor(Color.parseColor("#FD5523"))
                liveBlkChip.setChipStrokeColorResource(R.color.basil_orange)
                liveMessageCardB.strokeColor = Color.parseColor("#FD5523")
            }

        } else {
            holder.binding.apply {
                liveName.setTextColor(Color.parseColor("#356859"))
                liveEventTypeText.setTextColor(Color.parseColor("#356859"))
                view2.setBackgroundColor(Color.parseColor("#356859"))
                view3.setBackgroundColor(Color.parseColor("#356859"))
                liveTimeChip.setTextColor(Color.parseColor("#356859"))
                liveTimeChip.setChipStrokeColorResource(R.color.basil_green_dark)
                liveZoneChip.setTextColor(Color.parseColor("#356859"))
                liveZoneChip.setChipStrokeColorResource(R.color.basil_green_dark)
                liveMessageCard.strokeColor = Color.parseColor("#356859")

                liveNameB.setTextColor(Color.parseColor("#356859"))
                view2B.setBackgroundColor(Color.parseColor("#356859"))
                livePtsChip.setTextColor(Color.parseColor("#356859"))
                livePtsChip.setChipStrokeColorResource(R.color.basil_green_dark)
                liveRebChip.setTextColor(Color.parseColor("#356859"))
                liveRebChip.setChipStrokeColorResource(R.color.basil_green_dark)
                liveAstChip.setTextColor(Color.parseColor("#356859"))
                liveAstChip.setChipStrokeColorResource(R.color.basil_green_dark)
                liveStlChip.setTextColor(Color.parseColor("#356859"))
                liveStlChip.setChipStrokeColorResource(R.color.basil_green_dark)
                liveBlkChip.setTextColor(Color.parseColor("#356859"))
                liveBlkChip.setChipStrokeColorResource(R.color.basil_green_dark)
                liveMessageCardB.strokeColor = Color.parseColor("#356859")
            }
        }

        holder.binding.liveTimeChip.text = "Time |  " + event.matchTimeMin +
                ":" + event.matchTimeSec +
                " Qtr: " + event.quarter

        holder.binding.liveZoneChip.text = "Zone |  " +
                when(event.zone) {
                    1 -> "Around Rim"
                    2 -> "Left Elbow"
                    3 -> "Mid Straight"
                    4 -> "Right Elbow"
                    5 -> "Left Baseline"
                    6 -> "Left Wing"
                    7 -> "Long Straight"
                    8 -> "Right Wing"
                    9 -> "Right Baseline"
                    10 -> "Left Corner"
                    11 -> "Left 3Points"
                    12 -> "Top of Arc"
                    13 -> "Right 3Points"
                    14 -> "Right Corner"
                    else -> "FreeThrow Line"
                }

        holder.binding.apply {
            livePtsChip.text = "PTS | " + playerStat.pts.toString()
            liveRebChip.text = "REB | " + playerStat.reb.toString()
            liveAstChip.text = "AST | " + playerStat.ast.toString()
            liveStlChip.text = "STL | " + playerStat.stl.toString()
            liveBlkChip.text = "BLK | " + playerStat.blk.toString()
        }

        holder.binding.liveTimeChip.isClickable = false
        holder.binding.liveZoneChip.isClickable = false

        // flip card
        var isFront = true
        val context = holder.binding.root.context
        val scale = context.resources.displayMetrics.density
        holder.binding.liveMessageCard.cameraDistance = 8000 * scale
        holder.binding.liveMessageCardB.cameraDistance = 8000 * scale
        holder.binding.liveMessageCard.setOnClickListener {
            val animF = AnimatorInflater.loadAnimator(context, R.animator.front_animator) as AnimatorSet
            val animB = AnimatorInflater.loadAnimator(context, R.animator.back_animator) as AnimatorSet
            isFront = if (isFront) {
                animF.setTarget(holder.binding.liveMessageCard)
                animB.setTarget(holder.binding.liveMessageCardB)
                animF.start()
                animB.start()
                false
            } else {
                animF.setTarget(holder.binding.liveMessageCardB)
                animB.setTarget(holder.binding.liveMessageCard)
                animB.start()
                animF.start()
                true
            }
        }

        holder.bind(event)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }
}

sealed class EventType(var data: Event) {
}