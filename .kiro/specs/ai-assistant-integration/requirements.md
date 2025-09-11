# Requirements Document

## Introduction

이 문서는 기존 "Make It So" Firebase 연동 TODO 안드로이드 앱에 "심장 후벼파는 욕쟁이 AI 비서" 기능을 통합하는 요구사항을 정의합니다. 이 기능은 사용자의 할 일 목록과 개인 목표를 기반으로 선택된 AI 캐릭터가 개성 있는 잔소리를 제공하여 사용자의 동기부여와 재미를 높이는 것을 목표로 합니다.

## Development Phases

개발은 3단계로 구분하여 진행합니다:

**Phase 1**: Local in-memory 데이터 관리 및 미리 저장된 텍스트 기반 AI 응답
- Firebase 연동 없이 로컬 메모리에서 데이터 관리
- LLM API 연동 없이 미리 준비된 캐릭터별 응답 텍스트 표시

**Phase 2**: 기능 개선 및 사용자 경험 향상
- 로컬 데이터 지속성 개선 (SharedPreferences 활용)
- TODO 추가 시 자동 AI Nudge 기능
- 과거 AI 메시지 기록 보기 화면 추가
- 프롬프트 보기 및 복사 기능
- 개선된 프롬프트 생성 시스템

**Phase 3**: Firebase 연동 및 실제 LLM API 연동
- Firebase Firestore를 통한 실시간 데이터 동기화
- Gemini LLM API를 통한 동적 AI 응답 생성

## Requirements

### Requirement 1: 사용자 인증 및 세션 관리

**User Story:** As a user, I want to securely authenticate and maintain my login session, so that I can access my personalized AI assistant and todo data.

#### Acceptance Criteria

1. WHEN a new user opens the app for the first time THEN the system SHALL provide email/password and Google account registration options
2. WHEN a user completes registration THEN the system SHALL authenticate and maintain login session for 4 weeks
3. WHEN a returning user opens the app AND session is valid (within 4 weeks) THEN the system SHALL automatically log in without credentials
4. WHEN a user's session expires (after 4 weeks) THEN the system SHALL redirect to login screen and require re-authentication
5. WHEN a user chooses to logout THEN the system SHALL clear the session and redirect to login screen

### Requirement 2: 온보딩 및 초기 설정

**User Story:** As a user, I want to set up my personal goals and choose an AI character through settings, so that I can receive personalized assistance tailored to my preferences.

#### Acceptance Criteria

1. WHEN a user taps the settings icon (located in top-right corner) THEN the system SHALL provide goal and character configuration options
2. WHEN a user is setting goals THEN the system SHALL require exactly 2 goals: one short-term goal and one long-term goal as text input
3. WHEN a user is selecting AI character THEN the system SHALL provide exactly 3 options: "심장 후벼파는 욕쟁이", "귀에서 피나는 하이톤 잔소리 여친", "드라이 아이스 냉혹한 겨울공주"
4. WHEN a user has incomplete settings (missing goals or character) THEN the system SHALL redirect to settings screen and prevent access to main screen
5. WHEN a user completes goal and character setup THEN the system SHALL save the information to user profile and allow navigation to main screen

### Requirement 3: 할 일(Todo) 관리 개선

**User Story:** As a user, I want to manage my todo items with additional metadata like deadlines and priorities, so that I can better organize my tasks for AI analysis.

#### Acceptance Criteria

1. WHEN a user views the main screen THEN the system SHALL display all todo items with visual distinction between completed and incomplete items
2. WHEN a user creates a new todo item THEN the system SHALL require task content, deadline selection (24시간 내 or 1주일 내), priority level, and optional flag indicator
3. WHEN a user sets a flag on a todo item THEN the system SHALL display flag indicator to show the item is in progress
4. WHEN a user marks a todo as complete THEN the system SHALL update the status with visual indicators (strikethrough) but keep the item in the list
4. WHEN a user completes a todo item THEN the system SHALL record completion timestamp for AI analysis
6. WHEN todo data changes THEN the system SHALL sync with Firebase Firestore in real-time

### Requirement 4: AI 잔소리 기능 핵심

**User Story:** As a user, I want to receive personalized nagging messages from my chosen AI character based on my goals and todo progress, so that I stay motivated and entertained.

#### Acceptance Criteria

1. WHEN a user clicks the "AI 비서 건들기" button on main screen THEN the system SHALL generate and display AI nagging message in modal dialog
2. WHEN a user creates a new todo item THEN the system SHALL automatically trigger AI nagging message generation and display in modal dialog
3. WHEN AI nagging is triggered THEN the system SHALL send user goals, complete todo history, and selected character to Firebase Cloud Function
4. WHEN Cloud Function receives the request THEN it SHALL generate character-specific prompt based on user goals, todo items, and selected character
5. WHEN prompt is generated THEN the system SHALL store the prompt in Firestore for audit and debugging purposes
6. WHEN prompt is ready THEN the system SHALL call Gemini LLM API with the generated prompt
7. WHEN LLM returns response THEN the system SHALL display the nagging message in modal format with dismiss option
8. WHEN AI generates nagging THEN both the prompt and response message SHALL be stored in Firestore for history tracking

### Requirement 5: 설정 및 과거 기록 관리

**User Story:** As a user, I want to view my historical data and modify my settings, so that I can track my progress and adjust my AI assistant preferences.

#### Acceptance Criteria

1. WHEN a user taps the settings icon (located in top-right corner) THEN the system SHALL provide navigation to history view and settings modification
2. WHEN a user views history THEN the system SHALL provide toggle options for "완료된 할 일 포함하여 보기" and "과거 잔소리 메시지 함께 보기"
3. WHEN history display options are changed THEN the system SHALL immediately update the view according to selected filters
4. WHEN a user wants to modify goals THEN the system SHALL allow editing of short-term and long-term goals separately
5. WHEN a user wants to change AI character THEN the system SHALL allow selection from the 3 available character options
6. WHEN a user wants to logout THEN the system SHALL provide logout option in settings and clear session upon confirmation
7. WHEN settings are modified THEN the system SHALL update user profile in Firestore and apply changes immediately

### Requirement 6: 자동 AI Nudge 및 TODO 연동 (Phase 2)

**User Story:** As a user, I want the AI assistant to automatically provide feedback when I add new todo items, so that I receive immediate motivation and guidance.

#### Acceptance Criteria

1. WHEN a user creates a new todo item THEN the system SHALL automatically trigger AI nudge generation without manual button click
2. WHEN AI nudge is auto-triggered THEN the system SHALL display the message in modal dialog immediately after todo creation
3. WHEN todo item is added THEN the system SHALL include the new item in AI prompt generation context
4. WHEN auto-nudge is displayed THEN the user SHALL be able to dismiss it with confirmation button
5. WHEN multiple todos are added quickly THEN the system SHALL queue AI responses appropriately

### Requirement 7: 과거 AI 메시지 기록 관리 (Phase 2)

**User Story:** As a user, I want to view my past AI assistant messages, so that I can review previous advice and track my interaction history.

#### Acceptance Criteria

1. WHEN a user selects "과거 기록 보기" in settings THEN the system SHALL navigate to message history screen
2. WHEN message history screen loads THEN the system SHALL display all past AI messages in chronological order (newest first)
3. WHEN displaying message history THEN each entry SHALL show message content, timestamp, and trigger type (manual/auto)
4. WHEN message history is empty THEN the system SHALL display appropriate empty state message
5. WHEN user navigates back from history THEN the system SHALL return to settings screen

### Requirement 8: 프롬프트 보기 및 복사 기능 (Phase 2)

**User Story:** As a user, I want to see and copy the AI prompts used to generate responses, so that I can understand how the AI assistant works and reuse prompts elsewhere.

#### Acceptance Criteria

1. WHEN AI nudge modal is displayed THEN the system SHALL show "확인+" button in bottom-left corner alongside "확인" button
2. WHEN user clicks "확인+" button THEN the system SHALL display the generated prompt in a new modal dialog
3. WHEN prompt modal is displayed THEN the system SHALL show "copy" button in bottom-left and "확인" button in bottom-right
4. WHEN user clicks "copy" button THEN the system SHALL copy prompt content to device clipboard
5. WHEN user clicks "확인" in prompt modal THEN the system SHALL dismiss the prompt modal
6. WHEN clipboard copy succeeds THEN the system SHALL provide visual feedback (toast or snackbar)

### Requirement 9: 개선된 프롬프트 생성 시스템 (Phase 2)

**User Story:** As a system, I need to generate comprehensive prompts that properly combine user goals, todo items, and character personas, so that AI responses are more contextual and relevant.

#### Acceptance Criteria

1. WHEN generating AI prompt THEN the system SHALL include user's short-term and long-term goals in context
2. WHEN generating AI prompt THEN the system SHALL include complete todo list with completion status, priorities, and deadlines
3. WHEN generating AI prompt THEN the system SHALL include selected character persona and speaking style
4. WHEN generating AI prompt THEN the system SHALL include trigger context (manual button click vs auto todo creation)
5. WHEN prompt is generated THEN the system SHALL format it as structured text with clear sections for goals, todos, and character instructions

### Requirement 10: 로그인 상태 지속성 개선 (Phase 2)

**User Story:** As a user, I want my login session to persist for 4 weeks as intended, so that I don't need to repeatedly sign in during normal usage.

#### Acceptance Criteria

1. WHEN user successfully logs in THEN the system SHALL store login timestamp in persistent storage
2. WHEN app starts THEN the system SHALL check if current time is within 4 weeks of last login
3. WHEN login session is valid THEN the system SHALL automatically authenticate user without credentials
4. WHEN login session expires (after 4 weeks) THEN the system SHALL redirect to sign-in screen
5. WHEN user manually logs out THEN the system SHALL clear stored login timestamp

### Requirement 11: Firebase 및 LLM 연동 (Phase 3)

**User Story:** As a system, I need to integrate with Firebase services and Gemini LLM API, so that user data is securely stored and AI responses are generated reliably.

#### Acceptance Criteria

1. WHEN user data needs to be stored THEN the system SHALL use Firestore with structure: `/users/{userId}` containing goals, selectedCharacter, and todos subcollection
2. WHEN AI nagging is requested THEN the system SHALL trigger Firebase Cloud Function with user context data
3. WHEN Cloud Function processes request THEN it SHALL construct character-specific prompt and call Gemini API securely
4. WHEN LLM API is called THEN the system SHALL handle rate limits (60 requests/minute) and error responses gracefully
5. WHEN nagging messages are generated THEN both prompts and responses SHALL be stored in `/users/{userId}/messages` with timestamp and context data for history retrieval
6. IF API calls fail THEN the system SHALL provide fallback messages and retry mechanisms