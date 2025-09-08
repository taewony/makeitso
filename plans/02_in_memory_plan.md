# 계획: Firebase 연동을 In-Memory 데이터 소스로 전환

## 1. 목표

기존의 Firebase 연동(Authentication, Firestore) 부분을 앱 내의 로컬 메모리를 사용하는 방식으로 변경합니다. 이를 통해 인터넷 연결 없이도 앱의 핵심 기능이 동작하도록 수정합니다. 가장 중요한 원칙은 **기존 코드를 삭제하는 대신 주석 처리**하여 변경 사항을 최소화하고, 언제든지 다시 Firebase 연동으로 복귀할 수 있도록 하는 것입니다.

## 2. 핵심 전략

*   **변경 범위 최소화:** 모든 변경은 `data` 패키지, 특히 `datasource`와 `injection`에 집중합니다. ViewModel과 UI 계층은 수정하지 않는 것을 원칙으로 합니다.
*   **기존 코드 보존:** Firebase와 직접 통신하는 코드는 삭제하지 않고 주석(`//`)으로 처리합니다. 그 바로 아래에 In-Memory 방식으로 동작하는 코드를 추가합니다.
*   **인터페이스 유지:** Repository나 ViewModel의 기존 인터페이스는 그대로 유지되어야 합니다. DataSource의 변경이 상위 계층에 영향을 주지 않도록 합니다.

## 3. 단계별 실행 계획

### 1단계: DataSource 레이어 수정 (Firebase -> In-Memory)

각 `...RemoteDataSource.kt` 파일 내부의 실제 Firebase API 호출 부분을 주석 처리하고, 메모리 내의 컬렉션(List, Map 등)을 조작하는 코드로 대체합니다.

*   **`AuthRemoteDataSource.kt` 수정:**
    1.  클래스 내에 현재 사용자 정보를 저장할 변수를 추가합니다. (예: `private var currentUser: FirebaseUser? = null`)
    2.  `signIn`, `signOut`, `createGuestAccount` 등의 메소드 내부에서 `auth.signIn...`, `auth.signOut()` 등 Firebase 호출 코드를 주석 처리합니다.
    3.  주석 처리된 코드 아래에, 위에서 만든 `currentUser` 변수의 상태를 변경하는 코드를 작성하여 로그인/로그아웃을 흉내 냅니다.

*   **`TodoListRemoteDataSource.kt` / `TodoItemRemoteDataSource.kt` 수정:**
    1.  클래스 내에 할 일 목록/항목 데이터를 저장할 `MutableStateFlow` 변수를 추가합니다. (예: `private val inMemoryTodos = MutableStateFlow<List<TodoItem>>(emptyList())`)
    2.  `add`, `update`, `delete` 등의 메소드 내부에서 `firestore.collection...` 등 Firestore 호출 코드를 주석 처리합니다.
    3.  주석 처리된 코드 아래에, 위에서 만든 `MutableStateFlow`의 상태를 변경(추가, 수정, 삭제)하는 코드를 작성합니다.

### 2단계: 의존성 주입 (Hilt) 수정

Firebase 서비스(FirebaseAuth, FirebaseFirestore)가 실제로 존재하지 않아도 앱이 비정상 종료되지 않도록 Hilt 모듈을 수정해야 합니다.

*   **`FirebaseHiltModule.kt` 수정:**
    1.  `provideFirebaseAuth()`와 `provideFirestore()` 메소드에서 실제 Firebase 인스턴스(`Firebase.auth`, `Firebase.firestore`)를 반환하는 코드를 주석 처리합니다.
    2.  대신, 앱이 비정상 종료되지 않도록 Mock 객체나 `null`을 반환하도록 코드를 수정합니다. (DataSource에서 이미 Firebase를 사용하지 않으므로, 이 부분은 앱 실행을 보장하는 역할만 합니다.)

### 3단계: (선택사항) Firebase 초기화 코드 주석 처리

앱의 `Application` 클래스나 `Activity`에서 Firebase 관련 초기화 코드가 있다면, 해당 부분도 주석 처리하여 불필요한 호출을 막습니다.

### 4단계: 검증

1.  앱을 빌드하고 실행합니다.
2.  로그인/로그아웃 기능이 화면 상에서 정상적으로 동작하는 것처럼 보이는지 확인합니다.
3.  할 일 목록과 항목의 추가, 수정, 삭제(CRUD) 기능이 앱을 재시작하기 전까지는 정상적으로 동작하는지 확인합니다.
