package com.example.analytics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.analytics.ui.screen.analytics.AnalyticsScreen
import com.example.analytics.ui.theme.AnalyticsTheme
import dagger.hilt.android.AndroidEntryPoint

// ─────────────────────────────────────────────────────────────────────────────
// WHAT IS THIS FILE?
//
// MainActivity is the entry point for your app's UI — it is the first screen
// Android opens. There is no XML layout here; the entire UI is described in
// Kotlin using composable functions (see AnalyticsScreen.kt).
// ─────────────────────────────────────────────────────────────────────────────

// @AndroidEntryPoint
// This annotation tells Hilt: "this Activity uses dependency injection".
// It must be added to every Activity (or Fragment) that either:
//   a) directly needs an injected dependency, OR
//   b) hosts a Composable that uses hiltViewModel()
//
// Without this, Hilt cannot inject into this Activity and the app will crash
// with "Hilt components must be attached to a @AndroidEntryPoint component".
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // enableEdgeToEdge() — makes the app draw behind the system bars
        // (status bar at top, navigation bar at bottom) so the UI fills the
        // entire screen edge-to-edge. Compose's Scaffold handles the padding.
        enableEdgeToEdge()

        // setContent { } — the entry point for Compose UI. Composable functions
        // are called directly inside this block to build the screen.
        setContent {

            // AnalyticsTheme wraps the whole app in Material3 theming.
            // It provides colors, typography and shapes to all composables
            // inside it. Defined in ui/theme/Theme.kt.
            AnalyticsTheme {

                // This is our one and only screen. Compose builds the full UI
                // from this single composable function call.
                AnalyticsScreen()
            }
        }
    }
}
