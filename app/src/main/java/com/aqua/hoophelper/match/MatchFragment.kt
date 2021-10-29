package com.aqua.hoophelper.match

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.aqua.hoophelper.HoopInfo
import com.aqua.hoophelper.NavigationDirections
import com.aqua.hoophelper.R
import com.aqua.hoophelper.database.Event
import com.aqua.hoophelper.database.Match
import com.aqua.hoophelper.database.remote.HoopRemoteDataSource
import com.aqua.hoophelper.databinding.MatchFragmentBinding
import com.google.firebase.firestore.Query

enum class DataType {
    REBOUND, ASSIST, STEAL, BLOCK, TURNOVER, FOUL
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

        viewModel.setRoster()

        // safe arg
        val args: MatchFragmentArgs by navArgs()

        // Hint for user
        Toast.makeText(requireContext(), "drag the red dot to the court.", Toast.LENGTH_SHORT).show()

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
                Toast.makeText (requireContext(),"Game over.",Toast.LENGTH_SHORT).show()
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
            viewModel.db
                .collection("Matches")
                .whereEqualTo("matchId", args.matchId)
                .get()
                .addOnSuccessListener {
                    it.forEach {
                        it.reference.update("gaming",false)
                    }
                }
            findNavController().navigate(NavigationDirections.navToHome())
        }

        // select player
        binding.playerChipGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.player1_chip -> {
                    viewModel.selectPlayer(0)
                }
                R.id.player2_chip -> {
                    viewModel.selectPlayer(1)
                }
                R.id.player3_chip -> {
                    viewModel.selectPlayer(2)
                }
                R.id.player4_chip -> {
                    viewModel.selectPlayer(3)
                }
                R.id.player5_chip -> {
                    viewModel.selectPlayer(4)
                }
            }
        }
        // select substitution player
        viewModel.substitutionPlayer.observe(viewLifecycleOwner) {
            var teamAdapter = ArrayAdapter(requireContext(), R.layout.match_team_item, viewModel.substitutionPlayer.value!!)
            binding.subPlayerText.setAdapter(teamAdapter)
        }


        binding.subPlayerText.setOnItemClickListener { parent, view, position, id ->
            var buffer = viewModel.startPlayer.value!![viewModel.selectPlayerPos]
            viewModel.getSubPlayer(parent.getItemAtPosition(position).toString())
            viewModel.changeSubPlayer(buffer, position)
        }

        viewModel.startPlayer.observe(viewLifecycleOwner) {
            Log.d("tag","${it}")
            if (!it.isNullOrEmpty()) {
                binding.player1Chip.text = it[0]
                binding.player2Chip.text = it[1]
                binding.player3Chip.text = it[2]
                binding.player4Chip.text = it[3]
                binding.player5Chip.text = it[4]
            }
        }


        /// record data

        // 改變launch顯示文字
        viewModel.zone.observe(viewLifecycleOwner) {
            binding.launchChip.text = it.toString()
        }


        //開啟子選單
        binding.launchChip.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {

                binding.chipGroup.visibility = View.VISIBLE
                Log.d("launch","open ${binding.chipGroup.visibility} ${binding.scoreChip.x} ${binding.launchChip.x}")
            } else {
                Log.d("record","${viewModel.event}")
                binding.chipGroup.visibility = View.GONE
                Log.d("launch","close ${binding.chipGroup.visibility}")
            }
        }

        // 拖曳
        binding.launchChip.setOnLongClickListener {
            // 震動
            it.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
            // 拖曳
            val shadow = View.DragShadowBuilder(it)
            it.startDragAndDrop(null, shadow, it,0)
            false
        }
        // 獲取螢幕解析度
        val displayMetrics = DisplayMetrics()
        requireActivity().display?.getRealMetrics(displayMetrics)

        // 拖曳 get zone data
        binding.root.setOnDragListener{ v, event ->
            when(event.action) {
                DragEvent.ACTION_DROP -> {
                    Log.d("pos","${event.x.toInt()} ${event.y.toInt()}")
                    if (viewModel.getDiameter(event.x,event.y,displayMetrics.widthPixels,displayMetrics.heightPixels)) {
                        binding.launchChip.isCheckable = false
                        Toast.makeText(requireContext(),"drag the red dot to the court.",Toast.LENGTH_SHORT).show()
                    } else binding.launchChip.isCheckable = true
                    binding.launchChip.x = (event.x - 60)
                    binding.launchChip.y = (event.y - 60)
                }
            }
            true
        }
        // write data
        binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.score_chip -> {
                    if (viewModel.zone.value?.toInt() != 0) {
                        viewModel.setScoreData(true, args.matchId)
                    } else {
                        Toast.makeText(requireContext(),"Selected Zone first.",Toast.LENGTH_SHORT).show()
                    }
                    group.clearCheck()
                }
                R.id.miss_chip -> {
                    if (viewModel.zone.value?.toInt() != 0) {
                        viewModel.setScoreData(false, args.matchId)
                    } else {
                        Toast.makeText(requireContext(),"Selected Zone first.",Toast.LENGTH_SHORT).show()
                    }
                    group.clearCheck()
                }
                R.id.rebound_chip -> {
                    viewModel.setStatData(DataType.REBOUND, args.matchId)
                    group.clearCheck()
                }
                R.id.assist_chip -> {
                    viewModel.setStatData(DataType.ASSIST, args.matchId)
                    group.clearCheck()
                }
                R.id.steal_chip -> {
                    viewModel.setStatData(DataType.STEAL, args.matchId)
                    group.clearCheck()
                }
                R.id.block_chip -> {
                    viewModel.setStatData(DataType.BLOCK, args.matchId)
                    group.clearCheck()
                }
                R.id.turnover_chip -> {
                    viewModel.setStatData(DataType.TURNOVER, args.matchId)
                    group.clearCheck()
                }
                R.id.foul_chip -> {
                    viewModel.setStatData(DataType.FOUL, args.matchId)
                    group.clearCheck()
                }
            }
            binding.launchChip.isChecked = false
        }

        // 罰球
        binding.freeThrowSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.ftChipGroup.visibility = View.VISIBLE
                viewModel.shotClockTimer.cancel()
                viewModel.gameClockSecTimer.cancel()
            } else {
                binding.ftChipGroup.visibility = View.GONE
                viewModel.shotClockTimer.start()
                viewModel.gameClockSecTimer.start()
            }
        }
        binding.ftInChip.setOnClickListener {
            viewModel.setFreeThrowData(true, args.matchId)
            Log.d("record","${viewModel.event}")
        }
        binding.ftOutChip.setOnClickListener {
            viewModel.setFreeThrowData(false, args.matchId)
            Log.d("record","${viewModel.event}")
        }


        // event history & cancel event
        viewModel.lastEvent.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                binding.historyChip.text = viewModel.setHistoryText(it)
            }
        }

        binding.historyChip.setOnCloseIconClickListener {
            viewModel.cancelEvent()
            binding.historyChip.isCloseIconVisible = false
            binding.historyChip.isChecked = false
        }
        binding.historyChip.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.historyChip.isCloseIconVisible = isChecked
        }

        return binding.root
    }



}