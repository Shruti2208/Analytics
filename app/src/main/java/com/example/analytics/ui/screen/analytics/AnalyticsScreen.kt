package com.example.analytics.ui.screen.analytics

// ─────────────────────────────────────────────────────────────────────────────
// WHAT IS JETPACK COMPOSE?
//
// Jetpack Compose is Android's UI toolkit. You describe the UI directly in
// Kotlin using functions annotated with @Composable.
//
// KEY CONCEPT — "Declarative UI":
// You describe WHAT the screen should look like given the current state, and
// Compose figures out HOW to draw it. When state changes, Compose automatically
// re-runs the affected composable functions and redraws only what changed.
// This is called "recomposition".
// ─────────────────────────────────────────────────────────────────────────────

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.analytics.data.model.AnalyticsModel
import com.example.analytics.ui.screen.analytics.formatters.AvgBarChartFormatter
import com.example.analytics.ui.screen.analytics.formatters.BarChartFormatter
import com.example.analytics.util.Utils
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart

// @OptIn(ExperimentalMaterial3Api::class) — some Material3 APIs are still
// marked experimental (may change in future versions). This annotation says
// "I acknowledge the API might change, let me use it anyway". Without it,
// the compiler would show a warning/error for TopAppBar and dropdown usage.
@OptIn(ExperimentalMaterial3Api::class)

// @Composable — marks this as a composable function. Only composable functions
// can call other composable functions. The @Composable annotation tells the
// Compose compiler to treat this function specially (track state, recompose, etc.)
@Composable
fun AnalyticsScreen(
    // hiltViewModel() — asks Hilt for an instance of AnalyticsViewModel.
    // Hilt creates it, injects the repository, and ties its lifecycle to this
    // composable's host (the Activity). The ViewModel survives recompositions
    // and screen rotations. This is the default so tests can pass a fake ViewModel.
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    // collectAsStateWithLifecycle() — "subscribes" to the ViewModel's StateFlow.
    // Whenever _uiState changes in the ViewModel, this line automatically gets
    // the new value and triggers recomposition of any composable that uses uiState.
    // The "withLifecycle" part means it stops collecting when the app is in the
    // background (saving battery and preventing unnecessary work).
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // LaunchedEffect(Unit) — runs a coroutine ONCE when this composable first
    // enters the screen. Unit as the key means "run once and never again".
    // This triggers the initial data load.
    LaunchedEffect(Unit) {
        viewModel.onDurationSelected(uiState.selectedDurationLabel)
    }

    // Scaffold — the standard Material3 screen container. It handles:
    //   - topBar: the toolbar at the top of the screen
    //   - content: the main scrollable area
    // It also automatically applies the correct padding so content doesn't
    // hide behind the top bar or system navigation bar.
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Analytics") })
        }
    ) { paddingValues ->
        // paddingValues — the insets Scaffold gives us (top bar height, etc.)
        // We apply them to the Column so content starts below the top bar.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // The duration picker dropdown
            DurationDropdown(
                selected = uiState.selectedDurationLabel,
                options = viewModel.durationOptions.keys.toList(),
                onSelected = viewModel::onDurationSelected // passes the function reference
            )

            // CONDITIONAL UI BASED ON STATE
            // This is the core of declarative UI — we describe what to show
            // for each possible state. Compose redraws this whenever uiState changes.
            when {
                // STATE 1: Loading — show a spinner in the centre of the screen
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                // STATE 2: Error — show the error message
                uiState.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Error: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                // STATE 3: Data loaded — show the charts and tables
                uiState.analyticsData != null -> {
                    AnalyticsContent(
                        data = uiState.analyticsData!!,
                        currency = uiState.currency
                    )
                }

                // (Implicit STATE 4: Initial — nothing shown yet, LaunchedEffect
                // triggers loading immediately so this is barely visible)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PRIVATE COMPOSABLES
// Breaking the screen into smaller composable functions is good practice —
// each function does one thing and can be understood (and tested) independently.
// The 'private' keyword means these are internal helpers, not reusable elsewhere.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AnalyticsContent(data: AnalyticsModel, currency: String) {
    // remember { } — stores the formatter across recompositions. Without
    // remember, getCurrencyStringAmountFormatter() would create a new lambda
    // object every single time this composable redraws, wasting memory.
    val currencyFormatter = remember { Utils.getCurrencyStringAmountFormatter() }

    // LazyColumn — renders only items that are currently visible on screen,
    // making it memory-efficient for long lists. Each item { } block is one
    // row/section in the list.
    LazyColumn(modifier = Modifier.fillMaxSize()) {

        // ── Pie chart: Transaction volume by APM ──────────────────────────
        item {
            SectionHeader("Transaction Volume by APM")

            // AndroidView { } — a bridge between Compose and traditional
            // Android Views. MPAndroidChart (PieChart, BarChart) is a classic
            // View library and doesn't have Compose support, so we wrap it.
            //
            // factory  — called ONCE to create the View
            // update   — called every time the composable recomposes (state changes).
            //            This is where we apply new data to the chart.
            AndroidView(
                factory = { context -> PieChart(context) },
                update = { chart ->
                    PieChartUtils.displayTransactionVolumesPerApmPieChart(chart, data, chart.context)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }

        // ── Table: Transaction totals ─────────────────────────────────────
        item {
            SectionHeader("Transaction Totals")
            val rows = data.dataSumAmount?.map { entry ->
                AnalyticsTableRow(
                    title = entry.method,
                    value = "$currency ${currencyFormatter(entry.value.toString())}"
                )
            } ?: emptyList()
            AnalyticsTable(rows)
        }

        // ── Bar chart: Number of transactions ─────────────────────────────
        item {
            SectionHeader("Number of Transactions")
            AndroidView(
                factory = { context -> BarChart(context) },
                update = { chart ->
                    BarGraphUtils.displayBarGraph(
                        data = data.dataNumberOfTransactions,
                        barChart = chart,
                        context = chart.context,
                        formatter = BarChartFormatter(),
                        isSimpleBar = true
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
        }

        // ── Table: Number of transactions ─────────────────────────────────
        item {
            val rows = data.dataNumberOfTransactions?.map { entry ->
                AnalyticsTableRow(title = entry.method, value = entry.value.toString())
            } ?: emptyList()
            AnalyticsTable(rows)
        }

        // ── Bar chart: Average transaction value ──────────────────────────
        item {
            SectionHeader("Average Transaction Value")
            AndroidView(
                factory = { context -> BarChart(context) },
                update = { chart ->
                    BarGraphUtils.displayBarGraph(
                        data = data.dataAverageTransactionValue,
                        barChart = chart,
                        context = chart.context,
                        formatter = AvgBarChartFormatter(),
                        isSimpleBar = false
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
        }

        // ── Table: Average transaction value ──────────────────────────────
        item {
            val rows = data.dataAverageTransactionValue?.map { entry ->
                AnalyticsTableRow(
                    title = entry.method,
                    value = "$currency ${currencyFormatter(entry.value.toString())}"
                )
            } ?: emptyList()
            AnalyticsTable(rows)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Displays a bold section title above each chart/table group
@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

// Renders a table by looping through rows and calling a composable for each.
@Composable
private fun AnalyticsTable(rows: List<AnalyticsTableRow>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // weight(1f) — makes this Text take up all remaining space,
                // pushing the value Text to the right edge
                Text(
                    text = row.title ?: "",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = row.value,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

// Duration picker dropdown. ExposedDropdownMenuBox is Material3's dropdown
// component. It manages its own open/closed state internally.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DurationDropdown(
    selected: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    // remember { mutableStateOf(false) } — local UI state for whether the
    // dropdown is open or closed. 'remember' keeps this value across
    // recompositions (without it, expanded would reset to false every redraw).
    // 'by' is Kotlin delegation — lets us write 'expanded' instead of
    // 'expanded.value' everywhere.
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }, // toggle open/closed on tap
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // The text field that shows the currently selected option.
        // readOnly = true means the user can tap it to open the dropdown
        // but cannot type in it.
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Time Period") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                // menuAnchor — tells the dropdown menu where to anchor itself
                // (i.e. open directly below this text field)
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )

        // The actual dropdown list that appears when expanded = true
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false } // close when user taps outside
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option) // tell the ViewModel the selection changed
                        expanded = false   // close the dropdown
                    }
                )
            }
        }
    }
}
