package com.aqua.hoophelper.team.child.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.enums.Align
import com.aqua.hoophelper.R
import com.aqua.hoophelper.databinding.ChartFragmentBinding
import com.anychart.enums.LegendLayout

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.widget.ArrayAdapter
import com.anychart.APIlib
import com.anychart.AnyChartView
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
    ): View? {
        // binding
        val binding: ChartFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.chart_fragment, container,false)

        // spinner
        val playerAdapter = ArrayAdapter(requireContext(), R.layout.home_team_item, viewModel.playerList)
        binding.playerEdit.setAdapter(playerAdapter)
        viewModel.roster.observe(viewLifecycleOwner) {
            it.forEachIndexed { index, player ->
                viewModel.playerList.add(index, player.name)
            }
            binding.playerEdit.setText(viewModel.playerList.first(), false)
        }
        binding.playerEdit.setOnItemClickListener { parent, view, position, id ->
            val player = viewModel.roster.value?.get(position)
            viewModel.getPlayerStats(player!!.id)
        }

        /**
         * pie
         */

        viewModel.selectedPlayerData.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                val colors = mutableListOf<Int>()
                colors.add(resources.getColor(R.color.pie1))
                colors.add(resources.getColor(R.color.pie2))
                colors.add(resources.getColor(R.color.pie3))
                colors.add(resources.getColor(R.color.pie4))
                colors.add(resources.getColor(R.color.pie5))
                colors.add(resources.getColor(R.color.pie6))
                colors.add(resources.getColor(R.color.pie7))
                colors.add(resources.getColor(R.color.pie8))
                colors.add(resources.getColor(R.color.pie9))
                colors.add(resources.getColor(R.color.pie10))
                colors.add(resources.getColor(R.color.pie11))
                colors.add(resources.getColor(R.color.pie12))
                colors.add(resources.getColor(R.color.pie13))
                colors.add(resources.getColor(R.color.pie14))
                colors.add(resources.getColor(R.color.pie15))

                val dataP = mutableListOf<PieEntry>()
                dataP.add(PieEntry(it.filter { it.zone == 1 }.filter { it.score2 == true }.size.toFloat(),"Around Rim"))
                dataP.add(PieEntry(it.filter { it.zone == 2 }.filter { it.score2 == true }.size.toFloat(),"Left Elbow"))
                dataP.add(PieEntry(it.filter { it.zone == 3 }.filter { it.score2 == true }.size.toFloat(),"Mid Straight"))
                dataP.add(PieEntry(it.filter { it.zone == 4 }.filter { it.score2 == true }.size.toFloat(),"Right Elbow"))
                dataP.add(PieEntry(it.filter { it.zone == 5 }.filter { it.score2 == true }.size.toFloat(),"Left Baseline"))
                dataP.add(PieEntry(it.filter { it.zone == 6 }.filter { it.score2 == true }.size.toFloat(),"Left Wing"))
                dataP.add(PieEntry(it.filter { it.zone == 7 }.filter { it.score2 == true }.size.toFloat(),"Long Straight"))
                dataP.add(PieEntry(it.filter { it.zone == 8 }.filter { it.score2 == true }.size.toFloat(),"Right Wing"))
                dataP.add(PieEntry(it.filter { it.zone == 9 }.filter { it.score2 == true }.size.toFloat(),"Right Baseline"))
                dataP.add(PieEntry(it.filter { it.zone == 10 }.filter { it.score3 == true }.size.toFloat(),"Left Corner"))
                dataP.add(PieEntry(it.filter { it.zone == 11 }.filter { it.score3 == true }.size.toFloat(),"Left 3Points"))
                dataP.add(PieEntry(it.filter { it.zone == 12 }.filter { it.score3 == true }.size.toFloat(),"Top of Arc"))
                dataP.add(PieEntry(it.filter { it.zone == 13 }.filter { it.score3 == true }.size.toFloat(),"Right 3Points"))
                dataP.add(PieEntry(it.filter { it.zone == 14 }.filter { it.score3 == true }.size.toFloat(),"Right Corner"))
                dataP.add(PieEntry(it.filter { it.zone == -1 }.filter { it.freeThrow == true }.size.toFloat(),"FreeThrow Line"))


                val dataSet = PieDataSet(dataP, "labels")
                dataSet.valueTextSize = 15f
                dataSet.valueTextColor = Color.parseColor("#00000000")
                dataSet.colors = colors
                val pieData = PieData(dataSet)
                pieData.setDrawValues(true)
                binding.chartPie.apply {
                    setHoleColor(Color.parseColor("#FFE9C2"))
                    centerText = "Hot Spot Score"
                    setCenterTextColor(Color.parseColor("#356859"))
                    setCenterTextSize(25f)
                    legend.let {
                        it.textSize = 15f
                        it.setDrawInside(false)
                        it.isWordWrapEnabled = true
                        it.textColor = Color.parseColor("#356859")
                    }
                    setEntryLabelTextSize(15f)
                    setEntryLabelColor(Color.parseColor("#356859"))
                    setDrawEntryLabels(false)
                    data = pieData
                    invalidate()
                }
            }
        }
        return binding.root
    }
}