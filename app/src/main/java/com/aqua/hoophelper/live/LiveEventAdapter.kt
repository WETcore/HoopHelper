package com.aqua.hoophelper.live

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aqua.hoophelper.R
import com.aqua.hoophelper.database.Event
import com.aqua.hoophelper.databinding.LiveEventItemBinding
import com.bumptech.glide.Glide


class LiveEventAdapter(val viewModel: LiveViewModel, private val events: List<Event>):
    ListAdapter<Event, LiveEventAdapter.EventViewHolder>(DiffCallback) {

    class EventViewHolder(var binding: LiveEventItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event) {
            binding.event = event
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

        val context = holder.binding.root.context

        // get player data
        val playerStat = viewModel.getTeamPlayerData(event.playerId, events)

        holder.binding.apply {
            liveName.text = event.playerName
            liveNameB.text = event.playerName
            liveEventTypeText.text = viewModel.filterEventType(event)
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
                liveName.setTextColor(context.resources.getColor(R.color.basil_orange, null))
                liveEventTypeText.setTextColor(context.resources.getColor(R.color.basil_orange, null))
                view2.setBackgroundColor(context.resources.getColor(R.color.basil_orange, null))
                view3.setBackgroundColor(context.resources.getColor(R.color.basil_orange, null))
                liveTimeChip.setTextColor(context.resources.getColor(R.color.basil_orange, null))
                liveTimeChip.setChipStrokeColorResource(R.color.basil_orange)
                liveZoneChip.setTextColor(context.resources.getColor(R.color.basil_orange, null))
                liveZoneChip.setChipStrokeColorResource(R.color.basil_orange)
                liveMessageCard.strokeColor = context.resources.getColor(R.color.basil_orange, null)

                liveNameB.setTextColor(context.resources.getColor(R.color.basil_orange, null))
                view2B.setBackgroundColor(context.resources.getColor(R.color.basil_orange, null))
                livePtsChip.setTextColor(context.resources.getColor(R.color.basil_orange, null))
                livePtsChip.setChipStrokeColorResource(R.color.basil_orange)
                liveRebChip.setTextColor(context.resources.getColor(R.color.basil_orange, null))
                liveRebChip.setChipStrokeColorResource(R.color.basil_orange)
                liveAstChip.setTextColor(context.resources.getColor(R.color.basil_orange, null))
                liveAstChip.setChipStrokeColorResource(R.color.basil_orange)
                liveStlChip.setTextColor(context.resources.getColor(R.color.basil_orange, null))
                liveStlChip.setChipStrokeColorResource(R.color.basil_orange)
                liveBlkChip.setTextColor(context.resources.getColor(R.color.basil_orange, null))
                liveBlkChip.setChipStrokeColorResource(R.color.basil_orange)
                liveMessageCardB.strokeColor = context.resources.getColor(R.color.basil_orange, null)
            }
        } else {
            holder.binding.apply {
                liveName.setTextColor(context.resources.getColor(R.color.basil_green_dark, null))
                liveEventTypeText.setTextColor(context.resources.getColor(R.color.basil_green_dark, null))
                view2.setBackgroundColor(context.resources.getColor(R.color.basil_green_dark, null))
                view3.setBackgroundColor(context.resources.getColor(R.color.basil_green_dark, null))
                liveTimeChip.setTextColor(context.resources.getColor(R.color.basil_green_dark, null))
                liveTimeChip.setChipStrokeColorResource(R.color.basil_green_dark)
                liveZoneChip.setTextColor(context.resources.getColor(R.color.basil_green_dark, null))
                liveZoneChip.setChipStrokeColorResource(R.color.basil_green_dark)
                liveMessageCard.strokeColor = context.resources.getColor(R.color.basil_green_dark, null)

                liveNameB.setTextColor(context.resources.getColor(R.color.basil_green_dark, null))
                view2B.setBackgroundColor(context.resources.getColor(R.color.basil_green_dark, null))
                livePtsChip.setTextColor(context.resources.getColor(R.color.basil_green_dark, null))
                livePtsChip.setChipStrokeColorResource(R.color.basil_green_dark)
                liveRebChip.setTextColor(context.resources.getColor(R.color.basil_green_dark, null))
                liveRebChip.setChipStrokeColorResource(R.color.basil_green_dark)
                liveAstChip.setTextColor(context.resources.getColor(R.color.basil_green_dark, null))
                liveAstChip.setChipStrokeColorResource(R.color.basil_green_dark)
                liveStlChip.setTextColor(context.resources.getColor(R.color.basil_green_dark, null))
                liveStlChip.setChipStrokeColorResource(R.color.basil_green_dark)
                liveBlkChip.setTextColor(context.resources.getColor(R.color.basil_green_dark, null))
                liveBlkChip.setChipStrokeColorResource(R.color.basil_green_dark)
                liveMessageCardB.strokeColor = context.resources.getColor(R.color.basil_green_dark, null)
            }
        }

        holder.binding.liveTimeChip.text = "Time |  " +
                event.matchTimeMin +
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

        // image
        Glide
            .with(holder.binding.root)
            .load(event.playerImage.toUri())
            .into(holder.binding.imageView)
        Glide
            .with(holder.binding.root)
            .load(event.playerImage.toUri())
            .into(holder.binding.imageViewB)

        holder.bind(event)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }
}

sealed class EventType(var data: Event) {
}