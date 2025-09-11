# Design Document

## Overview

이 설계 문서는 기존 "Make It So" TODO 앱에 AI 비서 기능을 통합하는 기술적 접근 방법을 정의합니다. 현재 앱은 MVVM 아키텍처, Jetpack Compose UI, Firebase 백엔드를 사용하고 있으며, 기본적인 AI Nudge 기능이 이미 구현되어 있습니다.

## Architecture

### Current Architecture Analysis

기존 앱은 다음과 같은 구조를 가지고 있습니다:

```
app/src/main/java/com/example/makeitso/
├── data/
│   ├── datasource/          # Firebase 데이터 소스
│   ├── model/              # 데이터 모델 (TodoItem, User, Priority 등)
│   ├── repository/         # 데이터 접근 계층
│   └── injection/          # Hilt 의존성 주입
├── ui/
│   ├── todolist/          # 메인 TODO 리스트 화면 (AI Nudge 버튼 포함)
│   ├── todoitem/          # TODO 항목 편집 화면
│   ├── settings/          # 설정 화면
│   ├── signin/signup/     # 인증 화면
│   └── shared/            # 공통 UI 컴포넌트
└── MainActivity.kt         # 네비게이션 설정
```

### Enhanced Architecture for AI Integration

AI 비서 기능을 위한 새로운 컴포넌트들:

```
app/src/main/java/com/example/makeitso/
├── data/
│   ├── model/
│   │   ├── AiCharacter.kt          # AI 캐릭터 enum
│   │   ├── UserGoals.kt            # 사용자 목표 데이터 모델
│   │   ├── AiMessage.kt            # AI 메시지 데이터 모델
│   │   └── AiPrompt.kt             # AI 프롬프트 데이터 모델
│   ├── repository/
│   │   ├── UserProfileRepository.kt # 사용자 프로필 관리
│   │   └── AiAssistantRepository.kt # AI 비서 기능 관리
│   └── datasource/
│       ├── UserProfileDataSource.kt
│       └── AiAssistantDataSource.kt
├── ui/
│   ├── onboarding/                 # 초기 설정 화면
│   ├── aiassistant/               # AI 비서 관련 UI
│   ├── messagehistory/            # Phase 2: 과거 메시지 기록 화면
│   └── settings/                  # 확장된 설정 화면
└── service/
    ├── AiPromptService.kt         # 프롬프트 생성 서비스
    └── SessionManager.kt          # Phase 2: 로그인 세션 관리
```
## 
Components and Interfaces

### 1. Data Models

#### Enhanced TodoItem
```kotlin
data class TodoItem(
    @DocumentId val id: String = "",
    val title: String = "",
    val priority: Int = Priority.NONE.value,
    val completed: Boolean = false,
    val flagged: Boolean = false,        // 기존 구현됨
    val deadline: Deadline = Deadline.NONE, // 새로 추가
    val createdAt: Timestamp = Timestamp.now(),
    val completedAt: Timestamp? = null,
    val ownerId: String = ""
)

enum class Deadline(val displayName: String, val hours: Int) {
    NONE("설정 안함", 0),
    WITHIN_24H("24시간 내", 24),
    WITHIN_WEEK("1주일 내", 168)
}
```

#### New Data Models
```kotlin
data class UserGoals(
    val shortTermGoal: String = "",
    val longTermGoal: String = ""
)

enum class AiCharacter(val displayName: String, val promptPersona: String) {
    HARSH_CRITIC("심장 후벼파는 욕쟁이", "직설적이고 심장을 후벼파는 욕쟁이"),
    NAGGING_GIRLFRIEND("귀에서 피나는 하이톤 잔소리 여친", "하이톤으로 잔소리하는 여친"),
    COLD_PRINCESS("드라이 아이스 냉혹한 겨울공주", "차갑고 냉혹한 겨울공주")
}

data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val goals: UserGoals = UserGoals(),
    val selectedCharacter: AiCharacter = AiCharacter.HARSH_CRITIC,
    val isOnboardingComplete: Boolean = false
)

data class AiMessage(
    @DocumentId val id: String = "",
    val userId: String = "",
    val prompt: String = "",
    val response: String = "",
    val character: AiCharacter = AiCharacter.HARSH_CRITIC,
    val createdAt: Timestamp = Timestamp.now(),
    val triggerType: TriggerType = TriggerType.MANUAL
)

enum class TriggerType {
    MANUAL,      // 사용자가 버튼 클릭
    AUTO_CREATE  // TODO 생성 시 자동
}

// Phase 2 추가 데이터 모델
data class LoginSession(
    val userId: String = "",
    val loginTimestamp: Long = 0L,
    val expiryTimestamp: Long = 0L
)

data class MessageHistoryItem(
    val id: String = "",
    val message: AiMessage,
    val displayTimestamp: String = ""
)
```

### 2. Repository Layer

#### UserProfileRepository
```kotlin
interface UserProfileRepository {
    suspend fun getUserProfile(userId: String): UserProfile?
    suspend fun createUserProfile(profile: UserProfile): String
    suspend fun updateUserProfile(profile: UserProfile)
    fun getUserProfileFlow(userId: String): Flow<UserProfile?>
}
```

#### AiAssistantRepository
```kotlin
interface AiAssistantRepository {
    suspend fun generateAiResponse(
        goals: UserGoals,
        todoItems: List<TodoItem>,
        character: AiCharacter,
        triggerType: TriggerType
    ): AiMessage
    
    suspend fun saveAiMessage(message: AiMessage)
    fun getAiMessagesFlow(userId: String): Flow<List<AiMessage>>
}
```

### 3. Service Layer

#### AiPromptService (Enhanced for Phase 2)
```kotlin
class AiPromptService {
    fun generatePrompt(
        goals: UserGoals,
        todoItems: List<TodoItem>,
        character: AiCharacter,
        triggerType: TriggerType
    ): String {
        // Phase 2: 구조화된 프롬프트 생성
        return buildStructuredPrompt(goals, todoItems, character, triggerType)
    }
    
    private fun buildStructuredPrompt(
        goals: UserGoals,
        todoItems: List<TodoItem>,
        character: AiCharacter,
        triggerType: TriggerType
    ): String {
        // 목표, TODO 리스트, 캐릭터 페르소나를 체계적으로 조합
    }
    
    fun generateResponse(
        goals: UserGoals,
        todoItems: List<TodoItem>,
        character: AiCharacter,
        triggerType: TriggerType
    ): String {
        // 캐릭터별 미리 정의된 응답 반환
    }
}

// Phase 2 추가 서비스
class SessionManager {
    suspend fun saveLoginSession(userId: String)
    suspend fun isSessionValid(): Boolean
    suspend fun clearSession()
    fun getSessionExpiryTime(): Long
}
```

#### Phase 2 UI Components
```kotlin
// 과거 메시지 기록 화면
@Composable
fun MessageHistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: MessageHistoryViewModel = hiltViewModel()
)

// 프롬프트 보기 모달
@Composable
fun PromptViewDialog(
    prompt: String,
    onDismiss: () -> Unit,
    onCopyToClipboard: (String) -> Unit
)

// 개선된 AI Nudge 모달
@Composable
fun EnhancedAiNudgeDialog(
    message: String,
    prompt: String,
    onDismiss: () -> Unit,
    onShowPrompt: () -> Unit
)
```## Data
 Models

### Firebase Firestore Structure

```
/users/{userId}
├── profile/
│   ├── goals: { shortTermGoal: string, longTermGoal: string }
│   ├── selectedCharacter: string
│   └── isOnboardingComplete: boolean
├── todos/
│   └── {todoId}/
│       ├── title: string
│       ├── priority: number
│       ├── completed: boolean
│       ├── flagged: boolean
│       ├── deadline: string
│       ├── createdAt: timestamp
│       └── completedAt: timestamp?
└── aiMessages/
    └── {messageId}/
        ├── prompt: string
        ├── response: string
        ├── character: string
        ├── triggerType: string
        └── createdAt: timestamp
```

### Phase 1 vs Phase 2 vs Phase 3 Data Handling

**Phase 1 (Local In-Memory)**:
- `UserProfileLocalDataSource`: SharedPreferences 사용
- `AiAssistantLocalDataSource`: 메모리 기반 저장
- 미리 정의된 응답 템플릿 사용

**Phase 2 (Enhanced Local Features)**:
- `UserProfileLocalDataSource`: SharedPreferences 개선 (로그인 지속성)
- `AiAssistantLocalDataSource`: 메모리 + SharedPreferences 하이브리드
- 개선된 프롬프트 생성 시스템
- 자동 AI Nudge 트리거
- 과거 메시지 기록 관리

**Phase 3 (Firebase + LLM)**:
- `UserProfileRemoteDataSource`: Firestore 연동
- `AiAssistantRemoteDataSource`: Cloud Functions + Firestore
- Gemini API를 통한 동적 응답 생성

## Error Handling

### 1. Authentication Errors
- 세션 만료 시 자동 재로그인 시도
- 실패 시 로그인 화면으로 리다이렉트

### 2. AI Service Errors
```kotlin
sealed class AiServiceError : Exception() {
    object NetworkError : AiServiceError()
    object RateLimitExceeded : AiServiceError()
    object InvalidPrompt : AiServiceError()
    data class ApiError(val code: Int, val message: String) : AiServiceError()
}
```

### 3. Fallback Mechanisms
- LLM API 실패 시 미리 정의된 응답 사용
- 네트워크 오류 시 로컬 캐시된 데이터 활용
- 사용자에게 명확한 오류 메시지 표시## Tes
ting Strategy

### 1. Unit Tests
- `AiPromptService` 프롬프트 생성 로직
- Repository 계층의 데이터 변환 로직
- ViewModel의 상태 관리 로직

### 2. Integration Tests
- Firebase 연동 테스트 (Phase 2)
- LLM API 연동 테스트 (Phase 2)
- 사용자 플로우 통합 테스트

### 3. UI Tests
- 온보딩 플로우 테스트
- AI 응답 모달 표시 테스트
- 설정 변경 플로우 테스트

### 4. Phase-specific Testing
**Phase 1** ✅:
- 로컬 데이터 저장/로드 테스트
- 미리 정의된 응답 생성 테스트

**Phase 2**:
- 자동 AI Nudge 트리거 테스트
- 과거 메시지 기록 저장/조회 테스트
- 프롬프트 생성 및 복사 기능 테스트
- 로그인 세션 지속성 테스트
- 한글 입력 및 표시 테스트

**Phase 3**:
- Firebase 실시간 동기화 테스트
- Cloud Functions 트리거 테스트
- LLM API 응답 처리 테스트

## Implementation Phases

### Phase 1: Local Implementation ✅ (완료)
1. 새로운 데이터 모델 생성
2. 로컬 데이터 소스 구현
3. 온보딩 화면 구현
4. 설정 화면 확장
5. AI 응답 모달 개선
6. 미리 정의된 응답 시스템

### Phase 2: Enhanced Features & UX
1. 자동 AI Nudge 트리거 구현
2. 과거 메시지 기록 화면 개발
3. 프롬프트 보기 및 복사 기능
4. 개선된 프롬프트 생성 시스템
5. 로그인 세션 지속성 개선
6. 한글 입력 지원 확인 및 테스트

### Phase 3: Firebase + LLM Integration
1. Firebase 데이터 소스 구현
2. Cloud Functions 개발
3. Gemini API 연동
4. 실시간 데이터 동기화
5. 프롬프트 및 응답 저장 시스템
6. 성능 최적화 및 오류 처리

## Security Considerations

### 1. API Key Management
- Cloud Functions에서 API 키 관리
- 클라이언트에 API 키 노출 방지

### 2. User Data Protection
- 사용자 목표 및 TODO 데이터 암호화
- Firebase Security Rules 적용

### 3. Rate Limiting
- LLM API 호출 제한 (60회/분)
- 사용자별 요청 빈도 제한

## Performance Optimization

### 1. Caching Strategy
- AI 응답 로컬 캐시
- 사용자 프로필 메모리 캐시
- 이미지 및 정적 리소스 캐시

### 2. Network Optimization
- 배치 요청으로 API 호출 최소화
- 압축된 데이터 전송
- 오프라인 모드 지원

### 3. UI Performance
- LazyColumn 최적화
- 상태 관리 최적화
- 불필요한 리컴포지션 방지