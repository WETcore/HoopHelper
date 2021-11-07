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
import com.aqua.hoophelper.databinding.MatchFragmentBinding

enum class DataType {
    SCORE, REBOUND, ASSIST, STEAL, BLOCK, TURNOVER, FOUL
}

enum class DetailDataType {
    PTS, FG, ThreeP, FT, TOV,
    REB, AST, STL, BLK, PF
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

        // get roster from db
        viewModel.setRoster()

        // safe arg
        val args: MatchFragmentArgs by navArgs()

        // Hint for user
        Toast.makeText(requireContext(), "drag the red dot to the court.", Toast.LENGTH_SHORT).show()

        // set shot clock
        viewModel.shotClock.observe(viewLifecycleOwner) {
            binding.shotClock.text = it.toString()
            if (it == 0L) {
                viewModel._shotClock.value = viewModel.shotClockLimit.value
            }
        }

        // set game clock
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
                viewModel._gameClockMin.value = viewModel.gameClockLimit.value!! - 1
                viewModel._quarter.value = viewModel._quarter.value?.plus(1)
            }
        }
        viewModel.quarter.observe(viewLifecycleOwner) {
            if (it > viewModel.quarterLimit) {
                findNavController().navigate(NavigationDirections.navToHome())
                viewModel.shotClockTimer.cancel()
                viewModel.gameClockSecTimer.cancel()
                Toast.makeText (requireContext(),"Game over.",Toast.LENGTH_SHORT).show()
            }
            else if (it == viewModel.quarterLimit/2) {
                viewModel.timeOut = 0
                binding.pauseMatchChip.isCheckable = true
            }
            binding.quarter.text = it.toString()
        }
        // set pause
        binding.pauseMatchChip.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if(viewModel.setTimeOutCount()) {
                    Toast.makeText(requireContext(), "limit",Toast.LENGTH_SHORT).show()
                    binding.pauseMatchChip.isChecked = false
                    binding.pauseMatchChip.isCheckable = false
                } else {
                    viewModel.shotClockTimer.cancel()
                    viewModel.gameClockSecTimer.cancel()
                }
            } else {
                viewModel.shotClockTimer.start()
                viewModel.gameClockSecTimer.start()
            }
            binding.shotClock.text = viewModel.shotClock.value.toString()
            binding.gameClockSec.text = viewModel.gameClockSec.value.toString()
        }

        // set exit match
        binding.exitMatchButton.setOnClickListener {
//            viewModel.shotClockTimer.cancel()
//            viewModel.gameClockSecTimer.cancel()
            viewModel.db // TODO move to model, auto close game need same fun
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
            viewModel.subNum = mutableListOf<String>()
            it.forEach { player ->
                viewModel.subNum.add(player.number)
            }
            var teamAdapter = ArrayAdapter(requireContext(), R.layout.match_team_item, viewModel.subNum)
            binding.subPlayerText.setAdapter(teamAdapter)
        }


        binding.subPlayerText.setOnItemClickListener { parent, view, position, id ->
            var buffer = viewModel.startPlayer.value!![viewModel.selectPlayerPos]
            viewModel.getSubPlayer2Starting(position)
            viewModel.changeSubPlayer(buffer, position)
        }

        viewModel.startPlayer.observe(viewLifecycleOwner) {
            Log.d("tag","${it}")
            if (!it.isNullOrEmpty()) {
                binding.player1Chip.text = it[0].number
                if (viewModel.playerNum == "") viewModel.playerNum = it.first().number
                if (viewModel.playerName == "") viewModel.playerName = it.first().name
                binding.player2Chip.text = it[1].number
                binding.player3Chip.text = it[2].number
                binding.player4Chip.text = it[3].number
                binding.player5Chip.text = it[4].number
            }
        }


        /// record data

        // 改變launch顯示文字
        viewModel.zone.observe(viewLifecycleOwner) {
            binding.launchChip.text = when(it) {
                1 -> "Around Rim"
                2 -> "L.Elbow"
                3 -> "Mid Straight"
                4 -> "R.Elbow"
                5 -> "L.Baseline"
                6 -> "L.Wing"
                7 -> "Long Straight"
                8 -> "R.Wing"
                9 -> "R.Baseline"
                10 -> "L.Corner"
                11 -> "L.3Points"
                12 -> "Arc"
                13 -> "R.3Points"
                14 -> "R.Corner"
                else -> "Area"
            }
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
                    var buffer = viewModel.startPlayer.value!![viewModel.selectPlayerPos]
                    viewModel.getFoulCount(viewModel.playerNum, buffer)
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
                binding.historyChip.text = viewModel.setHistoryText(it.filter { it.matchId ==HoopInfo.matchId })
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