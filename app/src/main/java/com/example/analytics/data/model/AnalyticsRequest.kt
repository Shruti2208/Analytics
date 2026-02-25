package com.example.analytics.data.model

data class AnalyticsRequest(
    val operation: String,
    val transactionID: String,
    val tid: String,
    val filters: Filters,
    val paymentMethod: String
)

data class Filters(
    val duration: Long
)
