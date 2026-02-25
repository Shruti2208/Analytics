package com.example.analytics.data.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class AnalyticsModel {
    var dataSumAmount: List<AnalyticsDataEntry>? = null
    var dataNumberOfTransactions: List<AnalyticsDataEntry>? = null
    var dataAverageTransactionValue: List<AnalyticsDataEntry>? = null

    // TODO: Update field names to match your actual server JSON response keys
    fun setParameters(message: String) {
        val response = Gson().fromJson(message, AnalyticsResponse::class.java)
        dataSumAmount = response?.dataSumAmount
        dataNumberOfTransactions = response?.dataNumberOfTransactions
        dataAverageTransactionValue = response?.dataAverageTransactionValue
    }

    private data class AnalyticsResponse(
        @SerializedName("dataSumAmount")
        val dataSumAmount: List<AnalyticsDataEntry>? = null,
        @SerializedName("dataNumberOfTransactions")
        val dataNumberOfTransactions: List<AnalyticsDataEntry>? = null,
        @SerializedName("dataAverageTransactionValue")
        val dataAverageTransactionValue: List<AnalyticsDataEntry>? = null
    )
}
