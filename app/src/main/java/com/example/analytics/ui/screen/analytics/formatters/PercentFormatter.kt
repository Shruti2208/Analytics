package com.example.analytics.ui.screen.analytics.formatters

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

open class PercentFormatter : ValueFormatter() {
    private var mFormat: DecimalFormat = DecimalFormat("##0.0")

    override fun getFormattedValue(value: Float): String {
        return mFormat.format(value.toDouble()) + " %"
    }
}
