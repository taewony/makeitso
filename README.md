## Make it So

Source: https://github.com/FirebaseExtended/make-it-so-android/


This is the source code for Make It So, a sample app that demonstrates how to use Firebase in an Android app. 

Version 2 of this app was built in 2025 and you can find it in the v2 folder - you will also find instructions to set up your Firebase project and connect it to the Make it So app. This version implements a new architecture, based on the latest recommendations outlined in the [Android Guide to App Architecture documentation](https://developer.android.com/topic/architecture). Similarly, the UI was developed in accordance with the best practices described in [Android's "Develop UI for Android" documentation](https://developer.android.com/develop/ui).

# Make It So: Firebase 연동 TODO 안드로이드 앱

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

* Firebase 본격 연동 전에, App data를 SQLite를 사용하여 저장하면, 앱 재빌드 시에도 데이터가 유지되어 개발 중 매우 편리합니다. 특히 사용자 설정, 온보딩 상태, AI 메시지 히스토리 등을 테스트할 때 매번 다시 입력할 필요가 없어 개발 효율성이 크게 향상됩니다! 1단계는 in-memory에 2단계는 SQLite local DB에 3단계는 Firebase Cloud에 각각 데이터를 저장합니다.

## 6. 의존성 주입

*   **Hilt:** Android 앱의 표준 의존성 주입 라이브러리인 Hilt를 사용합니다. ViewModel에 Repository를 주입하거나, DataSource에 Firebase 인스턴스를 주입하는 등 컴포넌트 간의 결합도를 낮추고 코드의 재사용성과 테스트 용이성을 높입니다.

## 7. 개발은 3단계로 구분하여 진행. (현재 2단계가지 구현함)

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

## [부록] Firebase Setting up

In order for this app to work, you will need to create a [Firebase project](https://firebase.google.com/):

1. Clone this repository
1. Create a Firebase project in the [Firebase Console](https://console.firebase.google.com/)
1. Follow [these steps](https://firebase.google.com/docs/android/setup#register-app) to register Make it So app in your Firebase project
1. Follow [these steps](https://firebase.google.com/docs/android/setup#add-config-file) to add the Firebase Android configuration file to Make it So
1. [Create a Cloud Firestore database](https://firebase.google.com/docs/firestore/quickstart#create) in your Firebase project
1. [Enable Anonymous Authentication](https://firebase.google.com/docs/auth/android/anonymous-auth#before-you-begin) in your Firebase project
1. [Enable Email/Password Authentication](https://firebase.google.com/docs/auth/android/password-auth#before_you_begin) in your Firebase project
1. Run the app using Android Studio Ladybug+ on a device/emulator with API level 23 or above
1. Create your first to-do item in the app

## [부록] Spec Driven Development Process (with Kiro)

### 🚀 스타트업을 위한 AI 기반 MVP 개발 방법론

창업을 준비하는 개발자들이 아이디어를 빠르게 검증 가능한 제품으로 만들어내는 **AI-First 개발 방법론**입니다. 이 접근법은 완벽한 제품을 만드는 것이 아니라, **빠르게 시장에서 학습하고 개선하는 사이클**을 구축하는 데 초점을 맞춥니다.

### 🎯 핵심 철학: "Document-Driven AI Collaboration"

전통적인 개발에서는 코드가 진실의 원천이었지만, AI 시대에는 **명확한 의도와 요구사항이 담긴 문서**가 모든 개발 활동의 중심이 됩니다. AI는 이 문서를 기반으로 일관성 있는 코드를 생성하고, 개발자는 제품의 방향성과 품질을 관리하는 역할로 진화합니다.

### 📋 3계층 개발 프로세스

#### **Layer 1: 제품 의도 정의 (AGENT.md + README.md)**
- **목적**: 제품의 비전, 핵심 가치, 기술적 제약사항을 명문화
- **산출물**: 
  - `AGENT.md`: AI 협업을 위한 제품 컨텍스트 (타겟 사용자, 핵심 기능, 비즈니스 로직)
  - `README.md`: 기술 스택, 아키텍처, 개발 환경 설정
- **AI 역할**: 아이디어 구체화, 시장 조사, 경쟁사 분석 지원

#### **Layer 2: 기능 명세 작성 (.kiro/specs/)**
- **목적**: 추상적인 아이디어를 구현 가능한 기능 단위로 분해
- **산출물**: 
  - `requirements.md`: EARS 형식의 요구사항 정의
  - `design.md`: 시스템 아키텍처 및 데이터 모델
  - `tasks.md`: 구현 가능한 작업 단위로 분해된 태스크 리스트
- **AI 역할**: 요구사항 자동 생성, 설계 문서 초안 작성, 태스크 분해

#### **Layer 3: 점진적 구현 (Source Code)**
- **목적**: 명세를 바탕으로 실제 동작하는 제품 구현
- **산출물**: 테스트 가능한 MVP 코드
- **AI 역할**: 코드 생성, 리팩토링, 테스트 코드 작성, 버그 수정

### 🛠️ 지원 기술 스택

#### **웹 애플리케이션 (빠른 프로토타이핑)**
```
Frontend: Next.js + Tailwind CSS + TypeScript
Backend: Supabase (Auth, Database, Storage)
Deployment: Vercel (Frontend) + Supabase (Backend)
AI Tools: Kiro IDE, GitHub Copilot, v0.dev
```

#### **모바일 애플리케이션 (네이티브 성능)**
```
Frontend: Jetpack Compose (Android) / SwiftUI (iOS)
Backend: Firebase (Auth, Firestore, Cloud Functions)
API: FastAPI + PostgreSQL (복잡한 비즈니스 로직용)
AI Tools: Android Studio AI, Kiro IDE
```

### 🔄 개발 워크플로우

#### **Phase 1: 아이디어 검증 (1-2주)**
1. **문제 정의**: 해결하려는 실제 문제가 무엇인지 명확히 정의
2. **시장 조사**: AI를 활용한 경쟁사 분석 및 차별화 포인트 발굴
3. **MVP 범위 설정**: 핵심 가치를 전달하는 최소 기능 세트 정의
4. **기술 스택 선택**: 개발 속도와 확장성을 고려한 기술 선택

#### **Phase 2: 설계 및 명세 (1주)**
1. **AGENT.md 작성**: 제품 비전, 사용자 페르소나, 핵심 기능 정의
2. **Spec 문서 생성**: Kiro와 협업하여 요구사항, 설계, 태스크 문서 작성
3. **프로토타입 설계**: Figma 또는 코드 기반 UI 프로토타입 제작
4. **개발 계획 수립**: 우선순위 기반 개발 로드맵 작성

#### **Phase 3: 점진적 개발 (2-4주)**
1. **MVP 핵심 기능 구현**: 사용자 가치를 전달하는 최소 기능부터 시작
2. **사용자 피드백 수집**: 빠른 배포를 통한 실제 사용자 반응 확인
3. **데이터 기반 개선**: 사용 패턴 분석을 통한 기능 우선순위 재조정
4. **반복적 개선**: 피드백을 바탕으로 한 지속적인 제품 개선

### 🤖 AI 협업 모범 사례

#### **효과적인 프롬프팅**
```markdown
# 좋은 예시
"사용자 인증 기능을 구현해줘. Next.js + Supabase 환경에서 
이메일/비밀번호 로그인, 소셜 로그인(Google, GitHub), 
비밀번호 재설정 기능이 필요해. 
TypeScript와 Tailwind CSS를 사용하고, 
반응형 디자인으로 만들어줘."

# 나쁜 예시
"로그인 기능 만들어줘."
```

#### **문서 기반 컨텍스트 관리**
- AI에게 작업을 요청할 때 항상 관련 문서(AGENT.md, specs/)를 참조하도록 지시
- 코드 변경 시 관련 문서도 함께 업데이트하도록 요청
- 정기적으로 문서와 코드 간의 일관성 검증

#### **점진적 복잡도 증가**
- 단순한 기능부터 시작해서 점진적으로 복잡한 기능 추가
- 각 단계마다 테스트 가능한 상태로 유지
- 사용자 피드백을 받을 수 있는 최소 단위로 배포

### 📊 성공 지표 및 검증 방법

#### **개발 효율성 지표**
- **Time to First Deploy**: 아이디어에서 첫 배포까지의 시간 (목표: 2주 이내)
- **Feature Velocity**: 주당 구현되는 기능 수 (AI 협업으로 2-3배 향상)
- **Code Quality**: 테스트 커버리지, 코드 리뷰 통과율

#### **제품 검증 지표**
- **User Engagement**: DAU/MAU, 세션 시간, 기능 사용률
- **Product-Market Fit**: 사용자 리텐션, NPS 점수, 유료 전환율
- **Technical Debt**: 버그 발생률, 성능 지표, 확장성 평가

### 🎓 학습 리소스

#### **추천 도구 및 플랫폼**
- **AI 개발 도구**: Kiro IDE, GitHub Copilot, v0.dev, Cursor
- **프로토타이핑**: Figma, Framer, Webflow
- **배포 및 모니터링**: Vercel, Netlify, Firebase Hosting
- **사용자 피드백**: Hotjar, Mixpanel, PostHog

#### **커뮤니티 및 학습 자료**
- **개발 커뮤니티**: Indie Hackers, Product Hunt, Dev.to
- **AI 협업 가이드**: OpenAI Cookbook, Anthropic Claude Docs
- **스타트업 방법론**: Lean Startup, Design Sprint, Jobs-to-be-Done

### 💡 핵심 성공 요소

1. **명확한 문제 정의**: 기술이 아닌 사용자 문제에서 시작
2. **빠른 실험과 학습**: 완벽함보다는 속도와 학습에 집중
3. **AI와의 효과적 협업**: AI의 강점을 활용하되 인간의 판단력 유지
4. **사용자 중심 사고**: 기능이 아닌 사용자 가치 중심의 개발
5. **지속적인 개선**: 데이터와 피드백 기반의 반복적 개선

이 방법론을 통해 개발자는 기술적 구현에만 매몰되지 않고, **사용자에게 실제 가치를 전달하는 제품**을 빠르게 만들어낼 수 있습니다. AI는 단순한 코딩 도구가 아닌, **제품 개발 전 과정의 파트너**로서 활용됩니다.

* **주요 활동:**  
    
  * 테크 스택(Tech Stack) 선정: 앱의 확장성, 개발 속도, 팀의 기술 역량 등을 고려하여 개발 언어, 프레임워크, 데이터베이스 등을 결정합니다.  
  * 디자인 프로토타이핑 (Design Mode Prototype): **Figma, Adobe XD** 같은 툴을 사용해 실제 앱처럼 클릭하며 움직이는 '가짜 앱'을 만듭니다.


* **결과물:** 주요 화면에 대한 와이어프레임 및 디자인 프로토타입, 기술 아키텍처 정의서  
    
* **🤖 AI 활용 가이드:**  
    
  * **기술 스택 추천:** "**사용자 간 실시간 채팅 기능**이 중요한 소셜 앱을 만들 거야. **React Native**와 **Flutter** 중 어떤 기술이 더 적합할지 장단점을 비교 분석해 줘."  
  * **기본 명세서(Spec) 초안 작성:** "\*\*쇼핑 앱의 '상품 상세 페이지'\*\*에 들어가야 할 모든 기능적, 비기능적 요구사항을 담은 **기획 명세서(PRD, Product Requirements Document) 초안**을 작성해 줘. 이미지, 가격, 리뷰, 구매 버튼 등을 포함해서."  
  * **사용자 흐름(User Flow) 설계:** "**사용자가 회원가입 후 첫 번째 게시물을 작성하기까지의 User Flow**를 텍스트로 설명해 줘. 각 단계별로 필요한 화면과 버튼을 명시해 줘."

---

### **Phase 3: 핵심 개발과 학습의 반복 (MVP Evolution)**

가장 중요한 단계입니다. 완벽한 제품이 아닌, **고객의 핵심 문제를 해결해주는 최소한의 기능(MVP, Minimum Viable Product)만 담은 버전**을 빠르게 출시합니다.

* **주요 활동:**  
    
  * MVP 기능 정의: "이 기능이 없으면 우리 앱의 핵심 가치가 전달되지 않는다" 하는 기능만 추려냅니다.  
  * 빠른 출시: 핵심 기능이 동작하면 바로 출시하여 실제 사용자 데이터를 얻는 것이 중요합니다.  
  * 데이터 측정 및 학습: 사용자들이 어떤 기능을 주로 쓰는지, 어디서 이탈하는지 데이터를 분석하고, 직접 피드백을 받습니다.  
  * 개선(Evolution): 학습한 내용을 바탕으로 다음 버전에 추가하거나 개선할 기능의 우선순위를 정하고 개발 사이클을 반복합니다.


* **결과물:** 출시된 MVP 버전 1.0, 사용자 데이터 분석 리포트, 개선할 기능 백로그  
    
* **🤖 AI 활용 가이드:**  
    
  * **MVP 범위 정의:** "**중고 거래 앱 아이디어**가 있어. MVP에 반드시 포함되어야 할 **핵심 기능 3가지**와, 그 이후에 고려해볼 기능들을 우선순위별로 나눠서 제안해 줘."  
  * **사용자 스토리(User Story) 작성:** "**구매자로서, 판매자의 신뢰도를 확인하고 싶다**는 요구사항을 \*\*사용자 스토리 형식(As a..., I want to..., so that...)\*\*으로 작성해 줘."  
  * **피드백 분석:** "\[앱스토어 리뷰 또는 사용자 피드백 텍스트\]를 붙여넣고, **사용자들이 가장 많이 언급하는 불만 사항과 칭찬하는 기능이 무엇인지 요약하고, 개선 아이디어를 제안**해 줘."

---

### **Phase 4: 체계적인 구현 및 확장 (Implementation)**

MVP가 시장에서 긍정적인 반응을 얻기 시작하면, 이제 AI와 함께 훨씬 더 체계적으로 개발 프로세스를 관리하며 서비스를 확장해 나갑니다.

* **주요 활동:**  
    
  * 경량화된 규칙과 명세 (Lightweight Specs & Rules): 두꺼운 기획서 대신, 개발팀이 이해하기 쉬운 **사용자 스토리** 형식으로 요구사항을 정의합니다.  
  * 작업 계획 및 실행 (Tasks / Plan Mode / Implement): 개발할 기능들을 **Trello, Jira** 같은 툴을 사용하여 작은 '작업' 단위로 나눕니다.


* **결과물:** 주기적으로 업데이트되는 안정적인 서비스, 관리되는 작업 백로그, 팀의 개발 문화 정착  
    
* **🤖 AI 활용 가이드 (Iterative Mode 개발):**  
    
  * **AI가 Task Plan 문서 초안 작성:** "\*\*'이메일로 비밀번호 찾기' 기능에 대한 사용자 스토리를 기반으로, 개발자가 수행해야 할 **프론트엔드와 백엔드 Task 목록**을 상세하게 만들어 줘. 예상 소요 시간도 포함해 줘."  
  * **개발자가 검토 및 수정:** AI가 생성한 Task 목록을 개발자가 검토하며 기술적 현실에 맞게 수정하고 구체화합니다.  
  * **구현 시작:** 확정된 Task 목록에 따라 개발을 시작합니다.  
  * **코드 생성 및 리뷰:** "Node.js와 Express로 JWT 기반 로그인 API의 기본 코드를 작성해 줘" 와 같이 필요한 코드 스니펫을 생성하여 개발 속도를 높이고, 테스트 케이스 작성을 요청하여 코드 품질을 관리합니다.

이처럼 \*\*'AI가 초안 작성 → 개발자가 검토 및 확정 → 구현'\*\*의 반복적인(Iterative) 사이클을 통해, 기획과 문서화에 드는 시간을 획기적으로 줄이고 개발자는 실제 구현에 더 집중할 수 있게 됩니다.

---

---

## 🔄 기존 코드베이스에서 Spec Driven Development 적용하기

### 문제 상황
대부분의 실제 프로젝트는 이미 구현된 코드가 존재하는 상태에서 시작됩니다. 이런 경우 처음부터 Spec을 작성하는 것이 아니라, **기존 코드를 분석하여 역으로 설계 의도와 명세를 추출**하는 과정이 필요합니다.

### 🔍 Reverse Engineering 프로세스

#### **1단계: 코드베이스 분석 및 설계 요소 추출**

**AI를 활용한 코드 분석**
```bash
# 전체 프로젝트 구조 분석
"이 프로젝트의 전체 아키텍처를 분석하고, 
주요 컴포넌트, 데이터 플로우, 의존성 관계를 
다이어그램으로 정리해줘"

# 핵심 기능 식별
"src/ 폴더의 코드를 분석해서 
이 앱의 핵심 기능 5가지를 추출하고, 
각 기능별 관련 파일들을 매핑해줘"

# 데이터 모델 추출
"기존 코드에서 사용되는 모든 데이터 모델을 찾아서 
ERD(Entity Relationship Diagram) 형태로 정리해줘"
```

**추출해야 할 설계 요소들**
- **아키텍처 패턴**: MVVM, MVC, Clean Architecture 등
- **데이터 플로우**: 사용자 입력 → 비즈니스 로직 → 데이터 저장 경로
- **핵심 기능**: 사용자가 실제로 사용하는 주요 기능들
- **기술 스택**: 사용된 라이브러리, 프레임워크, 외부 서비스
- **비즈니스 규칙**: 코드에 숨어있는 도메인 로직과 제약사항

#### **2단계: 현재 상태 문서화**

**AGENT.md 역추출 작성**
```markdown
# AGENT.md (Reverse Engineered)

## 현재 제품 상태 분석
- 주요 기능: [코드 분석을 통해 식별된 기능들]
- 기술 스택: [실제 사용 중인 기술들]
- 아키텍처: [현재 구현된 아키텍처 패턴]

## 발견된 문제점
- 기술 부채: [코드 분석에서 발견된 문제들]
- 사용자 경험 이슈: [UI/UX 개선이 필요한 부분들]
- 성능 병목: [최적화가 필요한 영역들]

## 개선 방향성
- 단기 목표: [즉시 해결 가능한 문제들]
- 장기 비전: [제품이 나아가야 할 방향]
```

**현재 상태 Design 문서 생성**
```bash
# AI에게 현재 코드베이스 기반 설계 문서 생성 요청
"기존 코드를 분석해서 현재 시스템의 design.md 문서를 작성해줘.
아키텍처, 컴포넌트 구조, 데이터 모델, API 설계를 포함해서"
```

#### **3단계: 개선 계획 수립**

**Gap Analysis (현재 vs 이상적인 상태)**
```markdown
## 현재 상태 vs 목표 상태 비교

| 영역 | 현재 상태 | 목표 상태 | 개선 방법 |
|------|-----------|-----------|-----------|
| 아키텍처 | 스파게티 코드 | Clean Architecture | 점진적 리팩토링 |
| 테스트 | 테스트 없음 | 80% 커버리지 | TDD 도입 |
| 문서화 | 문서 없음 | 완전한 Spec | Spec 작성 |
```

**우선순위 기반 개선 로드맵**
```markdown
## Phase 1: 안정화 (1-2주)
- 핵심 기능 테스트 코드 작성
- 버그 수정 및 성능 최적화
- 기본 문서화

## Phase 2: 구조 개선 (2-4주)  
- 아키텍처 리팩토링
- 컴포넌트 분리 및 모듈화
- API 설계 개선

## Phase 3: 기능 확장 (4-8주)
- 새로운 기능 추가
- 사용자 경험 개선
- 성능 최적화
```

#### **4단계: Spec 문서 업데이트**

**Requirements 문서 작성**
```bash
# 기존 기능을 EARS 형식으로 문서화
"현재 구현된 로그인 기능을 분석해서 
EARS(Event-Action-Result) 형식의 요구사항으로 작성해줘"

# 개선사항을 새로운 요구사항으로 추가
"사용자 피드백을 바탕으로 개선이 필요한 기능들을 
새로운 요구사항으로 정의해줘"
```

**Design 문서 업데이트**
```bash
# 현재 설계를 개선된 설계로 발전
"현재 design.md를 기반으로 개선된 아키텍처를 제안하고,
마이그레이션 계획을 포함한 새로운 design.md를 작성해줘"
```

**Tasks 문서 생성**
```bash
# 개선 작업을 실행 가능한 태스크로 분해
"개선 계획을 바탕으로 실제 구현 가능한 태스크 리스트를 작성해줘.
각 태스크는 1-2일 내에 완료 가능한 크기로 나눠줘"
```

### 🛠️ 실제 적용 예시

#### **기존 TODO 앱 개선 시나리오**

**1. 현재 상태 분석**
```bash
# 코드 분석 요청
"app/src 폴더의 코드를 분석해서 현재 TODO 앱의 
아키텍처, 주요 기능, 데이터 모델을 정리해줘"
```

**2. 문제점 식별**
- 테스트 코드 부족 (커버리지 < 10%)
- 하드코딩된 문자열 (다국어 지원 불가)
- 단일 화면 구조 (확장성 부족)
- 오프라인 지원 없음

**3. 개선 방향 설정**
```markdown
## 개선 목표
- 테스트 커버리지 80% 달성
- 다국어 지원 추가
- 모듈화된 아키텍처로 리팩토링
- 오프라인 모드 지원
```

**4. 점진적 개선 실행**
```bash
# Phase 1: 테스트 코드 추가
"기존 TodoViewModel 클래스에 대한 단위 테스트를 작성해줘"

# Phase 2: 문자열 리소스화
"하드코딩된 모든 문자열을 strings.xml로 이동하는 리팩토링을 해줘"

# Phase 3: 아키텍처 개선
"현재 단일 Activity 구조를 Navigation Component를 사용한 
멀티 Fragment 구조로 리팩토링해줘"
```

### 💡 성공을 위한 핵심 원칙

#### **1. 점진적 개선 (Incremental Improvement)**
- 한 번에 모든 것을 바꾸려 하지 말고 작은 단위로 개선
- 각 단계마다 동작하는 상태를 유지
- 사용자에게 영향을 주지 않는 범위에서 진행

#### **2. 문서와 코드의 동기화**
- 코드 변경 시 관련 Spec 문서도 함께 업데이트
- AI를 활용해 코드 변경사항을 문서에 자동 반영
- 정기적인 문서-코드 일치성 검증

#### **3. 사용자 중심 우선순위**
- 사용자에게 직접적인 가치를 주는 개선사항 우선
- 내부 구조 개선은 사용자 기능 개선과 병행
- 데이터 기반 의사결정 (사용 패턴, 오류 로그 분석)

### 🔧 AI 활용 팁

#### **효과적인 코드 분석 프롬프트**
```bash
# 아키텍처 분석
"이 프로젝트의 아키텍처를 분석하고 개선점을 제안해줘.
특히 SOLID 원칙과 Clean Architecture 관점에서 평가해줘"

# 기술 부채 식별
"코드를 분석해서 기술 부채가 있는 부분을 찾고,
각각의 리팩토링 우선순위와 예상 작업량을 알려줘"

# 성능 최적화 포인트
"성능 병목이 될 수 있는 코드를 찾고,
최적화 방안을 구체적으로 제안해줘"
```

#### **문서 생성 자동화**
```bash
# 기존 코드에서 API 문서 생성
"Controller 클래스들을 분석해서 OpenAPI 3.0 스펙을 생성해줘"

# 컴포넌트 문서 생성
"React 컴포넌트들을 분석해서 Storybook 스토리를 생성해줘"

# 데이터베이스 스키마 문서화
"Entity 클래스들을 분석해서 ERD와 테이블 명세서를 만들어줘"
```

이러한 접근법을 통해 기존 코드베이스가 있는 프로젝트에서도 체계적으로 Spec Driven Development를 도입할 수 있으며, AI의 도움으로 이 과정을 크게 가속화할 수 있습니다.