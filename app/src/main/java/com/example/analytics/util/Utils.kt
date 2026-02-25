package com.example.analytics.util

import java.text.DecimalFormat
import java.util.UUID

object Utils {

    fun generateUUID(): String = UUID.randomUUID().toString()

    // Returns a formatter that converts a cents string (e.g. "12345") → "123.45"
    fun getCurrencyStringAmountFormatter(): (String) -> String? {
        val format = DecimalFormat("#,##0.00")
        return { amountString ->
            try {
                val amount = amountString.toLong()
                format.format(amount / 100.0)
            } catch (e: NumberFormatException) {
                amountString
            }
        }
    }
}
