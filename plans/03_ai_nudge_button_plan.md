# TASK-AI-03: AI Nudge 기능 구현 계획

## 목표

'AI 비서 건들기' 기능을 구현합니다. 사용자가 "AI Nudge" 버튼을 클릭하면, 미리 정의된 AI 잔소리 메시지를 담은 Modal 알림 창이 나타납니다.

## BDD (Behavior-Driven Development) 테스트 시나리오

### 시나리오 1: "AI Nudge" 버튼 표시

-   **Given** 사용자가 홈 화면에 있다.
-   **Then** "AI Nudge" 버튼이 화면 우측 하단에 표시된다.

### 시나리오 2: "AI Nudge" 버튼 클릭 및 Modal 알림 창 확인

-   **Given** 사용자가 홈 화면에 있다.
-   **When** 사용자가 "AI Nudge" 버튼을 클릭한다.
-   **Then** Modal 알림 창이 나타난다.
-   **And** 알림 창에는 "AI 비서가 잔소리를 시작합니다."라는 텍스트가 표시된다.

### 시나리오 3: 알림 상태는 인메모리(In-Memory)로만 관리

-   **Given** 사용자가 "AI Nudge" 버튼을 클릭하여 Modal 알림 창을 확인했다.
-   **And** 사용자가 알림 창을 닫았다.
-   **When** 사용자가 앱을 완전히 종료 후 다시 시작한다.
-   **Then** 홈 화면이 나타났을 때 Modal 알림 창은 표시되지 않는다.

---

## 단계별 구현 계획

### 1. UI 수정: "AI Nudge" 버튼 추가

-   **파일:** `app/src/main/java/com/example/makeitso/ui/home/HomeScreen.kt`

-   **수정 방식:**
    -   복잡한 `ConstraintLayout` 방식 대신, `Scaffold`가 제공하는 `floatingActionButton` 슬롯을 활용하는 더 간단하고 표준적인 방식으로 변경합니다.
    -   두 개의 `ExtendedFloatingActionButton`을 `Row`로 감싸서 `floatingActionButton` 슬롯에 배치합니다.

-   **상세 수정 계획:**
    1.  `Scaffold`의 `floatingActionButton` 파라미터에 `Row` Composable을 추가합니다.
    2.  `Row`의 `horizontalArrangement`를 `Arrangement.SpaceBetween`으로 설정하여 두 버튼이 양쪽 끝으로 정렬되도록 합니다.
    3.  기존 "Create list" 버튼과 새로운 "AI Nudge" 버튼을 `Row` 내부에 배치합니다.
    4.  `ConstraintLayout` 관련 코드는 `LazyColumn`에서 제거하여 레이아웃을 단순화합니다.

    ```kotlin
    // 예시 코드
    Scaffold(
        floatingActionButton = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ExtendedFloatingActionButton(...) // Create List Button
                ExtendedFloatingActionButton(...) // AI Nudge Button
            }
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            // ...
        }
    }
    ```

### 2. Modal 알림 창 구현

-   **파일:** `app/src/main/java/com/example/makeitso/ui/home/HomeScreen.kt`
-   **내용:**
    -   `AlertDialog` Composable을 사용하여 알림 창을 구현합니다.
    -   `HomeViewModel`의 상태에 따라 `AlertDialog`가 표시되거나 사라지도록 합니다.
    -   Modal 창 내부에 "AI 비서가 잔소리를 시작합니다."라는 `Text`를 표시합니다.

### 3. ViewModel 수정

-   **파일:** `app/src/main/java/com/example/makeitso/ui/home/HomeViewModel.kt`
-   **내용:**
    -   Modal 알림 창의 표시 여부를 제어하는 `StateFlow<Boolean>`를 추가합니다. (예: `showAiNudgeDialog`)
    -   "AI Nudge" 버튼 클릭 시 호출될 함수를 추가하여 `showAiNudgeDialog`의 값을 `true`로 변경합니다.
    -   Modal이 닫힐 때 `showAiNudgeDialog` 값을 `false`로 변경하는 함수도 추가합니다.

## 예상 수정 파일

-   `app/src/main/java/com/example/makeitso/ui/home/HomeScreen.kt`
-   `app/src/main/java/com/example/makeitso/ui/home/HomeViewModel.kt`