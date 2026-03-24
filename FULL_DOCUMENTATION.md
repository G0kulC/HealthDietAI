# AI-Based Personalized Health & Diet Recommendation System
## Full End-to-End Documentation — Final Year Project

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [System Architecture](#2-system-architecture)
3. [Technology Stack](#3-technology-stack)
4. [Backend — FastAPI Server](#4-backend--fastapi-server)
   - 4.1 [Project Structure](#41-project-structure)
   - 4.2 [Configuration & Environment](#42-configuration--environment)
   - 4.3 [Database Layer](#43-database-layer)
   - 4.4 [Authentication System](#44-authentication-system)
   - 4.5 [ML Model — Training](#45-ml-model--training)
   - 4.6 [ML Service — Prediction](#46-ml-service--prediction)
   - 4.7 [Nutrition Service](#47-nutrition-service)
   - 4.8 [API Endpoints Reference](#48-api-endpoints-reference)
5. [Android Frontend — HealthDietAI](#5-android-frontend--healthdietai)
   - 5.1 [Project Structure](#51-project-structure)
   - 5.2 [Architecture — MVVM](#52-architecture--mvvm)
   - 5.3 [Dependency Injection — Hilt](#53-dependency-injection--hilt)
   - 5.4 [Network Layer](#54-network-layer)
   - 5.5 [Data Models](#55-data-models)
   - 5.6 [ViewModels](#56-viewmodels)
   - 5.7 [UI Screens & Navigation](#57-ui-screens--navigation)
6. [End-to-End Request Flow](#6-end-to-end-request-flow)
7. [Database Schema](#7-database-schema)
8. [Datasets](#8-datasets)
9. [Setup & Running the Project](#9-setup--running-the-project)
10. [API Quick Reference](#10-api-quick-reference)

---

## 1. Project Overview

**Project Title:** AI-Based Personalized Health & Diet Recommendation System

**Objective:** Provide users with personalised dietary recommendations based on their health metrics (age, height, weight, lifestyle habits) using a trained Machine Learning model (RandomForestClassifier) and a real nutrition dataset.

**Problem Statement:**
- Obesity and poor diet are leading causes of chronic disease globally.
- Generic diet plans do not account for individual health profiles.
- This system bridges that gap by combining ML-based obesity classification with nutrition science to deliver personalised food recommendations.

**Solution:**
1. The user fills out a health form (age, height, weight, gender, lifestyle factors).
2. A RandomForest ML model predicts their obesity level (7 classes).
3. The system filters a 600+ item nutrition dataset using obesity-level-specific dietary profiles.
4. The user receives a ranked list of 10 recommended foods with full macro details (calories, protein, carbs, fat, sugar, sodium), BMR calculation, and a daily calorie target.
5. All results are saved to the user's account history for tracking.

---

## 2. System Architecture

```
+-----------------------------------------------------+
|              Android Application (Kotlin)           |
|                                                     |
|  [Splash] -> [Login/Register] -> [Profile Form]     |
|          -> [Results Dashboard] -> [History]        |
|                                                     |
|  MVVM Architecture (ViewModel + StateFlow)          |
|  Retrofit + OkHttp (HTTP Client)                   |
|  Hilt (Dependency Injection)                        |
+---------------------+-------------------------------+
                       | HTTPS / REST (JSON)
                       |  Bearer JWT Token
                       v
+-----------------------------------------------------+
|              FastAPI Backend (Python)               |
|                                                     |
|  /auth  ->  JWT Authentication                      |
|  /users  ->  User Profile                           |
|  /recommendations/ml  ->  ML Prediction Pipeline   |
|  /recommendations/history  ->  Saved Results       |
|                                                     |
|  Middleware: CORS, Global Exception Handler         |
+----------+--------------------+--------------------+
           |                    |
           v                    v
+--------------------+  +-------------------------+
| PostgreSQL DB      |  | ML Artifacts            |
| - users table      |  | - obesity_model.pkl     |
| - recommendation_  |  |   (RandomForest)        |
|   history table    |  | - encoders.pkl          |
+--------------------+  |   (LabelEncoders)       |
                        | - accuracy.txt          |
                        +-------------------------+
                                  |
                                  v
                        +-------------------------+
                        | Nutrition Dataset       |
                        | datasets/nutrition.csv  |
                        | (600+ food items)       |
                        +-------------------------+
```

**Key Architectural Decisions:**

| Decision | Choice | Reason |
|---|---|---|
| Backend Framework | FastAPI | Async, auto Swagger docs, Pydantic validation |
| ML Algorithm | RandomForestClassifier | High accuracy, handles mixed data types |
| Android Architecture | MVVM + StateFlow | Lifecycle-aware, reactive, testable |
| Authentication | JWT (Bearer Token) | Stateless, works perfectly for mobile APIs |
| Database ORM | SQLAlchemy | Mature, type-safe, works with FastAPI |
| DI Framework | Dagger Hilt | Google-recommended, reduces boilerplate |
| HTTP Client | Retrofit + OkHttp | Industry standard for Android REST APIs |

---

## 3. Technology Stack

### Backend

| Component | Technology | Version |
|---|---|---|
| Web Framework | FastAPI | 0.111.0 |
| ASGI Server | Uvicorn | 0.29.0 |
| ORM | SQLAlchemy | 2.0.30 |
| Database | PostgreSQL | 15+ |
| DB Driver | psycopg2-binary | 2.9.9 |
| Authentication | PyJWT + bcrypt | 2.8.0 / 4.1.3 |
| ML Framework | scikit-learn | 1.5.0 |
| Data Processing | pandas + numpy | 2.2.2 / 1.26.4 |
| Model Serialization | joblib | 1.4.2 |
| Validation | Pydantic v2 | 2.7.1 |
| Settings | pydantic-settings | 2.2.1 |

### Android Frontend

| Component | Technology | Version |
|---|---|---|
| Language | Kotlin | 1.9.24 |
| Min SDK | Android 7.0 (API 24) | — |
| Target SDK | Android 14 (API 34) | — |
| Build System | Gradle (KTS) | 8.7 |
| Android Gradle Plugin | AGP | 8.5.0 |
| DI Framework | Dagger Hilt | 2.51.1 |
| HTTP Client | Retrofit + OkHttp | 2.11.0 / 4.12.0 |
| JSON Parsing | Gson | (via Retrofit) |
| Coroutines | kotlinx-coroutines | 1.8.0 |
| Lifecycle | ViewModel + StateFlow | 2.8.4 |
| Navigation | Navigation Component | 2.7.7 |
| UI | Material Design 3 | 1.12.0 |
| Charts | MPAndroidChart | v3.1.0 |
| Shimmer Loading | Facebook Shimmer | 0.5.0 |

---

## 4. Backend — FastAPI Server

### 4.1 Project Structure

```
system-backend/
+-- app/
|   +-- __init__.py
|   +-- main.py                  # App factory, lifespan, routers registration
|   +-- database.py              # SQLAlchemy engine, session, Base
|   +-- core/
|   |   +-- config.py            # Settings (reads .env via pydantic-settings)
|   |   +-- security.py          # JWT creation/decode, bcrypt hashing
|   |   +-- dependencies.py      # FastAPI dependency functions (get_db, get_current_user)
|   +-- models/
|   |   +-- user.py              # ORM model: users table
|   |   +-- recommendation_history.py   # ORM model: recommendation_history table
|   +-- schemas/
|   |   +-- user.py              # Pydantic: UserCreate, LoginRequest, TokenResponse, UserResponse
|   |   +-- recommendation.py    # Pydantic: RecommendationRequest, RecommendationResponse, HistoryItem
|   +-- routers/
|   |   +-- auth.py              # POST /auth/register, /auth/login
|   |   +-- users.py             # GET /users/me
|   |   +-- recommendations.py   # POST /recommendations/ml, GET /recommendations/history
|   +-- services/
|       +-- ml_service.py        # Feature engineering, prediction, BMI classification, dietary profiles
|       +-- nutrition_service.py # CSV column normalisation, data cleaning, food ranking
+-- datasets/
|   +-- obesity.csv              # Kaggle dataset (training data, ~2111 rows)
|   +-- nutrition.csv            # Kaggle dataset (food items, 600+ rows)
+-- ml_model/
|   +-- obesity_model.pkl        # Trained RandomForestClassifier (serialized)
|   +-- encoders.pkl             # LabelEncoders + feature column list
|   +-- accuracy.txt             # Model accuracy on test set
+-- train_model.py               # One-time training script
+-- requirements.txt
+-- .env                         # Environment variables (not committed)
+-- .env.example                 # Template for .env
```

---

### 4.2 Configuration & Environment

**File:** `app/core/config.py`

The application is configured via environment variables loaded from a `.env` file using `pydantic-settings`.

```
# .env file
APP_NAME=AI-Based Personalized Health & Diet Recommendation System
DEBUG=False
DATABASE_URL=postgresql://postgres:password@localhost:5432/health_diet_db
SECRET_KEY=your-secret-key-here-generate-with-secrets.token_hex(32)
ALGORITHM=HS256
ACCESS_TOKEN_EXPIRE_MINUTES=1440
```

| Variable | Default | Description |
|---|---|---|
| `APP_NAME` | AI-Based... System | Application display name |
| `DEBUG` | False | Enable debug mode |
| `DATABASE_URL` | postgresql://... | PostgreSQL connection string |
| `SECRET_KEY` | changeme | JWT signing secret (must be changed in production) |
| `ALGORITHM` | HS256 | JWT signing algorithm |
| `ACCESS_TOKEN_EXPIRE_MINUTES` | 1440 (24h) | JWT token lifetime |

**App Startup (Lifespan):**

On startup (`app/main.py`), the FastAPI lifespan function:
1. Creates database tables (`Base.metadata.create_all`)
2. Loads `ml_model/obesity_model.pkl` into `app.state.obesity_model`
3. Loads `ml_model/encoders.pkl` into `app.state.encoders_bundle`
4. Loads `datasets/nutrition.csv` into `app.state.nutrition_df` (as a cleaned pandas DataFrame)

---

### 4.3 Database Layer

**File:** `app/database.py`

```python
engine = create_engine(
    settings.DATABASE_URL,
    pool_pre_ping=True,   # Reconnect on stale connections
    pool_size=10,
    max_overflow=20,
)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()
```

**Session Management:**

Every API request gets its own database session via the `get_db()` dependency injected by FastAPI. The session is closed automatically after the request completes (using `try/finally`).

```python
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
```

---

### 4.4 Authentication System

**File:** `app/core/security.py`

The system uses stateless JWT (JSON Web Token) authentication.

#### Password Hashing
- Uses `bcrypt` directly (not passlib) for hashing and verification.
- Passwords are never stored in plain text.

```
User Password (plain)  -->  bcrypt.hashpw()  -->  Stored hashed_password
Login attempt          -->  bcrypt.checkpw()  -->  Compare with stored hash
```

#### JWT Token Flow

```
POST /auth/register  -->  Create User account
POST /auth/login     -->  Verify password  -->  Create JWT token (sub=user_id, exp=24h)
                     -->  Return { access_token, token_type: "bearer" }

Protected Endpoint   -->  Request with "Authorization: Bearer <token>"
                     -->  decode_token()  -->  Extract user_id from "sub" claim
                     -->  Query User from DB  -->  Return user object
```

**Token Payload:**
```json
{
  "sub": "42",
  "exp": 1745000000
}
```

**Dependency Injection:**

`get_current_user()` in `app/core/dependencies.py` is a FastAPI dependency. Any endpoint that declares it as a parameter automatically becomes protected. It:
1. Extracts the Bearer token from the `Authorization` header.
2. Decodes and verifies the JWT.
3. Loads the user from the database.
4. Raises HTTP 401 if any step fails.

---

### 4.5 ML Model — Training

**File:** `train_model.py`

This script is run **once** before starting the server to train and save the ML model.

#### Dataset

**Obesity Level Prediction Dataset** (Kaggle):
- ~2,111 rows, 17 columns
- Target column: `NObeyesdad` (7 obesity level classes)
- Mixed numeric and categorical features

**Feature Columns Used:**

| Feature | Type | Description |
|---|---|---|
| Gender | Categorical | Male / Female |
| Age | Numeric | Age in years |
| Height | Numeric | Height in metres |
| Weight | Numeric | Weight in kilograms |
| BMI | Numeric | Computed: Weight / Height^2 (engineered feature) |
| family_history_with_overweight | Categorical | yes / no |
| FAVC | Categorical | Frequent high-caloric food consumption (yes/no) |
| FCVC | Numeric | Frequency of vegetable consumption (1-3) |
| NCP | Numeric | Number of main meals per day |
| CAEC | Categorical | Eating between meals (no/Sometimes/Frequently/Always) |
| SMOKE | Categorical | Smoker (yes/no) |
| CH2O | Numeric | Daily water consumption (litres) |
| SCC | Categorical | Calorie monitoring (yes/no) |
| FAF | Numeric | Physical activity frequency (0-5) |
| TUE | Numeric | Technology usage hours per day |
| CALC | Categorical | Alcohol consumption frequency |
| MTRANS | Categorical | Mode of transport |

**Target Classes (NObeyesdad):**

| Class | Description |
|---|---|
| Insufficient_Weight | BMI < 18.5 |
| Normal_Weight | BMI 18.5–24.9 |
| Overweight_Level_I | BMI 25–27.4 |
| Overweight_Level_II | BMI 27.5–29.9 |
| Obesity_Type_I | BMI 30–34.9 |
| Obesity_Type_II | BMI 35–39.9 |
| Obesity_Type_III | BMI >= 40 |

#### Training Pipeline

```
1. Load obesity.csv
2. Engineer BMI feature: df["BMI"] = df["Weight"] / (df["Height"] ** 2)
3. Label encode all categorical columns (LabelEncoder per column)
4. Label encode target column (NObeyesdad)
5. Train/test split: 80% train, 20% test (stratified, random_state=42)
6. Train RandomForestClassifier(n_estimators=200, random_state=42, n_jobs=-1)
7. Evaluate on test set (accuracy, classification report, confusion matrix)
8. Save artifacts:
   - ml_model/obesity_model.pkl  (fitted RandomForestClassifier)
   - ml_model/encoders.pkl       (dict of LabelEncoders + feature column list)
   - ml_model/accuracy.txt       (float accuracy score)
```

**Usage:**
```bash
python train_model.py
# or with custom path:
python train_model.py --dataset datasets/obesity.csv
```

---

### 4.6 ML Service — Prediction

**File:** `app/services/ml_service.py`

#### BMI Classification (`classify_bmi`)

| BMI Range | Category |
|---|---|
| < 18.5 | Underweight |
| 18.5 – 24.9 | Normal |
| 25.0 – 29.9 | Overweight |
| 30.0 – 34.9 | Obese Class I |
| 35.0 – 39.9 | Obese Class II |
| >= 40.0 | Obese Class III |

#### Feature Engineering (`build_input_dataframe`)

Converts the user's API request fields into a single-row pandas DataFrame that matches the model's training schema:
- Maps `physical_activity` → `FAF`, `water_intake` → `CH2O`
- Applies each column's saved `LabelEncoder` to encode categorical values
- Fills missing columns with 0.0
- Computes BMI from height and weight

#### Prediction (`predict_obesity`)

```
Input: RecommendationRequest fields
  --> build_input_dataframe() --> 1-row DataFrame
  --> model.predict() --> encoded class label
  --> le_target.inverse_transform() --> "Obesity_Type_I" (string)
  --> model.predict_proba() --> probability for each of 7 classes
  --> Return: (predicted_label, confidence_score, class_probabilities_dict)
```

#### Dietary Profiles (`OBESITY_DIET_PROFILE`)

Each of the 7 obesity levels has a custom dietary profile with:

- **filter** — A pandas boolean mask applied to the nutrition DataFrame as a gate (min/max calories, protein requirement, fat limit).
- **score_fn** — A weighted scoring formula that ranks foods within the filtered set.
- **notes** — Human-readable dietary advice included in the API response.

**Scoring Principle:**
- Protein is always rewarded (most satiating macro).
- Sugar and sodium are always penalised.
- Calories are rewarded for underweight users, penalised for obese users.
- Fat coefficient flips: positive for underweight, negative for obese.

**Example — Obesity_Type_I Profile:**
```
Filter: 50 <= Calories <= 300, Protein >= 8g, Fat <= 12g
Score:  Protein×3.5 − Calories×0.20 − Fat×1.00 − Sugar×1.50 − Sodium×0.004
```

**Example — Insufficient_Weight Profile:**
```
Filter: Calories >= 200, Protein >= 8g, Fat <= 60g
Score:  Protein×2.5 + Calories×0.30 + Fat×0.40 − Sugar×0.60 − Sodium×0.002
```

#### BMR Calculation (Mifflin-St Jeor)

Basal Metabolic Rate is calculated in the recommendations router:

```
Male:   BMR = 10 × weight(kg) + 6.25 × height(cm) − 5 × age + 5
Female: BMR = 10 × weight(kg) + 6.25 × height(cm) − 5 × age − 161
```

#### Daily Calorie Target

The daily calorie target is computed by multiplying BMR by an obesity-level-specific activity/deficit multiplier:

| Obesity Level | Multiplier | Goal |
|---|---|---|
| Insufficient_Weight | 1.75 | Aggressive caloric surplus |
| Normal_Weight | 1.55 | Active maintenance |
| Overweight_Level_I | 1.30 | Mild deficit |
| Overweight_Level_II | 1.20 | Moderate deficit |
| Obesity_Type_I | 1.10 | Controlled deficit |
| Obesity_Type_II | 1.05 | Supervised deficit |
| Obesity_Type_III | 1.00 | Strict supervised |

---

### 4.7 Nutrition Service

**File:** `app/services/nutrition_service.py`

#### Column Normalisation (`normalize_columns`)

The nutrition CSV may have different column names depending on the dataset version. This function maps all known aliases to a canonical schema:

| Canonical Name | Accepted Aliases |
|---|---|
| food_name | Food, Name, Item, FoodItem, food item |
| Calories | Calories, Energy, Caloric Value |
| Protein | Protein, Proteins |
| Carbohydrates | Carbohydrates, Carbs, Total Carbohydrate |
| Fat | Fat, Total Fat, Fats |
| Sugar | Sugar, Sugars, Total Sugars |
| Sodium | Sodium |

#### Data Quality Cleaning (`_clean_dataset`)

Applied globally on startup, 7 sequential filtering steps:

| Step | Rule | Reason |
|---|---|---|
| 1 | Drop rows with null Calories / Protein / Carbs / Fat | Core macros must be known |
| 2 | Drop rows where Calories <= 50 | Beverages and water are not food recommendations |
| 3 | Drop rows where Protein <= 2g | Juices and pure sugars excluded |
| 4 | Drop food_name shorter than 4 chars | Dataset artifacts |
| 5 | Drop names starting with '(' or digit | Serving-size annotations |
| 6 | Drop names matching junk keyword blocklist | Ice cream, soda, alcohol, candy, etc. |
| 7 | Fill remaining Sugar / Sodium nulls with 0 | Ensure no null fields in responses |

#### Food Recommendation (`get_food_recommendations`)

```
1. Look up dietary profile for the predicted obesity_level
2. Apply profile["filter"] to the cleaned DataFrame
3. If fewer than top_n foods pass the filter -> widen to full dataset (fallback)
4. Apply profile["score_fn"] to compute a score for each food
5. Sort descending by score
6. Return top 10 foods as list of dicts with full macro info
```

---

### 4.8 API Endpoints Reference

**Base URL:** `http://localhost:8000`
**Docs:** `http://localhost:8000/docs` (Swagger UI)

---

#### Health Endpoints

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/` | None | App info (name, version, docs link) |
| GET | `/health` | None | Server health: ML model + nutrition dataset loaded status |

**GET `/health` Response:**
```json
{
  "status": "healthy",
  "ml_model_loaded": true,
  "nutrition_dataset_loaded": true,
  "nutrition_food_count": 587
}
```

---

#### Auth Endpoints — `/auth`

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/auth/register` | None | Register a new user |
| POST | `/auth/login` | None | Login, returns JWT token |

**POST `/auth/register` — Request Body:**
```json
{
  "email": "alice@example.com",
  "username": "alice",
  "password": "securepassword",
  "full_name": "Alice Smith"
}
```

**POST `/auth/register` — Response (201 Created):**
```json
{
  "id": 1,
  "email": "alice@example.com",
  "username": "alice",
  "full_name": "Alice Smith",
  "is_active": true,
  "created_at": "2026-03-25T10:00:00Z"
}
```

**POST `/auth/login` — Request Body:**
```json
{
  "email": "alice@example.com",
  "password": "securepassword"
}
```

**POST `/auth/login` — Response:**
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "bearer"
}
```

---

#### User Endpoints — `/users`

| Method | Path | Auth | Description |
|---|---|---|---|
| GET | `/users/me` | Bearer JWT | Get current authenticated user's profile |

**GET `/users/me` — Response:**
```json
{
  "id": 1,
  "email": "alice@example.com",
  "username": "alice",
  "full_name": "Alice Smith",
  "is_active": true,
  "created_at": "2026-03-25T10:00:00Z"
}
```

---

#### Recommendation Endpoints — `/recommendations`

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/recommendations/ml` | Optional Bearer | ML prediction + food recommendations |
| GET | `/recommendations/history` | Required Bearer | Get user's past recommendations |
| GET | `/recommendations/obesity-levels` | None | List all 7 predictable obesity levels |

**POST `/recommendations/ml` — Request Body:**
```json
{
  "age": 25,
  "height": 1.70,
  "weight": 85,
  "gender": "Male",
  "family_history_with_overweight": "yes",
  "favc": "yes",
  "caec": "Sometimes",
  "smoke": "no",
  "scc": "no",
  "calc": "no",
  "mtrans": "Public_Transportation",
  "physical_activity": 1,
  "water_intake": 2,
  "faf": 1.0,
  "tue": 1.0,
  "ncp": 3.0,
  "ch2o": 2.0
}
```

**Request Field Validation:**

| Field | Type | Constraints |
|---|---|---|
| age | int | 1 – 120 |
| height | float | 0.5 – 3.0 (metres) |
| weight | float | 10.0 – 500.0 (kg) |
| gender | string | "Male" or "Female" |
| family_history_with_overweight | string | "yes" or "no" |
| favc | string | "yes" or "no" |
| caec | string | no/Sometimes/Frequently/Always |
| smoke | string | "yes" or "no" |
| scc | string | "yes" or "no" |
| calc | string | no/Sometimes/Frequently/Always |
| mtrans | string | Automobile/Bike/Motorbike/Public_Transportation/Walking |
| physical_activity | float | 0 – 5 |
| water_intake | float | 0 – 10 (litres) |
| ncp | float | 1 – 6 |
| faf | float | 0 – 5 |
| tue | float | 0 – 5 |
| ch2o | float | 0 – 10 |

**POST `/recommendations/ml` — Response:**
```json
{
  "bmi": 29.41,
  "bmi_category": "Overweight",
  "predicted_obesity_level": "Overweight_Level_I",
  "confidence_score": 0.87,
  "class_probabilities": {
    "Insufficient_Weight": 0.0,
    "Normal_Weight": 0.02,
    "Overweight_Level_I": 0.87,
    "Overweight_Level_II": 0.08,
    "Obesity_Type_I": 0.02,
    "Obesity_Type_II": 0.01,
    "Obesity_Type_III": 0.0
  },
  "recommended_foods": [
    {
      "food_name": "Grilled Chicken Breast",
      "calories": 165.0,
      "protein": 31.0,
      "carbs": 0.0,
      "fat": 3.6,
      "sugar": 0.0,
      "sodium": 74.0
    }
  ],
  "dietary_notes": "Reduce calorie and fat intake gradually...",
  "bmr": 1923.5,
  "daily_calorie_target": 2501,
  "total_foods_filtered": 142
}
```

**GET `/recommendations/history` — Query Params:**

| Param | Default | Description |
|---|---|---|
| skip | 0 | Offset (pagination) |
| limit | 20 | Max records to return |

**GET `/recommendations/history` — Response:**
```json
{
  "total": 5,
  "items": [
    {
      "id": 10,
      "age": 25,
      "gender": "Male",
      "height": 1.70,
      "weight": 85.0,
      "bmi": 29.41,
      "bmi_category": "Overweight",
      "predicted_obesity_level": "Overweight_Level_I",
      "confidence_score": 0.87,
      "bmr": 1923.5,
      "daily_calorie_target": 2501,
      "created_at": "2026-03-25T10:00:00"
    }
  ]
}
```

---

## 5. Android Frontend — HealthDietAI

### 5.1 Project Structure

```
app/src/main/
+-- java/com/healthdietapp/
|   +-- HealthDietApp.kt              # Application class (@HiltAndroidApp)
|   +-- MainActivity.kt              # Single Activity host (NavHostFragment)
|   +-- di/
|   |   +-- AppModule.kt             # Hilt module: provides Retrofit, ApiService
|   +-- data/
|   |   +-- api/
|   |   |   +-- RetrofitClient.kt    # OkHttp + Retrofit factory, AuthInterceptor
|   |   |   +-- ApiService.kt        # Retrofit interface (all API endpoints)
|   |   +-- model/
|   |   |   +-- AuthModels.kt        # LoginRequest, RegisterRequest, TokenResponse, UserResponse
|   |   |   +-- RecommendationResponse.kt   # RecommendationResponse, FoodItem
|   |   |   +-- RecommendationRequest.kt    # Request body data class
|   |   |   +-- RecommendationParcelable.kt # Parcelable wrapper for navigation
|   |   +-- repository/
|   |       +-- AuthRepository.kt           # Auth API calls
|   |       +-- RecommendationRepository.kt # Recommendation API calls
|   +-- viewmodel/
|   |   +-- AuthViewModel.kt           # Login/Register state management
|   |   +-- RecommendationViewModel.kt # Recommendation request state management
|   +-- ui/
|   |   +-- auth/
|   |   |   +-- LoginFragment.kt       # Login screen
|   |   |   +-- RegisterFragment.kt    # Registration screen
|   |   +-- dashboard/
|   |       +-- FoodAdapter.kt         # RecyclerView adapter for food cards
|   +-- utils/
|       +-- NetworkResult.kt           # Sealed class: Success/Error/Loading
|       +-- TokenManager.kt            # SharedPreferences JWT token storage
|       +-- Extensions.kt             # View extension functions
|       +-- Constants.kt              # BASE_URL, timeout, prefs keys
+-- res/
    +-- layout/
    |   +-- activity_main.xml         # Single activity with NavHostFragment
    |   +-- fragment_splash.xml       # Splash screen
    |   +-- fragment_login.xml        # Login form
    |   +-- fragment_register.xml     # Registration form
    |   +-- item_food_card.xml        # RecyclerView food item card
    +-- navigation/
    |   +-- nav_graph.xml             # Navigation graph (all screen flows)
    +-- values/
        +-- colors.xml, strings.xml, dimens.xml
```

---

### 5.2 Architecture — MVVM

The app follows the **MVVM (Model-View-ViewModel)** pattern strictly:

```
View (Fragment/Activity)
    |  observes StateFlow
    v
ViewModel (business logic, lifecycle-aware)
    |  calls suspend functions
    v
Repository (single source of truth)
    |  makes HTTP calls
    v
ApiService (Retrofit interface)
    |  HTTP over OkHttp
    v
FastAPI Backend
```

**Benefits:**
- ViewModels survive configuration changes (screen rotation).
- Fragments only observe UI state — no network logic in UI layer.
- Repositories are easily testable in isolation.
- StateFlow is lifecycle-aware (no memory leaks).

**State Management:**

All network state is represented as a sealed class `NetworkResult<T>`:

```kotlin
sealed class NetworkResult<T> {
    class Success<T>(val data: T) : NetworkResult<T>()
    class Error<T>(val message: String, val code: Int? = null) : NetworkResult<T>()
    class Loading<T> : NetworkResult<T>()
}
```

Fragments collect state from ViewModels using `viewLifecycleOwner.lifecycleScope.launch { viewModel.state.collect { ... } }`.

---

### 5.3 Dependency Injection — Hilt

**File:** `di/AppModule.kt`

Hilt is used to inject dependencies throughout the app without manual construction.

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideRetrofit(tokenManager: TokenManager): Retrofit {
        return RetrofitClient.create(tokenManager)
    }

    @Provides @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
```

**Injection Chain:**
```
@HiltAndroidApp (HealthDietApp)
  --> SingletonComponent
      --> TokenManager (SharedPreferences wrapper)
      --> Retrofit (HTTP client)
      --> ApiService (Retrofit interface)
      --> AuthRepository / RecommendationRepository
      --> @HiltViewModel AuthViewModel / RecommendationViewModel
      --> @AndroidEntryPoint Fragments
```

---

### 5.4 Network Layer

**File:** `data/api/RetrofitClient.kt`

#### AuthInterceptor

Automatically attaches the JWT Bearer token to every request that is NOT `/auth/login` or `/auth/register`:

```kotlin
class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val path = chain.request().url.encodedPath
        return if (path.contains("auth/login") || path.contains("auth/register")) {
            chain.proceed(chain.request())  // No token for auth endpoints
        } else {
            val token = tokenManager.getToken()
            val newRequest = if (token != null) {
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else {
                chain.request()
            }
            chain.proceed(newRequest)
        }
    }
}
```

#### OkHttp Client Configuration

| Setting | Value |
|---|---|
| Connect timeout | 30 seconds (Constants.TIMEOUT_SECONDS) |
| Read timeout | 30 seconds |
| Write timeout | 30 seconds |
| Logging | HttpLoggingInterceptor (BODY level) |
| Interceptors | AuthInterceptor (token injection) |

**Retrofit Configuration:**
- Base URL: `Constants.BASE_URL` (set to backend server URL)
- Converter: `GsonConverterFactory` (JSON parsing)

---

### 5.5 Data Models

**Auth Models** (`data/model/AuthModels.kt`):

| Class | Direction | Fields |
|---|---|---|
| `LoginRequest` | Request | email, password |
| `RegisterRequest` | Request | email, username, password, full_name |
| `TokenResponse` | Response | access_token, token_type |
| `UserResponse` | Response | id, email, username, full_name, is_active |

**Recommendation Models** (`data/model/RecommendationResponse.kt`):

| Class | Direction | Fields |
|---|---|---|
| `RecommendationResponse` | Response | bmi, bmi_category, predicted_obesity_level, confidence_score, class_probabilities, recommended_foods, dietary_notes, bmr, daily_calorie_target, total_foods_filtered |
| `FoodItem` | Nested in Response | food_name, calories, protein, carbs, fat, sugar, sodium |

**Token Management** (`utils/TokenManager.kt`):

The JWT token is stored securely in `SharedPreferences` using private mode:

| Method | Description |
|---|---|
| `saveToken(token)` | Store JWT after successful login |
| `getToken()` | Retrieve stored JWT (null if not logged in) |
| `clearToken()` | Remove JWT on logout |
| `isLoggedIn()` | Returns true if a token exists |

---

### 5.6 ViewModels

#### AuthViewModel

**File:** `viewmodel/AuthViewModel.kt`

| StateFlow | Type | Description |
|---|---|---|
| `loginState` | `NetworkResult<TokenResponse>?` | State of login operation |
| `registerState` | `NetworkResult<UserResponse>?` | State of registration operation |

| Function | Description |
|---|---|
| `login(email, password)` | Triggers login API call, updates loginState |
| `register(name, email, username, password)` | Triggers register API call, updates registerState |
| `resetLoginState()` | Resets loginState to null |
| `resetRegisterState()` | Resets registerState to null |

#### RecommendationViewModel

**File:** `viewmodel/RecommendationViewModel.kt`

| StateFlow | Type | Description |
|---|---|---|
| `recommendationState` | `NetworkResult<RecommendationResponse>?` | State of recommendation request |

| Function | Description |
|---|---|
| `getRecommendation(request)` | Triggers ML recommendation API call |
| `resetState()` | Resets recommendation state to null |

---

### 5.7 UI Screens & Navigation

The app uses a **Single Activity + Multiple Fragments** pattern with the Navigation Component.

**Navigation Graph Flow:**
```
SplashFragment
    |
    +--> LoginFragment  <----> RegisterFragment
              |
              v (after successful login)
         ProfileFragment (health form input)
              |
              v (after submitting form)
         ResultsFragment (bmi, obesity level, foods)
              |
              v
         HistoryFragment (past recommendations)
```

#### SplashFragment
- Checks if user is already logged in via `TokenManager.isLoggedIn()`.
- If logged in: navigate directly to ProfileFragment.
- If not: navigate to LoginFragment.

#### LoginFragment
- Collects email and password input.
- Validates fields (not empty).
- Calls `AuthViewModel.login()`.
- On `NetworkResult.Success`: saves token via `TokenManager.saveToken()`, navigates to ProfileFragment (clearing back stack so user cannot go back to login).
- On `NetworkResult.Error`: shows error Snackbar, clears token if 401.
- On `NetworkResult.Loading`: shows progress indicator, disables login button.

#### RegisterFragment
- Collects full name, email, username, password, confirm password.
- Validates all fields and checks password match.
- Calls `AuthViewModel.register()`.
- On success: shows success Snackbar, navigates back to LoginFragment.

#### FoodAdapter (RecyclerView)
- Uses `ListAdapter` with `DiffUtil` for efficient list updates.
- Binds `FoodItemParcelable` data to `ItemFoodCardBinding`.
- Each card displays: food name, calories (kcal), protein (g), carbs (g), fat (g).
- Includes a fade-in animation using `ObjectAnimator` for smooth UX.

---

## 6. End-to-End Request Flow

This section traces a full user journey from app launch to receiving recommendations.

### Step 1: App Launch
```
Android App Starts
--> SplashFragment checks TokenManager.isLoggedIn()
--> If no token: navigate to LoginFragment
```

### Step 2: User Registration (First Time)
```
User fills: full name, email, username, password
--> RegisterFragment validates input
--> AuthViewModel.register() called
--> AuthRepository.register() makes:
    POST /auth/register
    Body: { email, username, password, full_name }
--> Backend: checks uniqueness, hashes password, saves User to DB
--> Response: UserResponse (id, email, username, ...)
--> App: shows success, navigates to LoginFragment
```

### Step 3: Login
```
User fills: email, password
--> LoginFragment validates input
--> AuthViewModel.login() called
--> AuthRepository.login() makes:
    POST /auth/login
    Body: { email, password }
--> Backend: bcrypt.checkpw(), creates JWT (sub=user_id, exp=24h)
--> Response: { access_token, token_type: "bearer" }
--> App: TokenManager.saveToken(access_token)
--> Navigate to ProfileFragment (back stack cleared)
```

### Step 4: Fill Health Profile
```
User fills form fields:
  age, height, weight, gender,
  family_history_with_overweight,
  favc, caec, smoke, scc, calc, mtrans,
  physical_activity, water_intake
--> RecommendationViewModel.getRecommendation(request) called
```

### Step 5: ML Prediction (Backend)
```
POST /recommendations/ml
Authorization: Bearer <jwt_token>
Body: { age, height, weight, gender, ... all lifestyle fields }

Backend Pipeline:
1. Validate RecommendationRequest (Pydantic)
2. Load ML model + encoders from app.state
3. Calculate BMI = weight / height^2
4. Classify BMI -> bmi_category string
5. Calculate BMR (Mifflin-St Jeor formula)
6. build_input_dataframe() -> encode categoricals via LabelEncoders
7. model.predict() -> encoded obesity class
8. le_target.inverse_transform() -> "Overweight_Level_I"
9. model.predict_proba() -> confidence score + class probabilities
10. _daily_calorie_target(bmr, predicted_level) -> daily calorie target
11. get_food_recommendations(nutrition_df, "Overweight_Level_I", top_n=10):
    a. Apply filter gate (calories, protein, fat thresholds)
    b. Apply scoring formula (weighted)
    c. Sort descending by score
    d. Return top 10 foods
12. Save RecommendationHistory to DB (if token valid)
13. Return RecommendationResponse JSON
```

### Step 6: Display Results
```
App receives RecommendationResponse
--> NavigateToResultsFragment with response data (Parcelable)
--> Display:
    - BMI value and category
    - Predicted obesity level + confidence %
    - BMR and daily calorie target
    - Dietary advice notes
    - RecyclerView of 10 food cards (FoodAdapter)
      Each card: name, calories, protein, carbs, fat
```

### Step 7: View History
```
User navigates to History screen
GET /recommendations/history
Authorization: Bearer <jwt_token>

Backend:
--> get_current_user() dependency resolves user from JWT
--> Query RecommendationHistory WHERE user_id = current_user.id
--> ORDER BY created_at DESC
--> Return paginated HistoryResponse

App displays list of past recommendations with date, BMI, obesity level
```

---

## 7. Database Schema

### Table: `users`

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | INTEGER | PRIMARY KEY, AUTO INCREMENT | Unique user identifier |
| email | VARCHAR | UNIQUE, NOT NULL, INDEXED | User email address |
| username | VARCHAR | UNIQUE, NOT NULL, INDEXED | User display name |
| hashed_password | VARCHAR | NOT NULL | bcrypt-hashed password |
| full_name | VARCHAR | NULLABLE | User's full name |
| is_active | BOOLEAN | DEFAULT TRUE | Soft-delete / deactivation flag |
| created_at | TIMESTAMP WITH TZ | SERVER DEFAULT now() | Account creation time |
| updated_at | TIMESTAMP WITH TZ | ON UPDATE now() | Last profile update time |

### Table: `recommendation_history`

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | INTEGER | PRIMARY KEY, AUTO INCREMENT | Record identifier |
| user_id | INTEGER | FK users.id ON DELETE CASCADE, INDEXED | Owning user |
| age | INTEGER | NOT NULL | Input: user's age |
| gender | VARCHAR | NOT NULL | Input: Male / Female |
| height | FLOAT | NOT NULL | Input: height in metres |
| weight | FLOAT | NOT NULL | Input: weight in kg |
| bmi | FLOAT | NOT NULL | Computed BMI |
| bmi_category | VARCHAR | NOT NULL | Underweight / Normal / Overweight / etc. |
| predicted_obesity_level | VARCHAR | NOT NULL | ML model output class |
| confidence_score | FLOAT | NOT NULL | Model confidence (0.0 – 1.0) |
| bmr | FLOAT | NOT NULL | Basal Metabolic Rate (kcal/day) |
| daily_calorie_target | INTEGER | NOT NULL | Adjusted daily calorie goal |
| created_at | TIMESTAMP WITH TZ | SERVER DEFAULT now() | When this recommendation was made |

**Relationships:**
- `recommendation_history.user_id` → `users.id` (Many-to-One)
- ON DELETE CASCADE: deleting a user removes all their history

---

## 8. Datasets

### Obesity Level Prediction Dataset

| Property | Value |
|---|---|
| Source | Kaggle — fatemehmehrparvar/obesity-levels |
| File | `datasets/obesity.csv` |
| Rows | ~2,111 |
| Columns | 17 features + 1 target |
| Target | NObeyesdad (7 obesity classes) |
| Used For | Training the RandomForestClassifier |

### Nutrition Details Dataset

| Property | Value |
|---|---|
| Source | Kaggle — niharika41298/nutrition-details-for-most-common-foods |
| File | `datasets/nutrition.csv` |
| Rows | 600+ food items |
| Columns | food_name, Calories, Protein, Carbohydrates, Fat, Sugar, Sodium |
| Used For | Food recommendation filtering and ranking |

---

## 9. Setup & Running the Project

### Backend Setup

**Prerequisites:**
- Python 3.10+
- PostgreSQL 15+
- pip

**Step 1: Clone and install dependencies**
```bash
cd "D:\CLg\Docter Ai\system-backend"
pip install -r requirements.txt
```

**Step 2: Configure environment**
```bash
cp .env.example .env
# Edit .env with your PostgreSQL credentials and a strong SECRET_KEY
```

**Step 3: Create the database**
```sql
-- In psql or pgAdmin:
CREATE DATABASE health_diet_db;
```

**Step 4: Download Kaggle datasets**
```
Place in datasets/ folder:
  datasets/obesity.csv    (from: kaggle.com/datasets/fatemehmehrparvar/obesity-levels)
  datasets/nutrition.csv  (from: kaggle.com/datasets/niharika41298/nutrition-details-for-most-common-foods)
```

**Step 5: Train the ML model**
```bash
python train_model.py
# Output: ml_model/obesity_model.pkl, ml_model/encoders.pkl, ml_model/accuracy.txt
```

**Step 6: Start the server**
```bash
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

**Verify:**
- Swagger UI: `http://localhost:8000/docs`
- Health check: `http://localhost:8000/health`

---

### Android App Setup

**Prerequisites:**
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK API 34

**Step 1: Open project**
```
Open Android Studio -> Open -> Select d:\CLg\HealthDietAI
Wait for Gradle sync to complete
```

**Step 2: Configure Base URL**

Edit `app/src/main/java/com/healthdietapp/utils/Constants.kt`:
```kotlin
object Constants {
    const val BASE_URL = "http://10.0.2.2:8000/"  // Android emulator -> localhost
    // For physical device: const val BASE_URL = "http://YOUR_PC_IP:8000/"
    const val TIMEOUT_SECONDS = 30L
    const val PREFS_NAME = "health_diet_prefs"
    const val TOKEN_KEY = "auth_token"
}
```

**Step 3: Run the app**
```
Select an emulator or physical device
Click Run (Shift+F10)
```

---

## 10. API Quick Reference

```
Base URL: http://localhost:8000

Health
  GET  /                           -> App info
  GET  /health                     -> Server status

Authentication
  POST /auth/register              -> Register user
  POST /auth/login                 -> Login, get JWT

User
  GET  /users/me                   -> [Auth] Get profile

Recommendations
  POST /recommendations/ml         -> [Optional Auth] ML prediction + food list
  GET  /recommendations/history    -> [Auth] Past recommendations (paginated)
  GET  /recommendations/obesity-levels -> List all 7 obesity level labels

HTTP Headers (for protected endpoints):
  Authorization: Bearer <access_token>

Content-Type: application/json
```

---

*This documentation covers the complete system end-to-end as built for the Final Year Project submission.*
*Backend: FastAPI + PostgreSQL + scikit-learn | Frontend: Android (Kotlin) + Hilt + Retrofit + MVVM*
