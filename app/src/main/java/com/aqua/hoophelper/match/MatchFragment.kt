package com.aqua.hoophelper.match

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.aqua.hoophelper.HoopInfo
import com.aqua.hoophelper.NavigationDirections
import com.aqua.hoophelper.R
import com.aqua.hoophelper.databinding.MatchFragmentBinding
import com.aqua.hoophelper.util.LoadApiStatus

enum class DataType { //TODO
    SCORE, REBOUND, ASSIST, STEAL, BLOCK, TURNOVER, FOUL, FREE_THROW
}

enum class DetailDataType { //TODO
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
            DataBindingUtil.inflate(inflater, R.layout.match_fragment, container, false)

        viewModel.status.observe(viewLifecycleOwner) {
            when (it) {
                LoadApiStatus.LOADING -> {
                    binding.lottieMatch.visibility = View.VISIBLE
                    binding.matchLayout.visibility = View.GONE
                }
                LoadApiStatus.DONE -> {
                    binding.lottieMatch.visibility = View.GONE
                    binding.matchLayout.visibility = View.VISIBLE
                }
                LoadApiStatus.ERROR -> {

                }
            }
        }

        // safe arg match id
        val args: MatchFragmentArgs by navArgs()

        // Hint for user
        Toast.makeText(
            requireContext(),
            "Long press & drag the red dot to the court.",
            Toast.LENGTH_LONG
        ).show()

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
                Toast.makeText(requireContext(), "Game over.", Toast.LENGTH_SHORT).show()
            } else if (it == viewModel.quarterLimit / 2) {
                viewModel.timeOut = 0
                binding.pauseMatchChip.isCheckable = true
            }
            binding.quarter.text = it.toString()
        }
        // set pause
        binding.pauseMatchChip.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (viewModel.setTimeOutCount()) {
                    Toast.makeText(requireContext(), "limit", Toast.LENGTH_SHORT).show()
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

        // select player
        binding.playerChipGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
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
            val teamAdapter =
                ArrayAdapter(requireContext(), R.layout.match_team_item, viewModel.subNum)
            binding.subPlayerText.setAdapter(teamAdapter)
        }

        binding.subPlayerText.setOnItemClickListener { parent, view, position, id ->
            val buffer = viewModel.startPlayer.value!![viewModel.selectPlayerPos]
            viewModel.getSubPlayer2Starting(position)
            viewModel.changeSubPlayer(buffer, position)
        }

        viewModel.startPlayer.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                viewModel.setFirstPlayer(it.first())
                binding.player1Chip.text = it[0].number
                binding.player2Chip.text = it[1].number
                binding.player3Chip.text = it[2].number
                binding.player4Chip.text = it[3].number
                binding.player5Chip.text = it[4].number
            }
        }

        /// record data
        // set launch chip text
        viewModel.zone.observe(viewLifecycleOwner) {
            binding.launchChip.text = when (it) {
                1 -> "A"
                2 -> "B"
                3 -> "C"
                4 -> "D"
                5 -> "E"
                6 -> "F"
                7 -> "G"
                8 -> "H"
                9 -> "I"
                10 -> "J"
                11 -> "K"
                12 -> "L"
                13 -> "M"
                14 -> "N"
                else -> "O"
            }
        }

        // launch record chip menu
        binding.launchChip.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.chipGroup.visibility = View.VISIBLE
            } else {
                binding.chipGroup.visibility = View.GONE
            }
        }

        // vibrate & drag
        binding.launchChip.setOnLongClickListener {
            it.performHapticFeedback(
                HapticFeedbackConstants.LONG_PRESS,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            )
            val shadow = View.DragShadowBuilder(it)
            it.startDragAndDrop(null, shadow, it, 0)
            false
        }
        // get screen resolution
        val displayMetrics = DisplayMetrics()
        requireActivity().display?.getRealMetrics(displayMetrics)

        // drag & get zone data
        binding.root.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    if (viewModel.getDiameter(
                            event.x,
                            event.y,
                            displayMetrics.widthPixels,
                            displayMetrics.heightPixels
                        )
                    ) {
                        binding.launchChip.isCheckable = false
                        Toast.makeText(
                            requireContext(),
                            "drag the red dot to the court.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else binding.launchChip.isCheckable = true
                    binding.launchChip.x = (event.x - 60)
                    binding.launchChip.y = (event.y - 60)
                }
            }
            true
        }
        // write data
        binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.score_chip -> {
                    if (viewModel.zone.value?.toInt() != 0) {
                        viewModel.setEventData(args.matchId, DataType.SCORE, true)
                    } else {
                        Toast.makeText(requireContext(), "Selected Zone first.", Toast.LENGTH_SHORT)
                            .show()
                    }
                    group.clearCheck()
                }
                R.id.miss_chip -> {
                    if (viewModel.zone.value?.toInt() != 0) {
                        viewModel.setEventData(args.matchId, DataType.SCORE, false)
                    } else {
                        Toast.makeText(requireContext(), "Selected Zone first.", Toast.LENGTH_SHORT)
                            .show()
                    }
                    group.clearCheck()
                }
                R.id.rebound_chip -> {
                    viewModel.setEventData(args.matchId, DataType.REBOUND, false)
                    group.clearCheck()
                }
                R.id.assist_chip -> {
                    viewModel.setEventData(args.matchId, DataType.ASSIST, false)
                    group.clearCheck()
                }
                R.id.steal_chip -> {
                    viewModel.setEventData(args.matchId, DataType.STEAL, false)
                    group.clearCheck()
                }
                R.id.block_chip -> {
                    viewModel.setEventData(args.matchId, DataType.BLOCK, false)
                    group.clearCheck()
                }
                R.id.turnover_chip -> {
                    viewModel.setEventData(args.matchId, DataType.TURNOVER, false)
                    group.clearCheck()
                }
                R.id.foul_chip -> {
                    viewModel.setEventData(args.matchId, DataType.FOUL, false)
                    val buffer = viewModel.startPlayer.value!![viewModel.selectPlayerPos]
                    viewModel.getFoulCount(viewModel.playerNum, buffer)
                    group.clearCheck()
                }
            }
            binding.launchChip.isChecked = false
        }

        // free throw
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
            viewModel.setEventData(args.matchId, DataType.FREE_THROW, true)
        }
        binding.ftOutChip.setOnClickListener {
            viewModel.setEventData(args.matchId, DataType.FREE_THROW, false)
        }

        // event history & cancel event
        viewModel.lastEvent.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                binding.historyChip.text =
                    viewModel.setHistoryText(it.filter { it.matchId == HoopInfo.matchId })
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