# HealthDietAI - Project Structure Documentation

## Overview
HealthDietAI is an Android application built with Kotlin, using modern architecture patterns including MVVM, Dependency Injection (Hilt), and Coroutines.

## Project Structure

```
HealthDietAI/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/healthdietapp/
│   │   │   │   ├── HealthDietApp.kt              # Hilt Application class
│   │   │   │   ├── MainActivity.kt               # Main Activity with Navigation
│   │   │   │   ├── data/
│   │   │   │   │   ├── api/
│   │   │   │   │   │   ├── ApiService.kt         # Retrofit API interface
│   │   │   │   │   │   └── RetrofitClient.kt     # Retrofit + OkHttp configuration
│   │   │   │   │   ├── model/
│   │   │   │   │   │   ├── AuthModels.kt         # Auth request/response models
│   │   │   │   │   │   ├── RecommendationRequest.kt
│   │   │   │   │   │   ├── RecommendationResponse.kt
│   │   │   │   │   │   └── RecommendationParcelable.kt
│   │   │   │   │   └── repository/
│   │   │   │   │       ├── AuthRepository.kt     # Auth data operations
│   │   │   │   │       └── RecommendationRepository.kt
│   │   │   │   ├── di/
│   │   │   │   │   ├── AppModule.kt              # Hilt application-level DI
│   │   │   │   │   └── RepositoryModule.kt       # Repository DI configuration
│   │   │   │   ├── ui/
│   │   │   │   │   ├── auth/
│   │   │   │   │   │   ├── LoginFragment.kt
│   │   │   │   │   │   └── RegisterFragment.kt
│   │   │   │   │   ├── dashboard/
│   │   │   │   │   │   ├── DashboardFragment.kt
│   │   │   │   │   │   └── FoodAdapter.kt
│   │   │   │   │   ├── profile/
│   │   │   │   │   │   └── ProfileFragment.kt
│   │   │   │   │   └── splash/
│   │   │   │   │       └── SplashFragment.kt
│   │   │   │   ├── viewmodel/
│   │   │   │   │   ├── AuthViewModel.kt          # Auth UI state management
│   │   │   │   │   └── RecommendationViewModel.kt
│   │   │   │   └── utils/
│   │   │   │       ├── Constants.kt              # App-wide constants
│   │   │   │       ├── Extensions.kt             # View extension functions
│   │   │   │       ├── NetworkResult.kt          # Sealed class for API responses
│   │   │   │       └── TokenManager.kt           # JWT token management
│   │   │   └── res/
│   │   │       ├── layout/                       # Fragment & Activity layouts
│   │   │       ├── values/                       # Strings, colors, dimensions
│   │   │       ├── navigation/                   # Navigation graphs
│   │   │       ├── drawable/                     # Vector drawables
│   │   │       └── mipmap-*/                     # App icons
│   │   └── test/                                 # Unit tests
│   │
│   ├── build.gradle.kts                          # App-level Gradle configuration
│   └── proguard-rules.pro                        # ProGuard rules for release builds
│
├── gradle/
│   ├── libs.versions.toml                        # Dependency versions catalog
│   └── wrapper/
│       └── gradle-wrapper.properties
│
├── build.gradle.kts                              # Root Gradle configuration
├── settings.gradle.kts                           # Gradle settings
├── gradle.properties                             # Gradle properties
├── local.properties                              # Local SDK paths
├── gradlew                                       # Gradle wrapper (Unix/Linux)
├── gradlew.bat                                   # Gradle wrapper (Windows)
└── README.md

```

## Technology Stack

### Architecture Pattern
- **MVVM (Model-View-ViewModel)** - Clean separation of concerns
- **Single Activity Architecture** - With Navigation Component
- **Repository Pattern** - Data access abstraction

### Core Dependencies
- **Hilt** - Dependency Injection framework
- **Retrofit** - HTTP client
- **OkHttp** - HTTP client with logging & interceptors
- **Coroutines** - Async/await style asynchronous operations
- **Lifecycle & ViewModel** - Android Architecture Components
- **Navigation** - Fragment-based navigation
- **Data Binding & View Binding** - UI binding

### UI Libraries
- **Material Design Components** - Modern Material Design
- **Shimmer** - Loading placeholders
- **Glide** - Image loading and caching
- **MPAndroidChart** - Data visualization
- **SwipeRefresh** - Pull-to-refresh functionality

## Package Responsibilities

### `data/`
Handles all data operations:
- **api/** - API endpoints and Retrofit configuration
- **model/** - Data classes for API requests/responses
- **repository/** - Business logic for data operations

### `di/` (Dependency Injection)
Hilt modules providing:
- Retrofit instance
- API Service
- Repository instances

### `ui/`
Fragment-based UI screens:
- **auth/** - Login and Registration screens
- **dashboard/** - Main diet recommendations display
- **profile/** - User profile screen
- **splash/** - Splash/loading screen

### `viewmodel/`
ViewModel classes managing UI state using StateFlow for reactive updates.

### `utils/`
Utility classes and extension functions:
- Token management
- Extension functions for Views
- Network result wrapper
- App constants

## Key Design Patterns

### 1. **Repository Pattern**
- Abstracts data sources (API, local DB)
- Single source of truth for data
- Easy to test and maintain

### 2. **Dependency Injection (Hilt)**
- Automatic dependency provision
- Testable code structure
- Loose coupling

### 3. **StateFlow for Reactive UI**
- Automatic UI updates on state changes
- Non-nullable state management
- Lifecycle-aware

### 4. **Sealed Classes for Results**
- Type-safe result handling
- Success, Error, Loading states

## Build Configuration

### Gradle Kotlin DSL (build.gradle.kts)
- Type-safe Gradle scripts
- IDE autocomplete support
- Better readability

### Version Catalog (libs.versions.toml)
- Centralized dependency management
- Easy version updates
- DRY principle

## Build Requirements

- **Gradle**: 8.7 or higher
- **Java**: 1.8 (JDK 8+)
- **Android SDK**: Minimum 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)

## Compilation

To build the project:

```bash
# Clean and build
./gradlew clean build

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run tests
./gradlew test

# Install on device/emulator
./gradlew installDebug
```

## Code Organization Best Practices

✅ **What's Done Right**:
1. Clear separation of concerns (data, ui, viewmodel)
2. Proper use of DI with Hilt
3. Fragment-based navigation
4. Kotlin coroutines for async operations
5. StateFlow for reactive UI state
6. Extension functions for cleaner code
7. Proper error handling with sealed classes
8. Network security configuration

## Files Overview

| File | Purpose |
|------|---------|
| MainActivity.kt | Entry point with navigation setup |
| HealthDietApp.kt | Hilt application initialization |
| RetrofitClient.kt | API client with auth interceptor |
| AuthInterceptor | Adds JWT token to requests |
| ApiService.kt | API endpoints definition |
| AuthRepository.kt | Authentication operations |
| RecommendationRepository.kt | Diet recommendation operations |
| AuthViewModel.kt | Auth screen state management |
| TokenManager.kt | Secure token storage/retrieval |
| NetworkResult.kt | Sealed class for API responses |
| Extensions.kt | View utility extensions |

## Security Features

1. **Token Management** - JWT tokens stored securely in SharedPreferences
2. **Auth Interceptor** - Automatic JWT injection in API requests
3. **Network Security Config** - Restricted cleartext traffic except configured hosts
4. **HTTPS Support** - Recommended for production

## Testing

- Unit tests for ViewModels and Repositories
- Instrumented tests for Fragments
- Test dependencies properly configured in build.gradle.kts

## Next Steps for Deployment

1. Configure `Constants.BASE_URL` with actual backend
2. Set up proper error handling and logging
3. Implement offline caching (Room database)
4. Add SSL certificate pinning
5. Set up analytics and crash reporting
6. Configure ProGuard rules for release builds
7. Add unit and integration tests

---
**Last Updated**: February 27, 2026

