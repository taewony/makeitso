# Make It So: Firebase 연동 TODO 안드로이드 앱 설계

## 1. 개요

이 문서는 'Make It So' TODO 애플리케이션의 기술적인 설계에 대해 설명합니다. 이 앱은 사용자가 할 일 목록과 항목을 관리할 수 있도록 도와주며, 모든 데이터는 Firebase를 통해 실시간으로 동기화됩니다.

## 2. 주요 기술 스택

*   **언어:** Kotlin
*   **UI:** Jetpack Compose
*   **백엔드:** Firebase (Authentication, Firestore, Crashlytics, Analytics)
*   **아키텍처:** MVVM (Model-View-ViewModel)
*   **의존성 주입:** Hilt
*   **비동기 처리:** Kotlin Coroutines & Flow

## 3. 애플리케이션 아키텍처

UI 로직과 비즈니스 로직을 분리하여 유지보수성과 테스트 용이성을 높이기 위해 MVVM (Model-View-ViewModel) 아키텍처 패턴을 채택했습니다.

*   **View (UI Layer):** Jetpack Compose로 구현된 화면들입니다. 사용자의 입력을 받고 ViewModel의 데이터를 관찰(observe)하여 UI를 갱신하는 역할만 담당합니다. (`HomeScreen`, `SettingsScreen` 등)
*   **ViewModel:** UI에 표시될 데이터를 관리하고, 사용자의 액션에 따른 비즈니스 로직을 처리합니다. 이를 위해 데이터가 필요할 경우 Repository에 요청합니다. (`HomeViewModel`, `SettingsViewModel` 등)
*   **Model (Data Layer):** 앱의 데이터 처리를 담당하는 계층입니다. Repository, DataSource, 데이터 모델로 구성됩니다.

## 4. 계층별 상세 설계

### 4.1. 데이터 계층 (Data Layer)

*   **Repository 패턴:** 데이터 소스에 대한 추상화를 제공합니다. ViewModel은 구체적인 데이터 출처(네트워크, 로컬 DB 등)를 알 필요 없이 Repository와 상호작용합니다.
    *   `AuthRepository`: 인증 관련 로직 처리
    *   `TodoListRepository`: 할 일 목록 데이터 처리
    *   `TodoItemRepository`: 할 일 항목 데이터 처리
*   **DataSource:** Firebase와 직접 통신하여 원격 데이터를 가져오는 역할을 합니다.
    *   `AuthRemoteDataSource`: Firebase Authentication 관련 API 호출
    *   `TodoListRemoteDataSource`: Firestore에서 할 일 목록 CRUD 수행
*   **Data Models:** 앱에서 사용하는 데이터의 구조를 정의합니다.
    *   `User`: 사용자 정보
    *   `TodoList`: 할 일 목록
    *   `TodoItem`: 개별 할 일 항목

### 4.2. UI 계층 (UI Layer)

*   **Jetpack Compose:** Android의 선언형 UI 툴킷인 Jetpack Compose를 사용하여 전체 UI를 구축했습니다. 이를 통해 더 적은 코드로 직관적인 UI를 구현할 수 있습니다.
*   **주요 화면:**
    *   `TodoListScreen`: 전체 할 일 목록을 보여주는 메인 화면
    *   `TodoItemScreen`: 특정 할 일 목록의 항목들을 보여주는 화면
    *   `SignInScreen` / `SignUpScreen`: 로그인 및 회원가입 화면
    *   `SettingsScreen`: 설정 화면 (로그아웃, 회원탈퇴 등)
*   **내비게이션:** Jetpack Navigation Compose를 사용하여 화면 간의 이동을 안전하고 일관성 있게 관리합니다.

## 5. Firebase 연동

*   **Authentication:** 이메일/비밀번호 및 익명 로그인을 지원하여 사용자 인증을 처리합니다. 사용자의 로그인 상태를 관리하고 보안 규칙의 기반이 됩니다.
*   **Firestore:** 할 일 목록과 항목 데이터를 저장하는 실시간 NoSQL 데이터베이스로 사용됩니다. 데이터가 변경될 때마다 앱의 UI가 실시간으로 업데이트되어 사용자 경험을 향상시킵니다.
*   **Crashlytics & Analytics:** 앱의 비정상 종료를 추적하여 안정성을 높이고, 사용자 행동을 분석하여 서비스를 개선하는 데 사용됩니다.

## 6. 의존성 주입

*   **Hilt:** Android 앱의 표준 의존성 주입 라이브러리인 Hilt를 사용합니다. ViewModel에 Repository를 주입하거나, DataSource에 Firebase 인스턴스를 주입하는 등 컴포넌트 간의 결합도를 낮추고 코드의 재사용성과 테스트 용이성을 높입니다.
