# News App 📰

A modern news application built with the latest Android ecosystem technologies. The project focuses on scalability, testability, and separation of concerns through a modularized architecture using Clean Architecture and Unidirectional Data Flow.

---

## 🚀 Tech Stack

| Category | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | Clean Architecture + MVI (UDF) |
| DI | Hilt |
| Navigation | Navigation Compose (type-safe) |
| Networking | Retrofit + OkHttp + Moshi (KSP) |
| Async | Coroutines + Flow |
| Images | Coil |
| Auth | AndroidX Biometric |
| Testing | JUnit + MockK + Turbine |

---

## 🏗️ Architecture & Modularization

The project follows **Clean Architecture** with **Unidirectional Data Flow (UDF)**, split into independent modules:

```
:app
├── :feature
│   ├── :feature:headlines   # News list and detail screens
│   └── :feature:splash      # Launch screen with biometric auth
├── :domain                  # Pure business rules (use cases, models, repository interfaces)
├── :data                    # Repository implementations, DTOs, mappers
└── :core
    ├── :core:ui             # Reusable components, theme, MVI base, BiometricHelper
    ├── :core:network        # Retrofit setup, API service, DTOs
    ├── :core:navigation     # Type-safe routes (Kotlin Serialization)
    └── :core:common         # AppResult, AppDispatchers, extensions
```

### MVI Contract

Each screen defines a contract inheriting from `UnidirectionalViewModel<STATE, EVENT, EFFECT>`:

- **State** — what the UI renders (immutable data class)
- **Event** — user actions dispatched to the ViewModel
- **Effect** — one-shot side effects (navigation, dialogs)

```kotlin
// Example: Headlines
data class State(
    val title: String,
    val searchQuery: String = "",
    val headlines: List<Headline> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
)

sealed class Event {
    data object OnStart : Event()
    data class OnSearchQueryChange(val query: String) : Event()
    data class OnCardClick(val article: Headline) : Event()
}

sealed class Effect {
    data class NavigateTo(val route: Route) : Effect()
}
```

The `use()` composable collects state, exposes a dispatch function, and returns the effect flow — all lifecycle-aware via `collectAsStateWithLifecycle()`.

### Data Flow

```
UI (Compose)  ──Event──►  ViewModel  ──►  UseCase  ──►  Repository  ──►  API
     ▲                        │
     └────── State/Effect ─────┘
```

---

## 📱 Features

### Splash + Biometric Authentication

The splash screen checks device biometric capability on startup using `BiometricManager`. If the device supports biometrics, a `BiometricPrompt` is displayed before granting access:

- ✅ **Authentication success** → navigates to the headlines list
- ❌ **Authentication error** → closes the app
- ⚠️ **Biometric unavailable** → navigates directly to headlines

The biometric logic is encapsulated in `showBiometricPrompt()` inside `:core:ui`, making it reusable across any feature:

```kotlin
// core/ui/utils/BiometricHelper.kt
fun showBiometricPrompt(
    activity: FragmentActivity,
    context: Context,
    onSuccess: () -> Unit,
    onError: () -> Unit
)
```

### Headlines List

- Fetches top headlines from [NewsAPI](https://newsapi.org/) filtered by product flavor source
- Real-time search with **500ms debounce** filtering articles by title (case-insensitive)
- **Image preloading** via Coil on list load for faster detail screen transitions
- Loading, error, and empty states handled with dedicated reusable components

### Headline Detail

- Receives article data through type-safe navigation route parameters
- **Shared Element Transition** on the article image between list and detail
- Scrollable content: image, title, author, publication date, description, and full content

---

## 🧭 Navigation

Navigation uses **Navigation Compose** with type-safe routes via **Kotlin Serialization**, eliminating magic strings:

```kotlin
@Serializable object SplashRoute
@Serializable object HeadlineRoute {
    @Serializable object ListRoute
    @Serializable data class DetailsRoute(
        val author: String,
        val title: String,
        val description: String,
        val url: String,
        val urlToImage: String,
        val publishedAt: String,
        val content: String
    )
}
```

### Shared Element Transitions

A `SharedTransitionLayout` wraps the entire `NavHost`. The article image is keyed with `"headline_image_${article.url}"` so Compose animates it continuously between the list card and the detail screen.

```
SplashRoute → HeadlineRoute.ListRoute → HeadlineRoute.DetailsRoute(...)
```

---

## 🌐 Network Layer

### Authentication

Every request is authenticated via an `AuthInterceptor` that appends the `X-Api-Key` header from `BuildConfig` (set in `local.properties`):

```
GET /v2/top-headlines?sources={flavor_source}
```

### Logging

`HttpLoggingInterceptor` is configured at `BODY` level in debug builds and `NONE` in release builds.

### Timeouts

OkHttpClient is configured with 30-second connect, read, and write timeouts.

### Error Handling

All repository calls are wrapped in `safeApiCall`, which maps exceptions to user-friendly `AppResult.Error` messages:

| Scenario | Mapped message |
|---|---|
| HTTP 401 | Check API key |
| HTTP 404 | Not found |
| HTTP 429 | Rate limited |
| HTTP 5xx | Server error |
| `UnknownHostException` | No internet connection |
| `SocketTimeoutException` | Connection timeout |

```kotlin
sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Error(val message: String, val code: Int?, val throwable: Throwable?) : AppResult<Nothing>
}
```

---

## 🖼️ Image Loading

Coil is configured globally via a custom `ImageLoader` provided by Hilt:

- **Memory cache**: 25% of available memory
- **Disk cache**: 50 MB at `cacheDir/image_cache`
- **Crossfade** animation enabled
- Cache headers disabled (respects NewsAPI's CDN)
- Images are **preloaded** by `HeadlinesViewModel` on list fetch to make detail transitions instant

---

## 🧪 Testing

The project is structured for full unit test coverage with no Android framework dependencies in lower layers.

### Tools

- **JUnit 4** — test runner
- **MockK** — mocking library
- **Turbine** — Flow and SharedFlow testing
- **TestAppDispatchers** — replaces `AppDispatchers` with `UnconfinedTestDispatcher` for synchronous coroutine execution

### Coverage

| Layer | What is tested |
|---|---|
| Domain | `GetHeadlines` use case success and failure |
| Data | `HeadlineRepositoryImpl` — all HTTP errors, network errors, mapping |
| Feature | `HeadlinesViewModel` — loading, search debounce, filtering, navigation effect |

---

## 🏭 Product Flavors

The app supports multiple news sources via Android Product Flavors:

| Flavor | App ID | News Source |
|---|---|---|
| `bbc` | `com.jvrni.news.bbc` | `bbc-news` |
| `cnn` | `com.jvrni.news.cnn` | `cnn` |

The active source is injected at build time via `BuildConfig.NEWS_SOURCE` and provided to the repository through Hilt's `@Named("NewsSource")` qualifier.

To run, select the desired variant in the **Build Variants** panel (`bbcDebug`, `cnnDebug`, etc.).

---

## 🛠️ Setup

### 1. API Key

Create a `local.properties` file at the project root:

```properties
API_KEY=your_api_key_here
BASE_URL=https://newsapi.org/
```

Get a free API key at [newsapi.org](https://newsapi.org/).

### 2. Run

Select a build variant (`bbcDebug` or `cnnDebug`) and run on a device or emulator (min SDK 24).

> Biometric authentication requires a device with a registered fingerprint. On emulators, configure it via **Extended Controls → Fingerprint**.

---

## 📄 License

This project is for learning and personal portfolio purposes.
