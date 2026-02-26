# Troubleshooting Guide - HealthDietAI

## Common Issues and Solutions

### 1. Build Issues

#### Issue: "JAVA_HOME is not set"
```
ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH
```

**Solution:**
1. Install Java JDK (if not installed)
2. Set JAVA_HOME environment variable:
   - **Windows**: 
     - Right-click "This PC" → Properties
     - Advanced system settings → Environment Variables
     - New System Variable: `JAVA_HOME = C:\Program Files\Java\jdk-11`
   - **macOS**: `export JAVA_HOME=$(/usr/libexec/java_home)`
   - **Linux**: `export JAVA_HOME=/usr/lib/jvm/java-11-openjdk`

#### Issue: "Failed to resolve dependency"
```
Could not resolve com.example:library:1.0.0
```

**Solution:**
```bash
# Clear Gradle cache
./gradlew clean

# Refresh dependencies
./gradlew build --refresh-dependencies

# Check internet connection - ensure you can reach Maven repositories
```

#### Issue: "Gradle build timeout"
```
Build takes forever to complete
```

**Solution:**
```bash
# Increase Gradle heap size in gradle.properties
org.gradle.jvmargs=-Xmx4096m

# Enable parallel builds
org.gradle.parallel=true

# Skip tests during development
./gradlew build -x test
```

#### Issue: "Compilation error: Unresolved reference"
```
Unresolved reference: 'someClass'
```

**Solution:**
1. Clean and rebuild:
   ```bash
   ./gradlew clean build
   ```
2. Invalidate Android Studio cache:
   - File → Invalidate Caches → Invalidate and Restart
3. Check for typos in imports and class names
4. Ensure all dependencies are in `build.gradle.kts`

---

### 2. Runtime Issues

#### Issue: "App crashes on startup"

**Check the logs:**
```bash
adb logcat | grep healthdietapp
```

**Common causes and solutions:**

1. **NullPointerException:**
   - Check API endpoint configuration
   - Verify backend is running
   - Check network connectivity

2. **BindingException:**
   - Rebuild and sync Gradle
   - Clear build directory: `./gradlew clean`

3. **Missing resources:**
   - Verify all layout files exist
   - Check string resources are defined
   - Rebuild project

#### Issue: "App force closes when logging in"

**Debug steps:**
```bash
# View crash logs
adb logcat | grep "FATAL\|Exception"

# Check if backend is reachable
adb shell ping 10.0.2.2  # For emulator
```

**Solutions:**
- Verify Constants.BASE_URL is correct
- Check network security config allows cleartext traffic
- Ensure backend API is running and accessible
- Verify request/response models match API

#### Issue: "Network timeout errors"

**Solutions:**
1. Check network connectivity:
   ```bash
   adb shell ping google.com  # Device
   ping 10.0.2.2              # Emulator to host
   ```

2. Increase timeout in Constants.kt:
   ```kotlin
   const val TIMEOUT_SECONDS = 120L  // Increase from 60L
   ```

3. Check backend is running:
   ```bash
   curl http://10.0.2.2:8000/health
   ```

#### Issue: "401 Unauthorized - Token expired"

**Solution:**
The token has expired and needs to be refreshed.

1. Manual fix (temporary):
   - Log out and log back in
   - This will generate a new token

2. Implement token refresh (production):
   ```kotlin
   // In AuthInterceptor
   if (response.code() == 401) {
       // Try to refresh token
       val newToken = refreshToken()
       if (newToken != null) {
           // Retry request with new token
           return chain.proceed(newRequest(newToken))
       }
   }
   ```

---

### 3. Emulator Issues

#### Issue: "Emulator won't start"

**Solution:**
```bash
# Check if ADB server is running
adb start-server

# Kill all processes
adb kill-server

# Kill emulator process
taskkill /F /IM emulator.exe  # Windows
pkill emulator              # macOS/Linux

# Start emulator with fresh state
emulator -avd Pixel_6_API_34 -no-snapshot-load
```

#### Issue: "Emulator is very slow"

**Solution:**
1. Check system resources:
   - Close unnecessary applications
   - Ensure at least 4GB RAM available
   
2. Enable hardware acceleration:
   - Android Studio → Settings → Emulator
   - Enable "Use native host GPU (via ANGLE)"

3. Increase emulator RAM:
   - Tools → AVD Manager → Edit Device
   - RAM: 4096 MB (or higher)

4. Use smaller device profile:
   - Use Nexus 5X instead of Pixel 6
   - Reduces resource requirements

#### Issue: "Cannot connect to emulator from host"

**Solution:**
```bash
# For accessing localhost from emulator use:
http://10.0.2.2:8000/  # Instead of http://localhost:8000/

# Check connectivity
adb shell ping 10.0.2.2
```

---

### 4. Android Studio Issues

#### Issue: "Android Studio is slow/freezing"

**Solution:**
1. Invalidate and restart cache:
   - File → Invalidate Caches → Invalidate and Restart

2. Increase IDE heap size:
   - Help → Edit Custom VM Options
   - Change: `-Xmx2048m` to `-Xmx4096m`

3. Disable unnecessary plugins:
   - Settings → Plugins → Disable unused plugins

4. Close other projects:
   - File → Close Other Projects

#### Issue: "Gradle sync fails"

**Solution:**
```bash
# From command line
./gradlew clean sync

# Or in Android Studio
File → Sync Now

# If still fails
File → Invalidate Caches → Invalidate and Restart
```

#### Issue: "Layout preview not showing"

**Solution:**
1. Rebuild project: `Ctrl+Shift+R` (Windows/Linux) or `⌘⇧R` (macOS)
2. Change API level in preview
3. Restart Android Studio

---

### 5. Network/API Issues

#### Issue: "API request returns 422 Validation Error"

**Solution:**
The request body doesn't match expected format.

1. Verify all required fields are included:
   ```kotlin
   data class RecommendationRequest(
       val age: Int,
       val height: Float,
       val weight: Float,
       // ... all fields must be provided
   )
   ```

2. Check field types match backend expectations
3. View API response: Check logcat for error message
4. Validate request before sending:
   ```kotlin
   if (age < 18 || age > 120) {
       return NetworkResult.Error("Invalid age")
   }
   ```

#### Issue: "CORS error when accessing API"

**Solution:**
Backend must enable CORS for your domain. This is a backend configuration issue.

Contact backend team to ensure CORS headers are configured.

#### Issue: "SSL certificate verification failed"

**Solution:**
1. For development (not production):
   - Create network security config to allow cleartext
   - Edit `res/xml/network_security_config.xml`

2. For production:
   - Implement certificate pinning
   - Use proper SSL certificates

#### Issue: "API calls not including Authorization header"

**Solution:**
Check TokenManager and AuthInterceptor:

```kotlin
// Verify token is saved
fun isTokenSaved() {
    val token = tokenManager.getToken()
    Log.d("TokenManager", "Token: $token")
}

// Verify interceptor adds header
// Check RetrofitClient.kt for AuthInterceptor setup
```

---

### 6. Data Issues

#### Issue: "Token not persisting after app close"

**Solution:**
TokenManager uses SharedPreferences which should persist.

1. Verify token is saved:
   ```kotlin
   fun saveAndVerifyToken(token: String) {
       tokenManager.saveToken(token)
       val saved = tokenManager.getToken()
       Log.d("Token", "Saved: $saved")
   }
   ```

2. Check SharedPreferences is configured:
   ```kotlin
   private val prefs: SharedPreferences =
       context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
   ```

#### Issue: "Data not updating after API call"

**Solution:**
StateFlow might not be collecting updates properly.

1. Verify StateFlow is configured as `MutableStateFlow`
2. Check lifecycle-aware collection:
   ```kotlin
   lifecycleScope.launch {
       viewModel.state.collect { result ->
           // This block should execute
           Log.d("ViewModel", "New result: $result")
       }
   }
   ```

3. Ensure ViewModel is @HiltViewModel annotated

---

### 7. Version and Dependency Issues

#### Issue: "Kotlin version mismatch"

**Solution:**
Ensure all Kotlin versions match in `libs.versions.toml`

```toml
[versions]
kotlin = "1.9.20"  # All modules must use same version
```

#### Issue: "Dependency conflict"

**Solution:**
```bash
# View dependency tree
./gradlew dependencies

# Find conflicting versions
# Exclude conflicting dependency
implementation(libs.library) {
    exclude(group = "com.example", module = "conflicting")
}
```

---

### 8. Testing Issues

#### Issue: "Unit tests fail"

**Solution:**
```bash
# Run tests with detailed output
./gradlew testDebugUnitTest --info

# Run specific test
./gradlew testDebugUnitTest --tests "com.healthdietapp.viewmodel.*"
```

#### Issue: "Instrumented tests timeout"

**Solution:**
```bash
# Increase timeout in test configuration
<instrumentation
    android:name="androidx.test.runner.AndroidJUnitRunner"
    android:maxStdoutLineLength="1048576"
    android:timeout="600000" />
```

---

## Debug Tips

### Enable Logging
```kotlin
// In RetrofitClient.kt
val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY  // Verbose logging
}
```

### Use Logcat Filters
```bash
# Filter by tag
adb logcat | grep "ViewModel\|Repository\|API"

# Save to file
adb logcat > debug.log

# Follow mode
adb logcat -f output.txt
```

### Check Device Storage
```bash
# View app data
adb shell run-as com.healthdietapp cat /data/data/com.healthdietapp/shared_prefs/health_diet_prefs.xml

# Clear app data
adb shell pm clear com.healthdietapp
```

---

## Performance Profiling

### Monitor Memory Usage
- Android Studio → Profiler (Shift+Ctrl+Alt+P)
- Watch Memory, CPU, Network tabs
- Look for memory leaks and ANRs

### Check Network Performance
- Simulate slow network:
  - Android Studio → Device Manager → Running Devices
  - Click device settings → Network Speed: Slow 3G

---

## Contact and Support

If you encounter issues not covered here:
1. Check existing GitHub issues
2. Review logcat output for error messages
3. Consult Android documentation
4. Open a new issue with detailed error logs

---

**Last Updated**: February 27, 2026

