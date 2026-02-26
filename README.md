# HealthDietAI - Android App 🥗

A complete Android health & diet recommendation app built with **Kotlin**, **MVVM** + **Clean Architecture**, featuring AI-powered personalized diet recommendations.

## 🚀 Quick Start

### Prerequisites
- Java JDK 8 or higher
- Android Studio 2023.1+
- Android SDK API 34
- Emulator or physical device (Android 8.0+)

### 1. Clone & Setup
```bash
# Navigate to project
cd D:\CLg\HealthDietAI

# Build the project
./gradlew clean build
```

### 2. Configure Backend API
Edit `app/src/main/java/com/healthdietapp/utils/Constants.kt`:
```kotlin
const val BASE_URL = "http://10.0.2.2:8000/"  // For emulator
// Or for physical device:
// const val BASE_URL = "http://192.168.x.x:8000/"
```

### 3. Run on Emulator
```bash
# Install and run on connected device/emulator
./gradlew installDebug

# Or use Android Studio: Run → Run 'app' (Shift+F10)
```

### 4. View Logs
```bash
adb logcat | grep healthdietapp
```

---

## 🏗️ Architecture

This project follows **MVVM + Clean Architecture** principles:

```
┌─────────────────┐
│      UI Layer   │  (Fragments, Activities)
├─────────────────┤
│   ViewModel     │  (State Management, Business Logic)
├─────────────────┤
│   Repository    │  (Data Abstraction)
├─────────────────┤
│   Data Layer    │  (API, Local Storage)
└─────────────────┘
```

### Key Technologies
- **Kotlin** - Modern Android development
- **MVVM** - Separation of concerns
- **Hilt** - Dependency Injection
- **Retrofit** - HTTP client with logging
- **OkHttp** - Network interception
- **Coroutines** - Async operations
- **StateFlow** - Reactive state management
- **Navigation Component** - Fragment-based navigation
- **Material Design 3** - Modern UI framework

## 📱 App Screens

| Screen | Purpose | Features |
|--------|---------|----------|
| **Splash** | Initial loading | Auto-navigates based on login state |
| **Login** | User authentication | Email/password with JWT token |
| **Register** | New user registration | Form validation, error handling |
| **Profile** | Health data collection | Age, weight, height, gender, lifestyle data |
| **Dashboard** | Results display | BMI, obesity level, food recommendations, graphs |

### User Flow
```
Splash → Login/Register → Profile (Input Health Data) → Dashboard (View Recommendations)
```

## 🔌 API Integration

### Authentication Flow
1. User registers with email, password, name, username
2. Backend returns user data and optional token
3. User logs in with email/password
4. Backend returns JWT token (`access_token`)
5. Token stored locally via `TokenManager`
6. Subsequent requests include: `Authorization: Bearer {token}`

### API Endpoints

| Method | Endpoint | Purpose | Auth Required |
|--------|----------|---------|----------------|
| POST | `/auth/register` | User registration | No |
| POST | `/auth/login` | User authentication | No |
| POST | `/recommendations/ml` | Get diet recommendation | Yes |

### Request/Response Models

**Login Request:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Recommendation Request:**
```json
{
  "age": 25,
  "height": 175.5,
  "weight": 70.0,
  "gender": "Male",
  "family_history_with_overweight": "yes",
  "favc": "yes",
  "caec": "between_meals",
  "smoke": "no",
  "scc": "yes",
  "calc": "sometimes",
  "mtrans": "public_transportation",
  "physical_activity": 3.0,
  "water_intake": 2.5,
  "ncp": 3.0,
  "tue": 2.0
}
```

**Recommendation Response:**
```json
{
  "bmi": 22.86,
  "bmi_category": "Normal weight",
  "predicted_obesity_level": "Normal_Weight",
  "confidence_score": 0.95,
  "class_probabilities": { "Normal_Weight": 0.95, ... },
  "recommended_foods": [
    {
      "food_name": "Grilled Chicken",
      "calories": 165,
      "protein": 31,
      "carbs": 0,
      "fat": 3.6,
      "sugar": null,
      "sodium": null
    }
  ],
  "dietary_notes": "Maintain balanced diet...",
  "bmr": 1700,
  "daily_calorie_target": 2000,
  "total_foods_filtered": 45
}
```

## 📁 Project Structure

```
app/src/main/java/com/healthdietapp/
├── HealthDietApp.kt              # Hilt Application initialization
├── MainActivity.kt               # Main Activity with Navigation
├── data/
│   ├── api/
│   │   ├── ApiService.kt         # Retrofit API endpoints
│   │   └── RetrofitClient.kt     # Retrofit + OkHttp setup
│   ├── model/
│   │   ├── AuthModels.kt         # Login/Register models
│   │   └── RecommendationResponse.kt
│   └── repository/
│       ├── AuthRepository.kt     # Auth operations
│       └── RecommendationRepository.kt
├── di/
│   ├── AppModule.kt              # Retrofit/API DI
│   └── RepositoryModule.kt       # Repository DI
├── ui/
│   ├── auth/                     # Login/Register screens
│   ├── dashboard/                # Results display
│   ├── profile/                  # Health data input
│   └── splash/                   # Loading screen
├── viewmodel/
│   ├── AuthViewModel.kt          # Auth state
│   └── RecommendationViewModel.kt
└── utils/
    ├── Constants.kt              # Configuration
    ├── Extensions.kt             # View utilities
    ├── NetworkResult.kt          # Result wrapper
    └── TokenManager.kt           # JWT management
```

### Key Classes

| Class | Package | Purpose |
|-------|---------|---------|
| `Constants.kt` | `utils` | Base URL, configuration, timeouts |
| `TokenManager.kt` | `utils` | JWT token storage and retrieval |
| `ApiService.kt` | `data.api` | Retrofit interface with endpoints |
| `RetrofitClient.kt` | `data.api` | Retrofit client with interceptors |
| `AuthViewModel.kt` | `viewmodel` | Authentication state management |
| `RecommendationViewModel.kt` | `viewmodel` | Recommendation state management |
| `AuthRepository.kt` | `data.repository` | Auth API calls with error handling |
| `RecommendationRepository.kt` | `data.repository` | Recommendation API calls |
| `LoginFragment.kt` | `ui.auth` | Login UI implementation |
| `DashboardFragment.kt` | `ui.dashboard` | Results display with charts |

