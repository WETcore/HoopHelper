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
import android.widget.ArrayAdapter


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

        val pie = AnyChart.pie()

        /**
         * pie
         */
        val dataP = mutableListOf<DataEntry>()
        dataP.add(ValueDataEntry("Around Rim", 63))
        dataP.add(ValueDataEntry("Left Elbow", 78))
        dataP.add(ValueDataEntry("Mid Straight", 72))
        dataP.add(ValueDataEntry("Right Elbow", 14))
        dataP.add(ValueDataEntry("Left Baseline", 12))
        dataP.add(ValueDataEntry("Left Wing", 63))
        dataP.add(ValueDataEntry("Long Straight", 78))
        dataP.add(ValueDataEntry("Right Wing", 72))
        dataP.add(ValueDataEntry("Right Baseline", 14))
        dataP.add(ValueDataEntry("Left Corner", 12))
        dataP.add(ValueDataEntry("Left 3Points", 63))
        dataP.add(ValueDataEntry("Top of Arc", 78))
        dataP.add(ValueDataEntry("Right 3Points", 72))
        dataP.add(ValueDataEntry("Right Corner", 14))
        dataP.add(ValueDataEntry("FreeThrow Line", 200))
        pie.data(dataP)

        pie.title("Hot Spot")

        pie.labels().position("outside")

        pie.legend()
            .position("center-bottom")
            .itemsLayout(LegendLayout.HORIZONTAL)
            .align(Align.CENTER)

        /////////////////////

        pie.background().enabled(true)
        pie.background().fill("#FFE9C2")

        binding.chartPie.setChart(pie)

        val playerAdapter = ArrayAdapter(requireContext(), R.layout.home_team_item, viewModel.playerList)
        binding.chartEdit.setAdapter(playerAdapter)
        binding.chartEdit.setText(viewModel.playerList.first(), false)
        binding.chartEdit.setOnItemClickListener { parent, view, position, id ->
        }

        return binding.root
    }
}