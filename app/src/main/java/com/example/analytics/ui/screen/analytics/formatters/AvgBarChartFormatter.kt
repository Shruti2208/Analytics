package com.example.analytics.ui.screen.analytics.formatters

import com.example.analytics.util.Utils
import com.github.mikephil.charting.formatter.ValueFormatter

class AvgBarChartFormatter : ValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        // Chart stores display amount (e.g. 123.45), convert back to cents for formatter
        val valueInCents = (value * 100).toInt()
        val formatter = Utils.getCurrencyStringAmountFormatter()
        return formatter(valueInCents.toString()) ?: value.toString()
    }
}
