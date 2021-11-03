package com.aqua.hoophelper.live

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


class LiveEventAdapter(val viewModel: LiveViewModel): ListAdapter<Event, LiveEventAdapter.EventViewHolder>(DiffCallback) {

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

        Log.d("zzz","${event.zone} ${event.matchTimeMin}")
        holder.binding.liveName.text = event.playerName
        holder.binding.liveEventTypeText.text = viewModel.filterEventType(event)

        val condition = viewModel.filterEventType(event)
        if(condition == "got turnover" ||
            condition == "miss 2 points" ||
            condition == "miss 3 points" ||
            condition == "miss a free throw" ||
            condition == "got foul"
                ) {
                holder.binding.liveName.setTextColor(Color.parseColor("#FD5523"))
                holder.binding.liveEventTypeText.setTextColor(Color.parseColor("#FD5523"))
                holder.binding.view2.setBackgroundColor(Color.parseColor("#FD5523"))
                holder.binding.view3.setBackgroundColor(Color.parseColor("#FD5523"))
                holder.binding.liveTimeChip.setTextColor(Color.parseColor("#FD5523"))
                holder.binding.liveTimeChip.setChipStrokeColorResource(R.color.basil_orange)
                holder.binding.liveZoneChip.setTextColor(Color.parseColor("#FD5523"))
                holder.binding.liveZoneChip.setChipStrokeColorResource(R.color.basil_orange)
                holder.binding.liveMessageCard.strokeColor = Color.parseColor("#FD5523")
        } else {
            holder.binding.liveName.setTextColor(Color.parseColor("#356859"))
            holder.binding.liveEventTypeText.setTextColor(Color.parseColor("#356859"))
            holder.binding.view2.setBackgroundColor(Color.parseColor("#356859"))
            holder.binding.view3.setBackgroundColor(Color.parseColor("#356859"))
            holder.binding.liveTimeChip.setTextColor(Color.parseColor("#356859"))
            holder.binding.liveTimeChip.setChipStrokeColorResource(R.color.basil_green_dark)
            holder.binding.liveZoneChip.setTextColor(Color.parseColor("#356859"))
            holder.binding.liveZoneChip.setChipStrokeColorResource(R.color.basil_green_dark)
            holder.binding.liveMessageCard.strokeColor = Color.parseColor("#356859")
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

        holder.bind(event)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }
}

sealed class EventType(var data: Event) {
}