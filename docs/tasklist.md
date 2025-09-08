# "심장 후벼파는 욕쟁이 AI 비서" 개발 Task List (In-Memory 구현 기준)

이 문서는 `feature_spec.md`에 명시된 기능을 In-Memory 데이터 소스 방식으로 구현하기 위한 개발 작업 목록을 정의합니다. 모든 작업은 기존 Firebase 코드를 주석 처리하고 메모리 내 로직을 추가하는 것을 원칙으로 합니다.

---

### **Feature 1: 사용자 인증 (User Authentication)**

*   **TASK-AUTH-01:** `AuthRemoteDataSource`에 In-Memory 사용자 상태(`currentUser`)를 저장할 변수 추가
*   **TASK-AUTH-02:** 이메일/비밀번호 회원가입 및 로그인 기능 구현 (Firebase `auth.createUserWithEmail...`, `auth.signIn...` 주석 처리 후, In-Memory `currentUser` 상태 업데이트 로직 추가)
*   **TASK-AUTH-03:** 구글 계정 회원가입 및 로그인 기능 구현 (Firebase `auth.signInWithCredential` 주석 처리 후, In-Memory `currentUser` 상태 업데이트 로직 추가)
*   **TASK-AUTH-04:** 로그아웃 기능 구현 (`auth.signOut()` 주석 처리 후, In-Memory `currentUser`를 null로 설정)
*   **TASK-AUTH-05:** 세션 관리 흉내 (앱 재시작 전까지 로그인 상태가 유지되도록 `currentUser` 상태 보존)
*   **TASK-AUTH-06:** `FirebaseHiltModule`에서 `FirebaseAuth` 의존성 주입 시, 실제 인스턴스 대신 Mock 또는 null을 반환하도록 수정 (앱 크래시 방지)

### **Feature 2: 온보딩 및 초기 설정 (Onboarding & Initial Setup)**

*   **TASK-ONBOARD-01:** 사용자 프로필 정보(목표, AI 캐릭터)를 저장할 In-Memory 데이터 구조 생성 (예: `UserDataSource` 내에 `MutableStateFlow<UserProfile>`)
*   **TASK-ONBOARD-02:** 온보딩 화면 UI 개발 (목표 입력 필드 1~3개, AI 캐릭터 선택 UI)
*   **TASK-ONBOARD-03:** 온보딩 화면에서 설정한 목표와 AI 캐릭터를 In-Memory 데이터 구조에 저장하는 로직 구현
*   **TASK-ONBOARD-04:** 앱 시작 시 사용자의 프로필 정보가 비어 있는지 확인하고, 비어있을 경우 온보딩 화면으로 강제 이동시키는 로직 구현

### **Feature 3: 할 일(Todo) 관리**

*   **TASK-TODO-01:** `TodoListRemoteDataSource`에 할 일 목록을 저장할 `MutableStateFlow<List<TodoItem>>` 변수 추가
*   **TASK-TODO-02:** 할 일 추가 기능 구현 (Firestore `firestore.collection...` 호출 코드 주석 처리 후, In-Memory 리스트에 아이템 추가 로직 구현)
*   **TASK-TODO-03:** 할 일 완료 처리 기능 구현 (Firestore 업데이트 코드 주석 처리 후, In-Memory 리스트에서 해당 아이템의 `completed` 상태 변경 로직 구현)
*   **TASK-TODO-04:** 메인 화면에 할 일 목록을 표시하고, 완료된 항목은 취소선 등으로 시각적으로 구분하는 UI 로직 구현
*   **TASK-TODO-05:** `FirebaseHiltModule`에서 `FirebaseFirestore` 의존성 주입 시, 실제 인스턴스 대신 Mock 또는 null을 반환하도록 수정

### **Feature 4: AI 잔소리 기능 (Simulation)**

*   **TASK-AI-01:** AI 잔소리 메시지를 표시할 Modal 알림 창 UI 컴포넌트 개발
*   **TASK-AI-02:** 메인 화면 우측 하단에 'AI 비서 건들기' 버튼 추가 및 클릭 이벤트 핸들러 구현
*   **TASK-AI-03:** **(시뮬레이션)** 'AI 비서 건들기' 버튼 클릭 또는 할 일 추가 시, In-Memory에 저장된 목표, 할 일 목록, AI 캐릭터 정보를 조합하여 **미리 정의된 고정 텍스트(Hardcoded Text)**를 Modal 알림 창으로 표시하는 기능 구현 (실제 LLM 연동 X)

### **Feature 5: 설정 및 조회**

*   **TASK-SETTINGS-01:** 설정 화면 UI 개발 (톱니바퀴 아이콘 클릭 시 진입)
*   **TASK-SETTINGS-02:** '과거 기록 보기' 화면 UI 개발
*   **TASK-SETTINGS-03:** '과거 기록 보기' 화면에 "완료된 할 일 포함", "과거 잔소리 메시지 함께 보기" 옵션 스위치 UI 추가
*   **TASK-SETTINGS-04:** 위 스위치 상태에 따라 In-Memory 할 일 목록 데이터를 필터링하여 보여주는 로직 구현
*   **TASK-SETTINGS-05:** 설정 화면에서 사용자의 목표와 AI 캐릭터를 수정하고, In-Memory 데이터를 업데이트하는 기능 구현
