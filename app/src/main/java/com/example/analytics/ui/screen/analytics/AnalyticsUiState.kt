package com.example.analytics.ui.screen.analytics

import com.example.analytics.data.model.AnalyticsModel

// ─────────────────────────────────────────────────────────────────────────────
// WHAT IS UI STATE?
//
// In MVVM, the UI should be a pure reflection of "state" — a snapshot of all
// the data the screen needs to display at any given moment.
//
// Instead of having separate variables scattered around (isLoading, currentData,
// errorMessage...) we group everything into ONE data class. At any point in time,
// the screen looks at this single object and decides what to show.
//
// EXAMPLE STATES:
//   Loading:   AnalyticsUiState(isLoading = true)
//   Success:   AnalyticsUiState(analyticsData = someModel)
//   Error:     AnalyticsUiState(error = "Connection failed")
// ─────────────────────────────────────────────────────────────────────────────

// data class — a Kotlin class that automatically generates equals(), hashCode(),
// toString() and copy(). The copy() function is especially useful: it lets you
// create a new state with only one field changed while keeping all others the
// same. Example: state.copy(isLoading = false)
data class AnalyticsUiState(

    // true while we are waiting for data (shows a spinner on screen)
    val isLoading: Boolean = false,

    // the fetched analytics data; null means we haven't loaded anything yet
    val analyticsData: AnalyticsModel? = null,

    // non-null when something went wrong (shows an error message on screen)
    val error: String? = null,

    // the duration label the user currently has selected in the dropdown
    val selectedDurationLabel: String = "1 Day",

    // the currency symbol/code to display next to amounts (e.g. "GBP")
    val currency: String = "GBP"
)
