package com.example.analytics.data.repository

import com.example.analytics.data.model.AnalyticsModel
import kotlinx.coroutines.flow.Flow

// ─────────────────────────────────────────────────────────────────────────────
// WHAT IS THE REPOSITORY PATTERN?
//
// In MVVM, the ViewModel should NOT directly talk to the network, database, or
// any data source. Instead, it talks to a "Repository" — a middleman that
// hides all data-fetching details behind a clean, simple function.
//
// Think of it like a restaurant:
//   - ViewModel = the waiter (asks for the dish, delivers it to the table)
//   - Repository = the kitchen (knows HOW to make the dish)
//   - Data source = ingredients (network, database, hardcoded data, etc.)
//
// The waiter doesn't care if the kitchen uses a gas stove or electric. It just
// says "give me order #5" and the kitchen handles the rest.
// ─────────────────────────────────────────────────────────────────────────────

// WHY AN INTERFACE?
//
// This is an interface, not a class — it only DECLARES what the repository can
// do, not HOW it does it. The concrete implementation lives in
// AnalyticsRepositoryImpl.kt.
//
// Benefit: The ViewModel only knows about this interface. You can swap the
// implementation (e.g. use hardcoded data today, real network tomorrow) without
// touching the ViewModel at all.
interface AnalyticsRepository {

    // WHAT IS Flow<Result<AnalyticsModel>>?
    //
    // Flow — a Kotlin coroutines concept. Think of it like a pipe: data flows
    // through it over time. The ViewModel "collects" from the pipe and reacts
    // each time a new value arrives. It is asynchronous (doesn't block the UI).
    //
    // Result<AnalyticsModel> — a wrapper that holds EITHER a success value
    // (the AnalyticsModel) OR a failure (an Exception). This lets us handle
    // both outcomes in one place without try/catch everywhere.
    //
    // So the full type: "a stream that will eventually emit either the data
    // or an error"
    fun fetchAnalytics(durationDays: Long): Flow<Result<AnalyticsModel>>
}
