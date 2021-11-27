package com.aqua.hoophelper.live

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.aqua.hoophelper.R
import com.aqua.hoophelper.database.Event
import com.aqua.hoophelper.databinding.LiveFragmentBinding
import com.aqua.hoophelper.util.HoopInfo

class LiveFragment : Fragment() {

    private val viewModel: LiveViewModel by lazy {
        ViewModelProvider(this).get(LiveViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // binding
        val binding: LiveFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.live_fragment, container, false)

        // safe args
        val args: LiveFragmentArgs by navArgs()

        if (args.isGaming) {
            setGamingVisibility(binding)

            // adapt recycler
            var adapter: LiveEventAdapter

            viewModel.events.observe(viewLifecycleOwner) { its ->
                val events = its.filter {
                    it.matchId == its.first().matchId
                            && it.teamId == HoopInfo.spinnerSelectedTeamId.value
                }

                adapter = LiveEventAdapter(viewModel, events)
                binding.liveRecycler.adapter = adapter
                adapter.submitList(events)
            }

        } else {
            setNoGameVisibility(binding)
        }

        return binding.root
    }

    private fun setNoGameVisibility(binding: LiveFragmentBinding) {
        binding.lottieNothing.visibility = View.VISIBLE
        binding.nothingText.visibility = View.VISIBLE
        binding.liveRecycler.visibility = View.GONE
    }

    private fun setGamingVisibility(binding: LiveFragmentBinding) {
        binding.lottieNothing.visibility = View.GONE
        binding.nothingText.visibility = View.GONE
        binding.liveRecycler.visibility = View.VISIBLE
    }
}