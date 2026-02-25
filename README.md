# Analytics

An Android application that displays payment analytics data through interactive charts and summary tables. Built as a reference/learning project demonstrating modern Android development patterns.

## Features

- **Transaction Volume by APM** — Pie chart showing distribution across payment methods (Visa, Mastercard, PayPal, Apple Pay, Google Pay)
- **Transaction Totals** — Bar chart and table of total transaction amounts per payment method
- **Number of Transactions** — Bar chart and table of transaction counts
- **Average Transaction Value** — Bar chart and table of average values per payment method
- **Time Period Filter** — Dropdown to filter data by: 1 Day, 7 Days, 1 Month, 3 Months, 6 Months
- **Currency Support** — Configurable currency symbol via SharedPreferences

## Tech Stack

| Layer | Technology |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Charts | MPAndroidChart (via JitPack) |
| Architecture | MVVM (ViewModel + StateFlow) |
| Dependency Injection | Hilt |
| Async | Kotlin Coroutines + Flow |
| JSON Parsing | Gson |
| Networking | WebSocket (neovisionaries/ws) |

## Architecture

The project follows MVVM with a clean separation of concerns:

```
UI (AnalyticsScreen)
    ↕  state / events
ViewModel (AnalyticsViewModel)
    ↕  Flow<Result<AnalyticsModel>>
Repository Interface (AnalyticsRepository)
    ↓
RepositoryImpl (AnalyticsRepositoryImpl)
    ↓
Data Source (WebSocket / hardcoded stub)
```

- **AnalyticsScreen** — Composable UI, renders state, dispatches user events
- **AnalyticsViewModel** — Holds `AnalyticsUiState` in a `StateFlow`, handles duration selection, calls the repository
- **AnalyticsRepository** — Interface; decouples the ViewModel from any specific data source
- **AnalyticsRepositoryImpl** — Concrete implementation; currently returns hardcoded stub data
- **WebSocketClient** — Stub for a real-time WebSocket connection (to be implemented)
- **AnalyticsModule** — Hilt DI module binding the repository interface to its implementation

## Requirements

- Android Studio Meerkat or later
- Android SDK 36 (compile), minSdk 24
- Kotlin + KSP
- Java 11

## Getting Started

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle (`File → Sync Project with Gradle Files`)
4. Run on an emulator or physical device (API 24+)

## Replacing the Stub Data

The app currently uses hardcoded data. To connect a real backend:

1. **WebSocket** — implement `WebSocketClient.connectToServer()`, `sendToServer()`, and `closeWebsocket()` in `data/remote/WebSocketClient.kt`
2. **Repository** — replace `buildHardcodedModel()` in `AnalyticsRepositoryImpl` with a real WebSocket/API call
3. **JSON mapping** — update `@SerializedName` annotations in `AnalyticsModel` to match your server's response keys
4. **SharedPreferences** — implement `SharedPreferencesReader.getCurrency()` and `getAuthId()` to read from actual device storage

## Project Structure

```
app/src/main/java/com/example/analytics/
├── data/
│   ├── model/          # AnalyticsModel, AnalyticsDataEntry, request/response models
│   ├── remote/         # WebSocketClient, ClientSocketCallbacks
│   └── repository/     # AnalyticsRepository (interface) + AnalyticsRepositoryImpl
├── di/
│   └── AnalyticsModule.kt   # Hilt DI bindings
├── properties/
│   ├── Constants.kt
│   └── SharedPreferencesReader.kt
├── ui/
│   ├── screen/analytics/
│   │   ├── AnalyticsScreen.kt
│   │   ├── AnalyticsViewModel.kt
│   │   ├── AnalyticsUiState.kt
│   │   ├── AnalyticsTableRow.kt
│   │   ├── BarGraphUtils.kt
│   │   ├── PieChartUtils.kt
│   │   └── formatters/      # BarChartFormatter, AvgBarChartFormatter, PercentFormatter
│   └── theme/               # Color, Type, Theme
├── util/
│   └── Utils.kt
├── AnalyticsApplication.kt
└── MainActivity.kt
```
<img width="1080" height="2400" alt="Screenshot_20260225_144826" src="https://github.com/user-attachments/assets/20402456-9d02-4a8f-bef2-8c9c01755092" />
<img width="1080" height="2400" alt="Screenshot_20260225_145007" src="https://github.com/user-attachments/assets/2feca019-2dac-4389-a581-9bc7c7576a0c" />

