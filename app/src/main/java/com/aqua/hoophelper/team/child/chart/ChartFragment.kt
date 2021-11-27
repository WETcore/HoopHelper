package com.aqua.hoophelper.team.child.chart

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aqua.hoophelper.R
import com.aqua.hoophelper.databinding.ChartFragmentBinding
import com.aqua.hoophelper.util.LoadApiStatus
import com.aqua.hoophelper.util.Zone
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry


class ChartFragment : Fragment() {

    private val viewModel: ChartViewModel by lazy {
        ViewModelProvider(this).get(ChartViewModel::class.java)
    }

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // binding
        val binding: ChartFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.chart_fragment, container, false)

        viewModel.status.observe(viewLifecycleOwner) {
            when (it) {
                LoadApiStatus.LOADING -> {
                    binding.lottieChart.visibility = View.VISIBLE
                    binding.chartLayout.visibility = View.GONE
                }
                LoadApiStatus.DONE -> {
                    binding.lottieChart.visibility = View.GONE
                    binding.chartLayout.visibility = View.VISIBLE
                }
                LoadApiStatus.ERROR -> {

                }
            }
        }

        // spinner
        val playerAdapter =
            ArrayAdapter(requireContext(), R.layout.home_team_item, viewModel.players)
        binding.playerEdit.setAdapter(playerAdapter)
        viewModel.roster.observe(viewLifecycleOwner) {
            it.forEachIndexed { index, player ->
                viewModel.players.add(index, player.name)
            }
            binding.playerEdit.setText(viewModel.players.first(), false)
        }
        binding.playerEdit.setOnItemClickListener { parent, view, position, id ->
            val player = viewModel.roster.value?.get(position)
            viewModel.getPlayerStats(player?.id ?: "")
        }

        /**
         * pie
         */

        viewModel.selectedPlayerData.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                val colors = mutableListOf<Int>()
                colors.add(resources.getColor(R.color.pie1, null))
                colors.add(resources.getColor(R.color.pie2, null))
                colors.add(resources.getColor(R.color.pie3, null))
                colors.add(resources.getColor(R.color.pie4, null))
                colors.add(resources.getColor(R.color.pie5, null))
                colors.add(resources.getColor(R.color.pie6, null))
                colors.add(resources.getColor(R.color.pie7, null))
                colors.add(resources.getColor(R.color.pie8, null))
                colors.add(resources.getColor(R.color.pie9, null))
                colors.add(resources.getColor(R.color.pie10, null))
                colors.add(resources.getColor(R.color.pie11, null))
                colors.add(resources.getColor(R.color.pie12, null))
                colors.add(resources.getColor(R.color.pie13, null))
                colors.add(resources.getColor(R.color.pie14, null))
                colors.add(resources.getColor(R.color.pie15, null))

                val dataP = mutableListOf<PieEntry>()
                dataP.apply {
                    add(PieEntry(it.filter { it.zone == 1 }
                        .filter { it.score2 == true }.size.toFloat(), Zone.AROUND_RIM.value))
                    add(PieEntry(it.filter { it.zone == 2 }
                        .filter { it.score2 == true }.size.toFloat(), Zone.L_ELBOW.value))
                    add(PieEntry(it.filter { it.zone == 3 }
                        .filter { it.score2 == true }.size.toFloat(), Zone.MID_STR.value))
                    add(PieEntry(it.filter { it.zone == 4 }
                        .filter { it.score2 == true }.size.toFloat(), Zone.R_ELBOW.value))
                    add(PieEntry(it.filter { it.zone == 5 }
                        .filter { it.score2 == true }.size.toFloat(), Zone.L_BASELINE.value))
                    add(PieEntry(it.filter { it.zone == 6 }
                        .filter { it.score2 == true }.size.toFloat(), Zone.L_WING.value))
                    add(PieEntry(it.filter { it.zone == 7 }
                        .filter { it.score2 == true }.size.toFloat(), Zone.LONG_STR.value))
                    add(PieEntry(it.filter { it.zone == 8 }
                        .filter { it.score2 == true }.size.toFloat(), Zone.R_WING.value))
                    add(PieEntry(it.filter { it.zone == 9 }
                        .filter { it.score2 == true }.size.toFloat(), Zone.R_BASELINE.value))
                    add(PieEntry(it.filter { it.zone == 10 }
                        .filter { it.score3 == true }.size.toFloat(), Zone.L_CORNER.value))
                    add(PieEntry(it.filter { it.zone == 11 }
                        .filter { it.score3 == true }.size.toFloat(), Zone.L_3PT.value))
                    add(PieEntry(it.filter { it.zone == 12 }
                        .filter { it.score3 == true }.size.toFloat(), Zone.ARC.value))
                    add(PieEntry(it.filter { it.zone == 13 }
                        .filter { it.score3 == true }.size.toFloat(), Zone.R_3PT.value))
                    add(PieEntry(it.filter { it.zone == 14 }
                        .filter { it.score3 == true }.size.toFloat(), Zone.R_CORNER.value))
                    add(PieEntry(it.filter { it.zone == -1 }
                        .filter { it.freeThrow == true }.size.toFloat(), Zone.FT.value))
                }

                val dataSet = PieDataSet(dataP, "labels")
                dataSet.apply {
                    valueTextSize = 15f
                    valueTextColor = resources.getColor(R.color.transparent, null)
                    this.colors = colors
                }
                val pieData = PieData(dataSet)
                pieData.setDrawValues(true)

                binding.chartPie.apply {
                    setHoleColor(resources.getColor(R.color.basil_background, null))
                    centerText = "Hot Spot Score"
                    setCenterTextColor(resources.getColor(R.color.basil_green_dark, null))
                    setCenterTextSize(25f)
                    legend.let {
                        it.textSize = 15f
                        it.setDrawInside(false)
                        it.isWordWrapEnabled = true
                        it.textColor = resources.getColor(R.color.basil_green_dark, null)
                    }
                    setEntryLabelTextSize(15f)
                    setEntryLabelColor(resources.getColor(R.color.basil_green_dark, null))
                    setDrawEntryLabels(false)
                    data = pieData
                    invalidate()
                    viewModel._status.value = LoadApiStatus.DONE
                }
            }
        }
        return binding.root
    }
}