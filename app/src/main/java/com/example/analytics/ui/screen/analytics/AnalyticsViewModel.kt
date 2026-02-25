package com.example.analytics.ui.screen.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.analytics.data.repository.AnalyticsRepository
import com.example.analytics.properties.SharedPreferencesReader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─────────────────────────────────────────────────────────────────────────────
// WHAT IS A VIEWMODEL?
//
// A ViewModel is a class that holds and manages the data for a UI screen.
// It survives configuration changes (like screen rotation) — so if the user
// rotates their phone, the data is NOT lost and the screen doesn't reload.
//
// MVVM ROLE:
//   Model       → AnalyticsRepository (fetches raw data)
//   View        → AnalyticsScreen.kt  (displays the data)
//   ViewModel   → THIS FILE           (sits between them, holds UI state)
//
// The ViewModel:
//   1. Asks the Repository for data
//   2. Transforms the result into a UI-friendly state (AnalyticsUiState)
//   3. Exposes that state to the UI via StateFlow
//   4. Handles user actions (e.g. duration changed)
//
// The ViewModel does NOT know anything about the UI (no Composable imports).
// This separation means you can test it without a device or emulator.
// ─────────────────────────────────────────────────────────────────────────────

// @HiltViewModel — tells Hilt that this ViewModel uses dependency injection.
// Hilt will automatically create it and inject the repository.
// The UI gets the ViewModel via hiltViewModel() in AnalyticsScreen.kt.
@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    // Hilt injects the repository here automatically.
    // The ViewModel only knows about the INTERFACE, not the implementation.
    // This means swapping data sources (real API, hardcoded, test) requires
    // zero changes to this file.
    private val repository: AnalyticsRepository
) : ViewModel() {

    // LinkedHashMap preserves insertion order — important so the dropdown
    // options appear in the correct sequence (1 Day first, 6 Months last).
    val durationOptions: LinkedHashMap<String, Long> = linkedMapOf(
        "1 Day" to 1L,
        "7 Days" to 7L,
        "1 Month" to 30L,
        "3 Months" to 90L,
        "6 Months" to 182L
    )

    // ─────────────────────────────────────────────────────────────────────────
    // STATEFLOW — the heart of MVVM data flow
    //
    // MutableStateFlow is like a "live variable" — whenever its value changes,
    // anyone observing it is automatically notified and the UI redraws.
    //
    // We use TWO variables for a deliberate reason:
    //
    //   _uiState (private, Mutable) — only THIS class can change the value.
    //    uiState (public, read-only) — the UI can only READ, never write.
    //
    // This prevents the UI from accidentally modifying state directly.
    // All state changes must go through the ViewModel's functions.
    // This is "unidirectional data flow" — state flows DOWN to the UI,
    // events flow UP from the UI to the ViewModel.
    // ─────────────────────────────────────────────────────────────────────────
    private val _uiState = MutableStateFlow(
        AnalyticsUiState(currency = SharedPreferencesReader.getCurrency("GBP"))
    )
    // asStateFlow() converts MutableStateFlow to a read-only StateFlow
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    // Called by the UI when the user picks a different time period.
    // This is an "event" flowing UP from the View to the ViewModel.
    fun onDurationSelected(label: String) {
        val days = durationOptions[label] ?: 1L

        // update { } — safely updates the state. It takes the current state
        // (called 'it') and returns a new state using copy() to change only
        // the fields we care about. StateFlow is thread-safe.
        _uiState.update { it.copy(selectedDurationLabel = label, isLoading = true, error = null) }

        fetchData(days)
    }

    private fun fetchData(days: Long) {
        // viewModelScope — a coroutine scope tied to this ViewModel's lifetime.
        // When the ViewModel is destroyed (user leaves screen), all coroutines
        // in this scope are automatically cancelled. No memory leaks.
        viewModelScope.launch {

            // collect the Flow from the repository — this suspends (waits)
            // until the repository emits a value, then continues.
            repository.fetchAnalytics(days)

                // .catch { } — intercepts any unhandled exception thrown inside
                // the Flow. If the repository throws (e.g. network error), this
                // block catches it and puts the error message in the state so
                // the UI can show it.
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }

                // .collect { } — runs this block every time the Flow emits a
                // new value. The Flow from our repository emits exactly once
                // (one successful result or one failure), but this pattern
                // supports multiple emissions too (e.g. real-time updates).
                .collect { result ->

                    // Result.fold() — cleanly handles both success and failure
                    // in one block without a try/catch.
                    result.fold(
                        onSuccess = { model ->
                            // Data arrived — store it in state, hide the spinner
                            _uiState.update { it.copy(isLoading = false, analyticsData = model) }
                        },
                        onFailure = { e ->
                            // Something went wrong — store the error message
                            _uiState.update { it.copy(isLoading = false, error = e.message) }
                        }
                    )
                }
        }
    }
}
