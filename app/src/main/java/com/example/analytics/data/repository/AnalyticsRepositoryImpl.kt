package com.example.analytics.data.repository

import com.example.analytics.data.model.AnalyticsDataEntry
import com.example.analytics.data.model.AnalyticsModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

// ─────────────────────────────────────────────────────────────────────────────
// WHAT IS THIS FILE?
//
// This is the concrete implementation of AnalyticsRepository. It is the
// "kitchen" — it knows HOW to get the data. Right now it returns hardcoded
// data. Later you will replace buildHardcodedModel() with a real network call.
//
// The ViewModel will never know the difference because it only talks to the
// AnalyticsRepository interface, not this class directly.
// ─────────────────────────────────────────────────────────────────────────────

// @Inject constructor()
// This tells Hilt: "you are allowed to create this class yourself when needed".
// Hilt reads this and knows it can instantiate AnalyticsRepositoryImpl with
// no arguments. Without @Inject, Hilt would not know how to create it.
class AnalyticsRepositoryImpl @Inject constructor() : AnalyticsRepository {

    // flow { } — creates a cold Flow. "Cold" means it only starts executing
    // when someone starts collecting from it (i.e. when the ViewModel calls
    // .collect { }). Nothing happens until then.
    //
    // Think of it like a function: it doesn't run until it's called.
    override fun fetchAnalytics(durationDays: Long): Flow<Result<AnalyticsModel>> = flow {

        // delay() — pauses execution for 800ms WITHOUT blocking the UI thread.
        // This simulates a real network call taking time to respond.
        // Unlike Thread.sleep(), Kotlin's delay() is "suspending" — it yields
        // the thread to other work while waiting, so the UI stays responsive.
        delay(800)

        // emit() — pushes a value into the Flow pipe.
        // Result.success() wraps our data in a "success" Result so the
        // ViewModel knows the fetch was successful.
        emit(Result.success(buildHardcodedModel()))
    }

    // Builds a fake AnalyticsModel with realistic-looking data.
    // TODO: Replace this with a real API/WebSocket call when ready.
    private fun buildHardcodedModel(): AnalyticsModel {
        val methods = listOf("Visa", "Mastercard", "PayPal", "Apple Pay", "Google Pay")
        val percents = listOf(35.0, 25.0, 20.0, 12.0, 8.0)

        // Values are stored in cents (smallest currency unit) to avoid
        // floating point precision issues. 175000 cents = £1750.00
        val totals = listOf(175000L, 125000L, 100000L, 60000L, 40000L)

        // Number of transactions per payment method
        val counts = listOf(350L, 250L, 200L, 120L, 80L)

        // Average transaction value per method in cents. 50000 = £500.00
        val averages = listOf(50000L, 60000L, 70000L, 90000L, 30000L)

        // apply { } — a Kotlin scope function. It lets you set multiple
        // properties on an object in a clean block without repeating the
        // variable name. It returns the object itself.
        return AnalyticsModel().apply {
            dataSumAmount = methods.mapIndexed { i, method ->
                AnalyticsDataEntry(method = method, value = totals[i], percent = percents[i])
            }
            dataNumberOfTransactions = methods.mapIndexed { i, method ->
                AnalyticsDataEntry(method = method, value = counts[i], percent = percents[i])
            }
            dataAverageTransactionValue = methods.mapIndexed { i, method ->
                AnalyticsDataEntry(method = method, value = averages[i], percent = percents[i])
            }
        }
    }
}
