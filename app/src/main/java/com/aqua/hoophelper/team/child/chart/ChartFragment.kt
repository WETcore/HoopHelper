package com.aqua.hoophelper.team.child.chart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.data.Set
import com.anychart.core.radar.series.Line
import com.anychart.data.Mapping
import com.anychart.enums.Align
import com.anychart.enums.MarkerType
import com.aqua.hoophelper.R
import com.aqua.hoophelper.databinding.ChartFragmentBinding


class ChartFragment : Fragment() {

    private val viewModel: ChartViewModel by lazy {
        ViewModelProvider(this).get(ChartViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // binding
        val binding: ChartFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.chart_fragment, container,false)

        val radar = AnyChart.radar()

        radar.yScale().minimum(0.0)
        radar.yScale().minimumGap(0.0)
        radar.yScale().ticks().interval(10.0)
        radar.xAxis().labels().padding(1.0, 1.0, 1.0, 1.0)

        radar.legend()
            .align(Align.CENTER)
            .enabled(true)

        val data: MutableList<DataEntry> = mutableListOf()

        val set = Set.instantiate()

        viewModel.events.observe(viewLifecycleOwner) {
            Log.d("chart","1 ${it}")
            data.add(CustomDataEntry("Score", viewModel.transData()?.filter { it.freeThrow == true }?.size, 19))
            data.add(CustomDataEntry("Rebound", 7, 12))
            data.add(CustomDataEntry("Assist", 14, 17))
            data.add(CustomDataEntry("Steal", 13, 3))
            data.add(CustomDataEntry("Block", 15, 6))

            set.data(data)
            val data1 = set.mapAs("{ x: 'x', value: 'value' }")
            val data2 = set.mapAs("{ x: 'x', value: 'value2' }")

            viewModel.roster.observe(viewLifecycleOwner) {
                Log.d("chart","2 ${it}")
                val shamanLine: Line = radar.line(data1)
                shamanLine.name(it.first().name)
                shamanLine.markers()
                    .enabled(true)
                    .type(MarkerType.SQUARE)
                    .size(3.0)

                val warriorLine: Line = radar.line(data2)
                warriorLine.name(it[1].name)
                warriorLine.markers()
                    .enabled(true)
                    .type(MarkerType.CIRCLE)
                    .size(3.0)
            }
        }

        binding.chartRadar.setChart(radar)

        return binding.root
    }

}

private class CustomDataEntry(x: String?, value: Number?, value2: Number?) :
    ValueDataEntry(x, value) {
    init {
        setValue("value2", value2)
    }
}