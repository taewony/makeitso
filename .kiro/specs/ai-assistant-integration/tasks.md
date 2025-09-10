# Implementation Plan

- [ ] 1. Phase 1 기반 데이터 모델 및 구조 설정
  - 새로운 데이터 모델 클래스들을 생성하여 AI 비서 기능의 기반을 마련
  - 기존 TodoItem 모델을 확장하여 deadline 필드 추가
  - _Requirements: 2.2, 3.2, 3.3_

- [x] 1.1 AI 관련 데이터 모델 생성


  - AiCharacter enum, UserGoals, UserProfile, AiMessage 데이터 클래스 구현
  - Deadline enum을 포함한 TodoItem 확장
  - _Requirements: 2.3, 3.2_



- [ ] 1.2 로컬 데이터 소스 구현
  - UserProfileLocalDataSource를 SharedPreferences 기반으로 구현
  - AiAssistantLocalDataSource를 메모리 기반으로 구현
  - _Requirements: 2.1, 2.4_

- [ ] 2. 사용자 프로필 관리 시스템 구현
  - 사용자 목표 설정 및 AI 캐릭터 선택 기능 구현
  - 온보딩 완료 상태 관리 로직 구현


  - _Requirements: 2.2, 2.3, 2.5_

- [x] 2.1 UserProfileRepository 구현


  - 사용자 프로필 CRUD 작업을 위한 repository 패턴 구현
  - 로컬 데이터 소스와 연동하는 repository 구현
  - _Requirements: 2.4, 2.5_

- [ ] 2.2 온보딩 화면 UI 구현
  - 단기/장기 목표 입력 화면 구현
  - AI 캐릭터 선택 화면 구현
  - 온보딩 완료 후 메인 화면 이동 로직
  - _Requirements: 2.1, 2.2, 2.3, 2.5_




- [ ] 3. 설정 화면 확장 및 개선
  - 기존 설정 화면에 목표 수정, 캐릭터 변경, 로그아웃 기능 추가
  - 과거 기록 보기 기능 구현
  - _Requirements: 5.1, 5.4, 5.5, 5.6_

- [ ] 3.1 설정 화면 UI 확장
  - 목표 편집 UI 구현 (단기/장기 목표 분리)
  - AI 캐릭터 변경 UI 구현
  - 로그아웃 버튼 및 확인 다이얼로그 구현
  - _Requirements: 5.4, 5.5, 5.6_

- [ ] 3.2 과거 기록 보기 화면 구현
  - 완료된 할 일 포함 여부 토글 기능
  - 과거 AI 잔소리 메시지 포함 여부 토글 기능
  - 필터링된 데이터 표시 UI 구현
  - _Requirements: 5.1, 5.2, 5.3_

- [x] 4. AI 프롬프트 서비스 및 응답 시스템 구현


  - Phase 1용 미리 정의된 응답 시스템 구현
  - 캐릭터별 응답 템플릿 생성 및 관리
  - _Requirements: 4.1, 4.2, 4.8_


- [ ] 4.1 AiPromptService 구현
  - 사용자 목표, TODO 항목, 캐릭터 정보를 기반으로 프롬프트 생성
  - Phase 1용 미리 정의된 응답 생성 로직 구현
  - _Requirements: 4.4, 4.5_

- [ ] 4.2 캐릭터별 응답 템플릿 시스템
  - 각 AI 캐릭터별 고유한 톤앤매너의 응답 템플릿 작성
  - 미완료 할 일 개수, 마감 임박 항목에 따른 동적 응답 생성
  - _Requirements: 4.5, 4.8_

- [ ] 5. TODO 항목 관리 기능 확장
  - 기존 TodoItem에 deadline 필드 추가 및 UI 반영
  - TODO 생성 시 자동 AI 응답 트리거 구현
  - _Requirements: 3.2, 3.3, 4.2_

- [ ] 5.1 TodoItem 모델 확장 및 UI 업데이트
  - Deadline enum 구현 및 TodoItem에 deadline 필드 추가
  - TODO 생성/편집 화면에 마감기한 선택 UI 추가
  - _Requirements: 3.2, 3.3_

- [ ] 5.2 TODO 생성 시 AI 응답 자동 트리거
  - 새 TODO 항목 생성 완료 시 AI 응답 자동 생성 및 표시
  - 자동 트리거와 수동 트리거 구분하여 처리
  - _Requirements: 4.2, 4.7_

- [ ] 6. AI 응답 모달 및 상호작용 개선
  - 기존 AI Nudge 다이얼로그를 개선하여 실제 AI 응답 표시
  - 응답 히스토리 저장 및 관리 기능 구현
  - _Requirements: 4.1, 4.7, 4.8_

- [ ] 6.1 AI 응답 모달 UI 개선
  - 캐릭터별 응답을 표시하는 개선된 모달 다이얼로그 구현
  - 응답 텍스트 스타일링 및 사용자 경험 개선
  - _Requirements: 4.1, 4.7_

- [ ] 6.2 AI 메시지 히스토리 관리
  - AiMessage 데이터 모델을 사용한 응답 히스토리 저장
  - 과거 응답 조회 및 표시 기능 구현
  - _Requirements: 4.8, 5.2_

- [ ] 7. 인증 및 세션 관리 개선
  - 4주 세션 유지 및 만료 처리 로직 구현
  - 온보딩 미완료 시 설정 화면 강제 이동 로직
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 2.4_

- [ ] 7.1 세션 관리 로직 구현
  - 로그인 상태 4주 유지 및 자동 만료 처리
  - 세션 만료 시 재로그인 화면 이동 로직
  - _Requirements: 1.2, 1.3, 1.4_

- [ ] 7.2 온보딩 상태 검증 및 리다이렉션
  - 로그인 후 사용자 프로필 완성도 검사
  - 미완료 시 설정 화면으로 강제 이동하는 네비게이션 로직
  - _Requirements: 2.4, 2.5_

- [ ] 8. Phase 1 통합 테스트 및 검증
  - 전체 기능 통합 테스트 수행
  - 사용자 플로우 검증 및 버그 수정
  - _Requirements: 모든 Phase 1 요구사항_

- [ ] 8.1 기능 통합 테스트
  - 온보딩부터 AI 응답까지 전체 플로우 테스트
  - 각 캐릭터별 응답 생성 테스트
  - _Requirements: 전체 요구사항 검증_

- [ ] 8.2 사용자 경험 최적화
  - UI/UX 개선사항 적용
  - 성능 최적화 및 메모리 사용량 개선
  - _Requirements: 성능 및 사용성 요구사항_