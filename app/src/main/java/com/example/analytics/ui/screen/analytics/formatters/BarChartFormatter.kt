package com.example.analytics.ui.screen.analytics.formatters

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

class BarChartFormatter : ValueFormatter() {
    private val mFormat = DecimalFormat("#")

    override fun getFormattedValue(value: Float): String {
        return mFormat.format(value)
    }
}
