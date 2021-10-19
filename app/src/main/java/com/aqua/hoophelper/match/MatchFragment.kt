package com.aqua.hoophelper.match

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.aqua.hoophelper.NavigationDirections
import com.aqua.hoophelper.R
import com.aqua.hoophelper.databinding.MatchFragmentBinding
import com.google.firebase.firestore.FirebaseFirestore

enum class DataType {
    REBOUND, ASSIST, STEAL, BLOCK, TURNOVER, FOUL, FREETHROW
}

class MatchFragment : Fragment() {


    private val viewModel: MatchViewModel by lazy {
        ViewModelProvider(this).get(MatchViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // binding
        val binding: MatchFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.match_fragment, container,false)

        // set shot clock
        viewModel.shotClockTimer.start()
        viewModel.shotClock.observe(viewLifecycleOwner) {
            binding.shotClock.text = it.toString()
        }

        // set game clock
        viewModel.setGameClockMin(binding.gameClockSec.text.toString().toLong())
        viewModel.gameClockSecTimer.start()
        viewModel.gameClockSec.observe(viewLifecycleOwner) {
            binding.gameClockSec.text = it.toString()
            viewModel.gameClockSec.value?.let { sec -> viewModel.setGameClockMin(sec) }
        }

        viewModel.gameClockMin.observe(viewLifecycleOwner) {
            binding.gameClockMin.text = it.toString()
        }

        // set pause
        binding.pauseMatchChip.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                viewModel.shotClockTimer.cancel()
                viewModel.gameClockSecTimer.cancel()
            } else {
                viewModel.shotClockTimer.start()
                viewModel.gameClockSecTimer.start()
            }
            binding.shotClock.text = viewModel.shotClock.value.toString()
            binding.gameClockSec.text = viewModel.gameClockSec.value.toString()
        }
        
        // count free throw
        binding.freeThrowChip.setOnClickListener {

        }

        // set exit match
        binding.exitMatchButton.setOnClickListener {
            viewModel.shotClockTimer.cancel()
            viewModel.gameClockSecTimer.cancel()
            findNavController().navigate(NavigationDirections.navToHome())
        }

        // select player
        binding.playerChipGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.player1_chip -> viewModel.selectPlayer(1)
                R.id.player2_chip -> viewModel.selectPlayer(2)
                R.id.player3_chip -> viewModel.selectPlayer(3)
                R.id.player4_chip -> viewModel.selectPlayer(4)
                R.id.player5_chip -> viewModel.selectPlayer(5)
            }
        }
        /// record data
        // score
        binding.zone1Button.setOnClickListener {
            viewModel.selectZone(1)
            Log.d("zone1","zone1 ${viewModel.events}")
        }

        binding.zone2Button.setOnClickListener {
            viewModel.selectZone(2)
            Log.d("zone2","zone2 ${viewModel.events}")
        }




        binding.launchChip.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (viewModel.player != -1) {
                    binding.chipGroup.visibility = View.VISIBLE
                } else {
                    Toast.makeText(requireContext(),"Selected Player first.",Toast.LENGTH_SHORT).show()
                    buttonView.isChecked = false
                }
            } else {
                viewModel.db.collection("Events").add(viewModel.events)
                Log.d("record","${viewModel.events}")
                binding.chipGroup.visibility = View.GONE
            }
        }


        binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.score_chip -> {
                    if (viewModel.zone != -1) {
                        viewModel.setScoreData(viewModel.player, viewModel.zone,
                            viewModel.gameClockMin.value.toString(), viewModel.gameClockSec.value.toString())
                        viewModel.selectZone(-1)
                    } else {
                        Toast.makeText(requireContext(),"Selected Zone first.",Toast.LENGTH_SHORT).show()
                    }
                    group.clearCheck()
                }
                R.id.rebound_chip -> {
                    viewModel.setStatData(viewModel.player, DataType.REBOUND,
                        viewModel.gameClockMin.value.toString(), viewModel.gameClockSec.value.toString())
                    group.clearCheck()
                }
                R.id.assist_chip -> {
                    viewModel.setStatData(viewModel.player, DataType.ASSIST,
                        viewModel.gameClockMin.value.toString(), viewModel.gameClockSec.value.toString())
                    group.clearCheck()
                }
                R.id.steal_chip -> {
                    viewModel.setStatData(viewModel.player, DataType.STEAL,
                        viewModel.gameClockMin.value.toString(), viewModel.gameClockSec.value.toString())
                    group.clearCheck()
                }
                R.id.block_chip -> {
                    viewModel.setStatData(viewModel.player, DataType.BLOCK,
                        viewModel.gameClockMin.value.toString(), viewModel.gameClockSec.value.toString())
                    group.clearCheck()
                }
                R.id.turnover_chip -> {
                    viewModel.setStatData(viewModel.player, DataType.TURNOVER,
                        viewModel.gameClockMin.value.toString(), viewModel.gameClockSec.value.toString())
                    group.clearCheck()
                }
                R.id.foul_chip -> {
                    viewModel.setStatData(viewModel.player, DataType.FOUL,
                        viewModel.gameClockMin.value.toString(), viewModel.gameClockSec.value.toString())
                    group.clearCheck()
                }
                R.id.free_throw_chip -> {
                    viewModel.setStatData(viewModel.player, DataType.FREETHROW,
                        viewModel.gameClockMin.value.toString(), viewModel.gameClockSec.value.toString())
                    group.clearCheck()
                }
            }
            binding.launchChip.isChecked = false

        }

        return binding.root
    }

}