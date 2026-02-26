# HealthDietAI - Build and Deployment Guide

## Prerequisites

### System Requirements
- **Windows 10 or later** (or macOS/Linux)
- **Java Development Kit (JDK) 8 or higher**
- **Android Studio 2023.1 or newer** (or command line tools)
- **Minimum 2GB RAM** (4GB recommended)
- **At least 5GB free disk space**

### Android Development Tools
- **Android SDK Tools** (installed via Android Studio)
- **Android SDK Platform 34** (Android 14)
- **Android SDK Platform Tools**
- **Android Emulator** (optional but recommended for testing)

## Setup Instructions

### 1. Install Java (JDK)
```bash
# Verify Java is installed
java -version

# Output should be similar to:
# openjdk version "11.0.x" 2021-...
# OpenJDK Runtime Environment ...
```

If Java is not installed:
- **Windows**: Download from [java.com](https://www.java.com) or [adoptopenjdk.net](https://adoptopenjdk.net)
- **macOS**: Install via Homebrew: `brew install openjdk`
- **Linux**: `sudo apt-get install default-jdk`

Set JAVA_HOME environment variable:
- **Windows**: 
  1. Right-click "This PC" → Properties → Advanced system settings
  2. Click "Environment Variables"
  3. New System Variable: `JAVA_HOME = C:\Program Files\Java\jdk-11` (or your JDK path)

### 2. Install Android Studio
- Download from [developer.android.com](https://developer.android.com/studio)
- Run installer and follow setup wizard
- Install Android SDK 34 and necessary tools

### 3. Clone/Setup Project
```bash
cd D:\CLg\HealthDietAI
```

### 4. Update local.properties
Create or update `local.properties` in the project root:
```properties
sdk.dir=C:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
```

## Build Commands

### Clean Build
```bash
./gradlew clean build
```

### Build Debug APK
```bash
./gradlew assembleDebug
```
Output: `app/build/outputs/apk/debug/app-debug.apk`

### Build Release APK
```bash
./gradlew assembleRelease
```
Output: `app/build/outputs/apk/release/app-release.apk`

### Run Tests
```bash
./gradlew test
```

### Check Dependencies
```bash
./gradlew dependencies
```

### View Project Info
```bash
./gradlew projects
```

## Running on Android Emulator (Pixel 6)

### Step 1: Create Virtual Device (if not exists)
1. Open Android Studio
2. Go to **Tools** → **Device Manager**
3. Click **Create Device**
4. Select **Pixel 6** 
5. Choose **API 34** (Android 14)
6. Click **Finish**

### Step 2: Start Emulator
```bash
# List available devices
emulator -list-avds

# Start Pixel 6 emulator
emulator -avd Pixel_6_API_34

# Alternative: Start from Android Studio
# Tools → Device Manager → Play button on your device
```

### Step 3: Install and Run App
```bash
# Install debug APK on emulator
./gradlew installDebug

# Launch app
adb shell am start -n com.healthdietapp/.MainActivity

# Or use Android Studio's "Run" button (Shift+F10)
```

### Step 4: View Logs
```bash
# View all logs
adb logcat

# Filter logs for your app
adb logcat | grep healthdietapp

# Save logs to file
adb logcat > logcat.txt
```

## Configuration Updates

### Backend API URL
Edit `app/src/main/java/com/healthdietapp/utils/Constants.kt`:
```kotlin
const val BASE_URL = "http://YOUR_BACKEND_URL:8000/"
```

For emulator to access local machine (localhost):
```kotlin
const val BASE_URL = "http://10.0.2.2:8000/"  // 10.0.2.2 is host's localhost from emulator
```

## Gradle Build Properties

File: `gradle.properties`
```properties
android.useAndroidX=true           # Use AndroidX libraries
android.enableJetifier=true        # Auto-convert libraries to AndroidX
org.gradle.jvmargs=-Xmx4096m       # Max heap size for Gradle
```

## Troubleshooting

### Issue: "JAVA_HOME is not set"
**Solution**: Set JAVA_HOME environment variable (see Setup Step 1)

### Issue: "Gradle build failed"
**Solutions**:
```bash
# Clear Gradle cache
./gradlew clean

# Rebuild with verbose output
./gradlew build --info

# Update Gradle wrapper
./gradlew wrapper --gradle-version=8.7
```

### Issue: "Emulator won't start"
**Solutions**:
```bash
# Check if emulator is already running
adb devices

# Kill all emulator processes
adb kill-server

# Restart emulator
emulator -avd Pixel_6_API_34 -no-snapshot-load
```

### Issue: "App crashes on launch"
**Solutions**:
1. Check logcat for errors: `adb logcat | grep healthdietapp`
2. Verify API endpoint is correct in Constants.kt
3. Check network connectivity: Emulator can reach backend
4. Verify all dependencies are resolved: `./gradlew dependencies`

### Issue: "Build takes too long"
**Solutions**:
```bash
# Parallel builds and daemon
./gradlew build -x test --parallel --daemon

# Increase heap size in gradle.properties
org.gradle.jvmargs=-Xmx4096m
```

## CI/CD Integration (GitHub Actions)

Create `.github/workflows/build.yml`:
```yaml
name: Build APK

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: '11'
      - run: chmod +x gradlew
      - run: ./gradlew build
```

## Performance Tips

1. **Enable Gradle Daemon** (enabled by default)
2. **Use Incremental Builds**: Avoid `clean` unless necessary
3. **Increase Heap Size**: Update `gradle.properties`
4. **Parallel Builds**: `org.gradle.parallel=true`
5. **Offline Mode**: `./gradlew build --offline` (if dependencies cached)

## Debugging

### Remote Debugging
1. App must be debuggable (default for debug builds)
2. In Android Studio: **Run** → **Debug 'app'** (Shift+F9)
3. Set breakpoints in code
4. Step through code execution

### Debug Builds Feature
```kotlin
// In code, check if app is debuggable
if (BuildConfig.DEBUG) {
    // Enable logging, mock responses, etc.
}
```

## Release Build

### Generate Signed APK
1. **Build** → **Generate Signed Bundle/APK**
2. Select **APK**
3. Create or select keystore
4. Fill in keystore details
5. Choose **release** build variant
6. Click **Finish**

### Keystore Security
```bash
# Create keystore (do this once, keep it safe)
keytool -genkey -v -keystore my-release-key.keystore \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias my-key-alias
```

## Testing

### Unit Tests
```bash
./gradlew testDebugUnitTest
```

### Instrumented Tests (on device/emulator)
```bash
./gradlew connectedAndroidTest
```

## Project Statistics

Run: `./gradlew projects`
Expected output shows all project modules and dependencies.

## Next Steps

1. ✅ **Code Structure** - Properly organized packages
2. ✅ **Dependency Injection** - Hilt fully configured
3. ✅ **API Integration** - Retrofit with auth interceptor
4. ⏳ **Backend Configuration** - Update Constants.BASE_URL
5. ⏳ **Testing** - Write unit and UI tests
6. ⏳ **Deployment** - Generate signed release APK

---
**Document Version**: 1.0
**Last Updated**: February 27, 2026

