package com.example.analytics.ui.screen.analytics

import com.example.analytics.R
import com.example.analytics.data.model.AnalyticsDataEntry
import android.content.Context
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

class BarGraphUtils {
    companion object {
        fun displayBarGraph(
            data: List<AnalyticsDataEntry>?,
            barChart: BarChart,
            context: Context,
            formatter: ValueFormatter,
            isSimpleBar: Boolean
        ) {
            if (data != null) {
                initBarChart(barChart, data)
            }

            val entries = ArrayList<BarEntry>()
            val chartColors = context.resources.getIntArray(R.array.chartColors)
            val colors = ArrayList<Int>()

            if (isSimpleBar) {
                data?.forEachIndexed { i, entry ->
                    entries.add(BarEntry(i.toFloat(), entry.value!!.toFloat()))
                    colors.add(chartColors[i % chartColors.size])
                }
            } else {
                data?.forEachIndexed { i, entry ->
                    val displayAmount = (entry.value ?: 0L) / 100f
                    entries.add(BarEntry(i.toFloat(), displayAmount))
                    colors.add(chartColors[i % chartColors.size])
                }
            }

            val barDataSet = BarDataSet(entries, "")
            barDataSet.colors = colors

            val barData = BarData(barDataSet)
            barChart.data = barData
            barData.setValueFormatter(formatter)
            barChart.invalidate()
        }

        private fun initBarChart(barChart: BarChart, data: List<AnalyticsDataEntry>) {
            barChart.axisLeft.setDrawGridLines(false)
            val xAxis: XAxis = barChart.xAxis
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(false)
            barChart.axisRight.isEnabled = false
            barChart.legend.isEnabled = false
            barChart.axisLeft.granularity = 1f
            barChart.description.isEnabled = false
            barChart.animateY(1000)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.valueFormatter = MyAxisFormatter(data)
            xAxis.setDrawLabels(true)
            xAxis.granularity = 1f
            xAxis.labelRotationAngle = +90f
        }
    }
}

class MyAxisFormatter(private val data: List<AnalyticsDataEntry>) : IndexAxisValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val index = value.toInt()
        return if (index < data.size) data[index].method ?: "" else ""
    }
}
