package com.example.analytics.ui.screen.analytics

import android.content.Context
import android.graphics.Color
import android.util.Log
import com.example.analytics.R
import com.example.analytics.data.model.AnalyticsModel
import com.example.analytics.ui.screen.analytics.formatters.PercentFormatter
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class PieChartUtils {
    companion object {
        private const val TAG = "PieChartUtils"

        fun displayTransactionVolumesPerApmPieChart(
            ourPieChart: PieChart,
            dataObject: AnalyticsModel,
            context: Context
        ) {
            val ourPieEntry = ArrayList<PieEntry>()
            val pieShades = ArrayList<Int>()
            val chartColors = context.resources.getIntArray(R.array.chartColors)

            val dataSize = dataObject.dataSumAmount?.size ?: 0
            for (i in 0 until dataSize) {
                val entry = dataObject.dataSumAmount!![i]
                Log.w(TAG, "Pie entry $i: ${entry.method} : ${entry.percent}")
                ourPieEntry.add(PieEntry(entry.percent!!.toFloat(), entry.method))
                pieShades.add(chartColors[i % chartColors.size]) // modulo prevents index out of bounds
            }

            val ourSet = PieDataSet(ourPieEntry, "")
            ourSet.colors = pieShades
            ourSet.sliceSpace = 1f
            ourSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            ourSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            ourSet.valueLinePart1OffsetPercentage = 90f
            ourSet.valueLinePart1Length = 0.6f
            ourSet.valueLinePart2Length = 0.6f

            ourPieChart.setExtraOffsets(0f, 20f, 0f, 20f)
            ourPieChart.isDrawHoleEnabled = true
            ourPieChart.setUsePercentValues(true)
            ourPieChart.setEntryLabelColor(Color.BLACK)
            ourPieChart.minAngleForSlices = 20f
            ourPieChart.animateY(1000, Easing.EaseInOutQuad)
            ourPieChart.description.isEnabled = false
            ourPieChart.description.textColor = Color.BLACK
            ourPieChart.setDrawEntryLabels(false)

            ourPieChart.legend.isEnabled = true
            ourPieChart.legend.orientation = Legend.LegendOrientation.HORIZONTAL
            ourPieChart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            ourPieChart.legend.isWordWrapEnabled = true

            ourPieChart.data = PieData(ourSet)
            ourPieChart.data.setDrawValues(true)
            ourPieChart.data.setValueTextColor(Color.BLACK)
            ourPieChart.data.setValueTextSize(12f)
            ourPieChart.data.setValueFormatter(PercentFormatter())

            ourPieChart.invalidate()
        }
    }
}
