# HealthDietAI - Quick Reference Card

## 🚀 Build Commands Cheat Sheet

```bash
# Clean and build
./gradlew clean build

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install on device
./gradlew installDebug

# Run tests
./gradlew test

# Check dependencies
./gradlew dependencies

# View build info
./gradlew projects
```

## 📱 Emulator Commands

```bash
# Start emulator
emulator -avd Pixel_6_API_34

# List AVDs
emulator -list-avds

# Install app
adb install app/build/outputs/apk/debug/app-debug.apk

# Start app
adb shell am start -n com.healthdietapp/.MainActivity

# View logs
adb logcat | grep healthdietapp

# Clear app data
adb shell pm clear com.healthdietapp

# List devices
adb devices

# Reboot device
adb reboot
```

## 🔧 Configuration

### API Base URL
**File**: `app/src/main/java/com/healthdietapp/utils/Constants.kt`

```kotlin
// For emulator
const val BASE_URL = "http://10.0.2.2:8000/"

// For physical device
const val BASE_URL = "http://192.168.1.100:8000/"

// For remote server
const val BASE_URL = "https://api.yourdomain.com:8000/"
```

### Environment Variables
```bash
# Windows
set JAVA_HOME=C:\Program Files\Java\jdk-11
set ANDROID_HOME=C:\Users\%USERNAME%\AppData\Local\Android\Sdk

# macOS/Linux
export JAVA_HOME=$(/usr/libexec/java_home)
export ANDROID_HOME=$HOME/Android/Sdk
```

## 🗂️ Project Structure at a Glance

```
app/src/main/java/com/healthdietapp/
├── data/           (API, Models, Repositories)
├── di/             (Hilt Modules)
├── ui/             (Fragments)
├── viewmodel/      (ViewModels)
└── utils/          (Utilities)
```

## 📝 Key Files

| File | Location | Purpose |
|------|----------|---------|
| Constants.kt | utils/ | Configuration |
| TokenManager.kt | utils/ | JWT Management |
| ApiService.kt | data/api/ | API Endpoints |
| AuthViewModel.kt | viewmodel/ | Auth State |
| LoginFragment.kt | ui/auth/ | Login Screen |
| MainActivity.kt | root | Main Activity |

## 🔌 API Endpoints

```
POST /auth/register      (Login/Register)
POST /auth/login         (Authentication)
POST /recommendations/ml (Diet Recommendation)
```

### Auth Header
```
Authorization: Bearer <jwt_token>
```

## 🛠️ Android Studio Shortcuts

| Action | Windows | macOS |
|--------|---------|-------|
| Build | Ctrl+Shift+B | ⌘⇧B |
| Run | Shift+F10 | ^R |
| Debug | Shift+F9 | ^D |
| Format | Ctrl+Alt+L | ⌘⌥L |
| Sync Gradle | Ctrl+Shift+I | ⌘⇧I |

## 📚 Documentation Quick Links

- **README.md** - Project overview
- **BUILD_AND_DEPLOYMENT_GUIDE.md** - Build instructions
- **PROJECT_STRUCTURE.md** - Architecture details
- **CONTRIBUTING.md** - Development standards
- **TROUBLESHOOTING.md** - Problem solving
- **PROJECT_SUMMARY.md** - Complete status
- **DOCUMENTATION_INDEX.md** - Doc navigation

## 🐛 Common Issues Quick Fixes

| Issue | Solution |
|-------|----------|
| JAVA_HOME not set | Set environment variable (see Configuration) |
| Gradle build fails | `./gradlew clean` then rebuild |
| Emulator won't start | `adb kill-server` then restart |
| App crashes | Check logcat: `adb logcat \| grep healthdietapp` |
| API timeout | Verify backend is running, check URL |
| Token expired | Log out and log back in |

## 🔐 Security Checklist

- ✅ JWT tokens stored securely
- ✅ API calls use HTTPS in production
- ✅ Input validation on all forms
- ✅ Error messages don't expose sensitive info
- ✅ Tokens injected via interceptor
- ✅ ProGuard rules configured

## 📊 Architecture Layers

```
UI Layer
  ↓ (observes StateFlow)
ViewModel Layer
  ↓ (calls)
Repository Layer
  ↓ (calls)
API Layer (Retrofit)
```

## 🎯 Development Workflow

1. Create feature branch: `git checkout -b feature/name`
2. Make changes following CONTRIBUTING.md guidelines
3. Build and test: `./gradlew build`
4. Commit with descriptive message
5. Create Pull Request
6. Address review comments
7. Merge when approved

## 🚀 Build Status Check

```bash
# Full build check
./gradlew clean build

# Expected output
BUILD SUCCESSFUL in Xs

# If failed
# Check specific error above
# Refer to TROUBLESHOOTING.md
```

## 💾 Database & Storage

- **SharedPreferences**: JWT tokens (TokenManager.kt)
- **Local Database**: Not yet implemented (future: Room)
- **Cache**: Not yet implemented (future: WorkManager)

## 🔄 State Management Flow

```
User Action (Click button)
    ↓
ViewModel Method Called
    ↓
Repository Call (API)
    ↓
StateFlow Updated
    ↓
UI Recomposes (auto-refresh)
```

## 📲 Testing on Device

```bash
# 1. Connect device via USB
# 2. Enable Developer Mode (tap Build Number 7 times)
# 3. Allow USB Debugging
# 4. Run:
./gradlew installDebug

# App will install and auto-launch
```

## 🎨 UI Layers & Navigation

```
MainActivity (NavController)
    └── Navigation Graph
        ├── SplashFragment
        ├── LoginFragment ↔ RegisterFragment
        ├── ProfileFragment
        └── DashboardFragment
```

## 📦 Dependencies Overview

- **Hilt**: Dependency Injection
- **Retrofit**: HTTP Client
- **OkHttp**: HTTP Logging
- **Coroutines**: Async Operations
- **Navigation**: Fragment Navigation
- **Material**: UI Components
- **Glide**: Image Loading
- **MPAndroidChart**: Charts

## 🎓 Learning Resources

- [Android Architecture Components](https://developer.android.com/topic/architecture)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Hilt Dependency Injection](https://dagger.dev/hilt/)
- [Retrofit HTTP Client](https://square.github.io/retrofit/)
- [Jetpack Navigation](https://developer.android.com/guide/navigation)

## ⚡ Performance Tips

- Use `clean` only when necessary
- Enable Gradle parallel builds
- Cache dependencies locally
- Use incremental builds during development
- Monitor memory in Android Profiler

## 📞 Support Resources

1. **Errors?** → Check TROUBLESHOOTING.md
2. **Building?** → Check BUILD_AND_DEPLOYMENT_GUIDE.md
3. **Development?** → Check CONTRIBUTING.md
4. **Architecture?** → Check PROJECT_STRUCTURE.md
5. **Lost?** → Check DOCUMENTATION_INDEX.md

---

**Quick Reference Version**: 1.0
**Last Updated**: February 27, 2026
**Keep this handy while developing!**

