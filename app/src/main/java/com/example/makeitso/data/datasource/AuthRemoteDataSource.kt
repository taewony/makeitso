package com.example.makeitso.data.datasource

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID
import javax.inject.Inject

// Mock FirebaseUser for Phase 1 local implementation
data class MockFirebaseUser(
    val uid: String,
    val email: String? = null,
    val isAnonymous: Boolean = false
)

class AuthRemoteDataSource @Inject constructor(
    private val context: Context
) {
    private var mockCurrentUser: MockFirebaseUser? = null
    private var hasSignedOut: Boolean = false
    
    // SharedPreferences for persistent account storage
    private val authPrefs: SharedPreferences by lazy {
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    }
    
    // Phase 2: SharedPreferences를 사용한 계정 저장소
    private fun saveAccount(email: String, password: String) {
        authPrefs.edit().putString("account_$email", password).apply()
        android.util.Log.d("AuthRemoteDataSource", "계정 저장됨: $email")
    }
    
    private fun getStoredPassword(email: String): String? {
        val password = authPrefs.getString("account_$email", null)
        android.util.Log.d("AuthRemoteDataSource", "저장된 계정 확인: $email -> ${if (password != null) "존재함" else "없음"}")
        return password
    }
    
    private fun hasStoredAccount(email: String): Boolean {
        return authPrefs.contains("account_$email")
    }
    
    val currentUser: MockFirebaseUser? 
        get() = mockCurrentUser
    
    // For Phase 1, we'll use a simple approach to get current user ID
    fun getCurrentUserId(): String? = mockCurrentUser?.uid
    
    // Get current user email
    fun getCurrentUserEmail(): String? = mockCurrentUser?.email
    
    val currentUserIdFlow: MutableStateFlow<String?> = MutableStateFlow(null)
    
    // Check if user has signed out (to distinguish from first-time user)
    fun hasUserSignedOut(): Boolean = hasSignedOut
    
    init {
        // 앱 시작 시 이전 로그인 상태 복원 시도
        restoreLoginState()
    }
    
    private fun restoreLoginState() {
        val lastLoggedInEmail = authPrefs.getString("last_logged_in_email", null)
        val sessionExpiry = authPrefs.getLong("session_expiry", 0L)
        val currentTime = System.currentTimeMillis()
        
        android.util.Log.d("AuthRemoteDataSource", "로그인 상태 복원 시도: email=$lastLoggedInEmail, expiry=$sessionExpiry, current=$currentTime")
        
        if (lastLoggedInEmail != null && currentTime < sessionExpiry) {
            // 세션이 유효하면 자동 로그인
            val userId = generateConsistentUserId(lastLoggedInEmail)
            mockCurrentUser = MockFirebaseUser(uid = userId, email = lastLoggedInEmail, isAnonymous = false)
            currentUserIdFlow.value = userId
            hasSignedOut = false
            android.util.Log.d("AuthRemoteDataSource", "자동 로그인 성공: $lastLoggedInEmail")
        } else {
            android.util.Log.d("AuthRemoteDataSource", "자동 로그인 불가 (세션 만료 또는 로그인 기록 없음)")
        }
    }
    
    private fun saveLoginSession(email: String) {
        val sessionDuration = 4 * 7 * 24 * 60 * 60 * 1000L // 4주
        val expiryTime = System.currentTimeMillis() + sessionDuration
        
        authPrefs.edit()
            .putString("last_logged_in_email", email)
            .putLong("session_expiry", expiryTime)
            .apply()
        
        android.util.Log.d("AuthRemoteDataSource", "로그인 세션 저장: $email, 만료시간: $expiryTime")
    }


    suspend fun createGuestAccount() {
        val userId = UUID.randomUUID().toString()
        mockCurrentUser = MockFirebaseUser(uid = userId, isAnonymous = true)
        currentUserIdFlow.value = userId
        hasSignedOut = false // 게스트 계정 생성 시 로그아웃 플래그 리셋
        println("AuthRemoteDataSource: Created guest account with ID: $userId")
    }

    suspend fun signIn(email: String, password: String) {
        android.util.Log.d("AuthRemoteDataSource", "로그인 시도: $email")
        
        // Phase 2: SharedPreferences에서 등록된 계정인지 확인
        val storedPassword = getStoredPassword(email)
        if (storedPassword == null) {
            android.util.Log.d("AuthRemoteDataSource", "로그인 실패 - 계정을 찾을 수 없음: $email")
            throw Exception("계정을 찾을 수 없습니다. 회원가입을 먼저 해주세요.")
        }
        
        if (storedPassword != password) {
            android.util.Log.d("AuthRemoteDataSource", "로그인 실패 - 비밀번호 불일치: $email")
            throw Exception("비밀번호가 올바르지 않습니다.")
        }
        
        // 로그인 성공
        val userId = generateConsistentUserId(email)
        mockCurrentUser = MockFirebaseUser(uid = userId, email = email, isAnonymous = false)
        currentUserIdFlow.value = userId
        hasSignedOut = false // 로그인 성공 시 로그아웃 플래그 리셋
        
        // 로그인 세션 저장
        saveLoginSession(email)
        
        android.util.Log.d("AuthRemoteDataSource", "로그인 성공: $email, userId: $userId")
    }

    suspend fun linkAccount(email: String, password: String) {
        android.util.Log.d("AuthRemoteDataSource", "회원가입 시도: $email")
        
        // Phase 2: SharedPreferences에서 이미 등록된 계정인지 확인
        if (hasStoredAccount(email)) {
            android.util.Log.d("AuthRemoteDataSource", "회원가입 실패 - 이미 존재하는 이메일: $email")
            throw Exception("이미 등록된 이메일입니다.")
        }
        
        // 새 계정 등록
        saveAccount(email, password)
        
        // 이메일을 기반으로 일관된 사용자 ID 생성
        val userId = generateConsistentUserId(email)
        mockCurrentUser = MockFirebaseUser(uid = userId, email = email, isAnonymous = false)
        currentUserIdFlow.value = userId
        hasSignedOut = false // 회원가입 성공 시 로그아웃 플래그 리셋
        
        // 회원가입 후 자동 로그인 세션 저장
        saveLoginSession(email)
        
        android.util.Log.d("AuthRemoteDataSource", "회원가입 성공: $email, userId: $userId")
    }

    fun signOut() {
        android.util.Log.d("AuthRemoteDataSource", "로그아웃: ${mockCurrentUser?.uid}")
        hasSignedOut = true
        mockCurrentUser = null
        currentUserIdFlow.value = null
        
        // 로그인 세션 정보 삭제
        authPrefs.edit()
            .remove("last_logged_in_email")
            .remove("session_expiry")
            .apply()
        
        android.util.Log.d("AuthRemoteDataSource", "로그인 세션 정보 삭제 완료")
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
