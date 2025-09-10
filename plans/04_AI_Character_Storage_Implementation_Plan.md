# Plan for TASK-ONBOARD-03: In-Memory AI Character Storage

**Date:** 2024-07-29
**Version:** 1.0
**Author:** AI 비서

## 1. 작업 계획 개요

- `tasklist.md` 에서 정의한 **TASK-ONBOARD-03** 작업을 위한 계획서이다.
- This document outlines the plan to implement in-memory storage for the user's selected AI character during the onboarding process.
- The implementation will leverage the in-memory data structures established in **TASK-ONBOARD-01** and adhere to the project's MVVM architecture.

## 2. Referenced Documents

*   `C:/code/mobileApp/makeitso/docs/tasklist.md` (especially TASK-ONBOARD-01, TASK-ONBOARD-03)
*   `C:/code/mobileApp/makeitso/AGENT.md` (for character definitions, overall concept, and data structure examples)
*   `C:/code/mobileApp/makeitso/README.md` (for architecture context - MVVM, Hilt, DataSources)
*   `C:/code/mobileApp/makeitso/docs/specs/feature_spec.md` (specifically Feature 2-3: AI Character Selection)

## 3. Proposed Solution

### 3.1. Data Representation of AI Characters

Based on `docs/specs/feature_spec.md` (Feature 2-3), the user can select from:
1.  심장 후벼파는 욕쟁이
2.  귀에서 피나는 하이톤 잔소리 여친
3.  드라이 아이스 냉혹한 겨울공주

`AGENT.md` also refers to character IDs like `"foul_mouthed_assistant"`.

**Recommendation:** Utilize a Kotlin `enum class` for type safety, easier management, and clear representation of AI characters. This enum will include a unique ID (for potential future backend integration or logging) and a display name.

```kotlin
// Suggested location: com.example.makeitso.model.AiCharacter.kt or similar
enum class AiCharacter(val id: String, val displayName: String) {
    FOUL_MOUTHED_ASSISTANT("foul_mouthed_assistant", "심장 후벼파는 욕쟁이"),
    HIGH_TONE_NAGGER("high_tone_nagger", "귀에서 피나는 하이톤 잔소리 여친"),
    ICE_PRINCESS("ice_princess", "드라이 아이스 냉혹한 겨울공주");

    companion object {
        funfromId(id: String?): AiCharacter? {
            return values().find { it.id == id }
        }
    }
}
```

### 3.2. In-Memory Data Storage (`UserDataSource`)

As indicated in `docs/tasklist.md` (TASK-ONBOARD-01) and aligning with the MVVM architecture (`README.md`), a `UserDataSource` will be responsible for managing user profile information in memory.

**`UserProfile` Data Class:**
This data class will hold the user's goals and their selected AI character.

```kotlin
// Suggested location: com.example.makeitso.model.UserProfile.kt or inline in UserDataSource
data class UserProfile(
    val goals: List<String> = emptyList(),
    val selectedCharacter: AiCharacter? = null
)
```

**`UserDataSource` Implementation:**
This class will use `kotlinx.coroutines.flow.MutableStateFlow` to hold and expose the `UserProfile`.

```kotlin
// Suggested location: com.example.makeitso.data.UserDataSource.kt
// Ensure it's provided via Hilt as a Singleton if appropriate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataSource @Inject constructor() { // Hilt injection
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    fun updateSelectedCharacter(character: AiCharacter) {
        _userProfile.update { currentProfile ->
            currentProfile.copy(selectedCharacter = character)
        }
    }

    fun updateUserGoals(goals: List<String>) { // Already planned in TASK-ONBOARD-01
        _userProfile.update { currentProfile ->
            currentProfile.copy(goals = goals)
        }
    }
}
```

### 3.3. ViewModel Integration (`OnboardingViewModel`)

The `OnboardingViewModel` will handle the logic for the onboarding screen. It will be injected with `UserDataSource` (via Hilt) to update the character selection.

```kotlin
// Suggested location: com.example.makeitso.ui.onboarding.OnboardingViewModel.kt
import androidx.lifecycle.ViewModel
import com.example.makeitso.data.UserDataSource
import com.example.makeitso.model.AiCharacter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userDataSource: UserDataSource
) : ViewModel() {

    fun onCharacterSelected(character: AiCharacter) {
        userDataSource.updateSelectedCharacter(character)
        // Add any further logic, e.g., navigating to the next screen
        // or updating a UI state within the ViewModel.
    }
}
```

### 3.4. UI Interaction (Onboarding Screen)

The onboarding UI (likely a Jetpack Compose screen, e.g., `OnboardingScreen.kt`) will display the AI character choices. When a user makes a selection, the corresponding `AiCharacter` enum value will be passed to the `OnboardingViewModel`'s `onCharacterSelected` method.

## 4. Detailed Implementation Steps

1.  **Define `AiCharacter.kt` Enum:**
    *   Create the `AiCharacter.kt` file (package com.example.makeitso.data.model).
    *   Implement the `enum class AiCharacter` as detailed in section 3.1.

2.  **Define/Update `UserProfile.kt` Data Class:**
    *   Create or update the `UserProfile.kt` file (package com.example.makeitso.data.model)
    *   Implement the `data class UserProfile` as detailed in section 3.2.

3.  **Implement `UserDataSource.kt`:**
    *   Create/Update `UserDataSource.kt` (e.g., in a `data` or `data/local` package).
    *   Implement the class with `MutableStateFlow` and the `updateSelectedCharacter` function as per section 3.2.
    *   Ensure it is injectable via Hilt (e.g., annotate with `@Singleton` and `@Inject constructor()`).

4.  **Implement `OnboardingViewModel.kt`:**
    *   Create `OnboardingViewModel.kt` (e.g., in a `ui/onboarding` package).
    *   Annotate with `@HiltViewModel` and inject `UserDataSource`.
    *   Implement the `onCharacterSelected` function as per section 3.3.

5.  **Connect UI to ViewModel:**
    *   In the Onboarding Composable screen (e.g., `OnboardingScreen.kt`):
        *   Obtain an instance of `OnboardingViewModel` (e.g., using `hiltViewModel()`).
        *   Implement UI elements (e.g., Buttons, RadioGroup) for character selection.
        *   On user selection, call `viewModel.onCharacterSelected(selectedAiCharacterEnumInstance)`.

## 5. Testing Considerations

*   **Unit Tests:**
    *   Test `UserDataSource.updateSelectedCharacter` to ensure `userProfile` StateFlow emits the correct updated state.
    *   Test `OnboardingViewModel.onCharacterSelected` to verify it correctly calls the `UserDataSource` method.
*   **UI Tests (Espresso/Compose UI Tests):**
    *   Simulate user interaction on the onboarding screen (selecting a character).
    *   Verify that the `UserDataSource.userProfile` flow reflects the change, or that subsequent UI behavior dependent on the character selection is correct.
*   **Manual Testing:**
    *   Navigate through the onboarding flow, select a character.
    *   If possible at this stage, add debug logs or a temporary UI element to display the selected character from `UserDataSource` to confirm it's stored.

