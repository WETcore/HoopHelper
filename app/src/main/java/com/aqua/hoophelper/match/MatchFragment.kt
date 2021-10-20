package com.aqua.hoophelper.match

import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.aqua.hoophelper.NavigationDirections
import com.aqua.hoophelper.R
import com.aqua.hoophelper.databinding.MatchFragmentBinding
import com.yxf.clippathlayout.PathInfo
import com.yxf.clippathlayout.pathgenerator.CirclePathGenerator

enum class DataType {
    REBOUND, ASSIST, STEAL, BLOCK, TURNOVER, FOUL, FREETHROW
}

class MatchFragment : Fragment() {


    private val viewModel: MatchViewModel by lazy {
        ViewModelProvider(this).get(MatchViewModel::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.N)
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
            if (it == 0L) {
                viewModel._shotClock.value = 24L
            }
        }

        // set game clock
        viewModel.gameClockSecTimer.start()
        viewModel.gameClockSec.observe(viewLifecycleOwner) {
            binding.gameClockSec.text = it.toString()
            viewModel.setGameClockMin(viewModel.gameClockSec.value!!)
            if (it == 0L) {
                viewModel._gameClockSec.value = 60L
            }
        }
        viewModel.gameClockMin.observe(viewLifecycleOwner) {
            binding.gameClockMin.text = it.toString()
            if (it == 0L) {
                viewModel._quarter.value = viewModel._quarter.value?.plus(1)
                viewModel._gameClockMin.value = 12L
            }
        }
        viewModel.quarter.observe(viewLifecycleOwner) {
            if (it == 5) {
                findNavController().navigate(NavigationDirections.navToHome())
                viewModel.shotClockTimer.cancel()
                viewModel.gameClockSecTimer.cancel()
                Toast.makeText(requireContext(),"Game over.",Toast.LENGTH_SHORT).show()
            }
            binding.quarter.text = it.toString()
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
            Log.d("zone1","zone1 ${binding.launchChip.x} ${binding.launchChip.y} ${binding.chipGroup.x}")
        }

        binding.zone2Button.setOnClickListener {
            viewModel.selectZone(2)
            Log.d("zone2","zone2 ${viewModel.event}")
        }



        //開啟子選單
        binding.launchChip.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.chipGroup.visibility = View.VISIBLE
            } else {
                binding.chipGroup.visibility = View.GONE
            }
        }

        // 拖曳
        binding.launchChip.setOnLongClickListener {
            // 震動
            it.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
            if (viewModel.player != -1) {
                // 拖曳
                val shadow = View.DragShadowBuilder(it)
                it.startDragAndDrop(null, shadow, it,0)
                true
            } else {
                Toast.makeText(requireContext(),"Selected Player first.",Toast.LENGTH_SHORT).show()
                false
            }
        }
        binding.root.setOnDragListener{ v, event ->
            when(event.action) {
                DragEvent.ACTION_DROP -> {

                    binding.chipGroup.x = event.x - binding.bufferChip.x
                    binding.chipGroup.y = event.y - binding.bufferChip.y
                    binding.launchChip.x = event.x
                    binding.launchChip.y = event.y
                }
            }
            true
        }


//        binding.launchChip.setOnCheckedChangeListener { buttonView, isChecked ->
//            if (isChecked) {
//                if (viewModel.player != -1) {
//                    binding.chipGroup.visibility = View.VISIBLE
//                } else {
//                    Toast.makeText(requireContext(),"Selected Player first.",Toast.LENGTH_SHORT).show()
//                    buttonView.isChecked = false
//                }
//            } else {
//                viewModel.db.collection("Events").add(viewModel.event)
//                Log.d("record","${viewModel.event}")
//                binding.chipGroup.visibility = View.GONE
//            }
//        }


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
                        viewModel.gameClockMin.value, viewModel.gameClockSec.value)
                    group.clearCheck()
                }
                R.id.assist_chip -> {
                    viewModel.setStatData(viewModel.player, DataType.ASSIST,
                        viewModel.gameClockMin.value, viewModel.gameClockSec.value)
                    group.clearCheck()
                }
                R.id.steal_chip -> {
                    viewModel.setStatData(viewModel.player, DataType.STEAL,
                        viewModel.gameClockMin.value, viewModel.gameClockSec.value)
                    group.clearCheck()
                }
                R.id.block_chip -> {
                    viewModel.setStatData(viewModel.player, DataType.BLOCK,
                        viewModel.gameClockMin.value, viewModel.gameClockSec.value)
                    group.clearCheck()
                }
                R.id.turnover_chip -> {
                    viewModel.setStatData(viewModel.player, DataType.TURNOVER,
                        viewModel.gameClockMin.value, viewModel.gameClockSec.value)
                    group.clearCheck()
                }
                R.id.foul_chip -> {
                    viewModel.setStatData(viewModel.player, DataType.FOUL,
                        viewModel.gameClockMin.value, viewModel.gameClockSec.value)
                    group.clearCheck()
                }
                R.id.free_throw_chip -> {
                    viewModel.setStatData(viewModel.player, DataType.FREETHROW,
                        viewModel.gameClockMin.value, viewModel.gameClockSec.value)
                    group.clearCheck()
                }
            }
            binding.launchChip.isChecked = false
        }

        return binding.root
    }

}