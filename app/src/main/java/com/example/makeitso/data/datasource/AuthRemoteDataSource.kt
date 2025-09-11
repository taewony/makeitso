package com.example.makeitso.data.datasource

import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

// Mock FirebaseUser for Phase 1 local implementation
data class MockFirebaseUser(
    val uid: String,
    val email: String? = null,
    val isAnonymous: Boolean = false
)

class AuthRemoteDataSource {
    private var mockCurrentUser: MockFirebaseUser? = null
    private var hasSignedOut: Boolean = false
    
    // Phase 1: 등록된 계정을 추적하기 위한 간단한 저장소
    private val registeredAccounts = mutableMapOf<String, String>() // email -> password
    
    val currentUser: MockFirebaseUser? 
        get() = mockCurrentUser
    
    // For Phase 1, we'll use a simple approach to get current user ID
    fun getCurrentUserId(): String? = mockCurrentUser?.uid
    
    // Get current user email
    fun getCurrentUserEmail(): String? = mockCurrentUser?.email
    
    val currentUserIdFlow: MutableStateFlow<String?> = MutableStateFlow(null)
    
    // Check if user has signed out (to distinguish from first-time user)
    fun hasUserSignedOut(): Boolean = hasSignedOut


    suspend fun createGuestAccount() {
        val userId = UUID.randomUUID().toString()
        mockCurrentUser = MockFirebaseUser(uid = userId, isAnonymous = true)
        currentUserIdFlow.value = userId
        hasSignedOut = false // 게스트 계정 생성 시 로그아웃 플래그 리셋
        println("AuthRemoteDataSource: Created guest account with ID: $userId")
    }

    suspend fun signIn(email: String, password: String) {
        // Phase 1: 등록된 계정인지 확인
        val storedPassword = registeredAccounts[email]
        if (storedPassword == null) {
            println("AuthRemoteDataSource: Sign in failed - Account not found for email: $email")
            throw Exception("계정을 찾을 수 없습니다. 회원가입을 먼저 해주세요.")
        }
        
        if (storedPassword != password) {
            println("AuthRemoteDataSource: Sign in failed - Invalid password for email: $email")
            throw Exception("비밀번호가 올바르지 않습니다.")
        }
        
        // 로그인 성공
        val userId = generateConsistentUserId(email)
        mockCurrentUser = MockFirebaseUser(uid = userId, email = email, isAnonymous = false)
        currentUserIdFlow.value = userId
        hasSignedOut = false // 로그인 성공 시 로그아웃 플래그 리셋
        println("AuthRemoteDataSource: Signed in user with ID: $userId, email: $email")
    }

    suspend fun linkAccount(email: String, password: String) {
        // Phase 1: 이미 등록된 계정인지 확인
        if (registeredAccounts.containsKey(email)) {
            println("AuthRemoteDataSource: Account creation failed - Email already exists: $email")
            throw Exception("이미 등록된 이메일입니다.")
        }
        
        // 새 계정 등록
        registeredAccounts[email] = password
        
        // 이메일을 기반으로 일관된 사용자 ID 생성
        val userId = generateConsistentUserId(email)
        mockCurrentUser = MockFirebaseUser(uid = userId, email = email, isAnonymous = false)
        currentUserIdFlow.value = userId
        hasSignedOut = false // 회원가입 성공 시 로그아웃 플래그 리셋
        println("AuthRemoteDataSource: Created new account with ID: $userId, email: $email")
    }

    fun signOut() {
        println("AuthRemoteDataSource: Signing out user: ${mockCurrentUser?.uid}")
        hasSignedOut = true
        mockCurrentUser = null
        currentUserIdFlow.value = null
    }

    suspend fun deleteAccount() {
        println("AuthRemoteDataSource: Deleting account: ${mockCurrentUser?.uid}")
        mockCurrentUser = null
        currentUserIdFlow.value = null
    }
    
    // Phase 1: 이메일을 기반으로 일관된 사용자 ID 생성
    private fun generateConsistentUserId(email: String): String {
        // 이메일을 기반으로 일관된 UUID 생성
        // Phase 1에서는 간단하게 이메일 해시를 사용
        val hash = email.hashCode()
        return "user_${Math.abs(hash)}_${email.substringBefore("@")}"
    }
}
