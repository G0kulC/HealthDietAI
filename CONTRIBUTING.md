# Contributing to HealthDietAI

Thank you for your interest in contributing to HealthDietAI! This document outlines our development practices and guidelines.

## Code Style Guidelines

### Kotlin Style
```kotlin
// ✅ Good: Use val instead of var when possible
val immutableValue = 10
var mutableValue = 20

// ✅ Good: Use meaningful variable names
val userAge = 25
val healthMetrics = hashMapOf(...)

// ✅ Good: Use extension functions for utility code
fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

// ❌ Avoid: Single-letter variables (except for loops)
val a = 10  // Bad
val b = 20  // Bad

// ❌ Avoid: Hungarian notation
val strUserName = "John"  // Bad
val userName = "John"  // Good
```

### Class Naming
```kotlin
// Activities: ActivityName
class MainActivity : AppCompatActivity()

// Fragments: NameFragment
class LoginFragment : Fragment()

// ViewModels: NameViewModel
class AuthViewModel : ViewModel()

// Repositories: NameRepository
class AuthRepository @Inject constructor(...)

// Adapters: NameAdapter
class FoodAdapter : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>()
```

### File Organization
- One public class per file (exceptions allowed for data classes)
- Related private classes can share a file
- Keep files focused and under 300 lines when possible

## Git Workflow

### Branch Naming
```
feature/feature-name          # New features
bugfix/bug-description        # Bug fixes
refactor/component-name       # Code refactoring
docs/documentation-topic      # Documentation
```

### Commit Messages
```
# Good commit messages:
- "feat: Add food recommendation filtering by calorie range"
- "fix: Resolve crash when token expires"
- "refactor: Extract TokenManager into separate module"
- "docs: Update API documentation"

# Avoid:
- "fix stuff"
- "made changes"
- "wip"
```

## Code Review Checklist

Before submitting a PR, ensure:

- [ ] Code follows Kotlin style guide
- [ ] No unused imports or variables
- [ ] Error handling is present for API calls
- [ ] Null safety is properly handled
- [ ] No hardcoded strings (use resources)
- [ ] Comments explain complex logic
- [ ] Unit tests are added for new features
- [ ] No breaking API changes
- [ ] Gradle build passes: `./gradlew build`

## Testing

### Unit Tests
```kotlin
class AuthViewModelTest {
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    @Test
    fun testLoginSuccess() {
        // Arrange
        val viewModel = AuthViewModel(mockRepository)
        
        // Act
        viewModel.login("test@example.com", "password")
        
        // Assert
        assertEquals(NetworkResult.Loading::class, viewModel.loginState.value!!::class)
    }
}
```

### Running Tests
```bash
# Unit tests
./gradlew testDebugUnitTest

# Instrumented tests
./gradlew connectedAndroidTest

# All tests
./gradlew test
```

## Performance Guidelines

1. **Avoid memory leaks**
   - Use weak references for context
   - Unsubscribe from observers
   - Cancel coroutines properly

2. **Efficient RecyclerView usage**
   - Reuse ViewHolders
   - Use DiffUtil for list updates
   - Avoid blocking operations in bind

3. **Network optimization**
   - Cache responses appropriately
   - Use pagination for large lists
   - Implement request throttling

4. **Minimize layouts inflation**
   - Use ViewBinding instead of findViewById
   - Prefer merge tags for layout reuse
   - Keep layout hierarchy shallow

## Documentation

### Comments
```kotlin
/**
 * Logs in the user with provided credentials.
 * 
 * @param email User email address
 * @param password User password
 * @return Coroutine Job for cancellation
 */
fun login(email: String, password: String): Job {
    // Implementation
}
```

### README Updates
Update README.md when:
- Adding new features
- Changing API endpoints
- Modifying project structure
- Updating dependencies

## Security Considerations

1. **Never commit sensitive data**
   - API keys, tokens, passwords
   - Use environment variables
   - Add secrets to .gitignore

2. **API Security**
   - Use HTTPS only in production
   - Implement certificate pinning
   - Validate SSL certificates

3. **Data Storage**
   - Use encrypted SharedPreferences for tokens
   - Consider Room database for sensitive data
   - Avoid storing PII in logs

4. **Input Validation**
   ```kotlin
   // Always validate user input
   if (email.isEmpty() || !email.contains("@")) {
       return NetworkResult.Error("Invalid email format")
   }
   ```

## Adding New Features

### Step 1: Create Feature Branch
```bash
git checkout -b feature/new-feature
```

### Step 2: Update Constants (if needed)
```kotlin
// constants.kt
const val NEW_ENDPOINT = "api/v1/new-endpoint"
```

### Step 3: Create/Update Data Models
```kotlin
// data/model/NewModel.kt
data class NewModel(
    val id: Int,
    val name: String
)
```

### Step 4: Create Repository
```kotlin
// data/repository/NewRepository.kt
@Singleton
class NewRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun fetchData(): NetworkResult<NewModel> {
        // Implementation
    }
}
```

### Step 5: Create ViewModel
```kotlin
// viewmodel/NewViewModel.kt
@HiltViewModel
class NewViewModel @Inject constructor(
    private val repository: NewRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow<NetworkResult<NewModel>?>(null)
    val state: StateFlow<NetworkResult<NewModel>?> = _state
    
    fun loadData() {
        viewModelScope.launch {
            _state.value = repository.fetchData()
        }
    }
}
```

### Step 6: Create UI Layer
```kotlin
// ui/new/NewFragment.kt
@AndroidEntryPoint
class NewFragment : Fragment() {
    private val viewModel: NewViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        lifecycleScope.launch {
            viewModel.state.collect { result ->
                when (result) {
                    is NetworkResult.Loading -> showLoading()
                    is NetworkResult.Success -> showData(result.data)
                    is NetworkResult.Error -> showError(result.message)
                    null -> {}
                }
            }
        }
    }
}
```

### Step 7: Add Tests
```kotlin
// Tests for the new feature
class NewViewModelTest {
    @Test
    fun testLoadDataSuccess() {
        // Test implementation
    }
}
```

### Step 8: Submit PR
- Push branch to remote
- Create Pull Request with description
- Address review comments
- Merge when approved

## Project Standards

### Minimum Supported Version
- **minSdk**: 26 (Android 8.0)
- **targetSdk**: 34 (Android 14)

### Dependency Updates
- Update dependencies monthly
- Check for security vulnerabilities
- Test thoroughly before merging

### Build Requirements
- Zero build warnings
- Zero Lint errors
- All tests passing

## Resources

- [Android Architecture Components](https://developer.android.com/topic/architecture)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Hilt Documentation](https://dagger.dev/hilt/)
- [Retrofit Guide](https://square.github.io/retrofit/)
- [Material Design 3](https://m3.material.io/)

## Getting Help

- Check existing issues for solutions
- Review code comments in relevant files
- Ask questions in issues with context
- Provide error logs and reproduction steps

---

**Thank you for contributing to HealthDietAI!** 🎉

