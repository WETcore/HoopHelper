package com.aqua.hoophelper.live

import android.annotation.SuppressLint
import android.graphics.Color
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

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)

        // set bg color
        if (position % 2 == 0) {
            holder.binding.liveMessageCard.setCardBackgroundColor(Color.parseColor("#FF9800"))
        } else {
            holder.binding.liveMessageCard.setCardBackgroundColor(Color.parseColor("#FF6600"))
        }

        holder.binding.liveEventTypeText.text = viewModel.filterEventType(event)

        holder.bind(event)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

}

sealed class EventType(var data: Event) {
}