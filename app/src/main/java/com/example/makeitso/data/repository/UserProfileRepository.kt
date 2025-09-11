package com.example.makeitso.data.repository

import com.example.makeitso.data.datasource.UserProfileLocalDataSource
import com.example.makeitso.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepository @Inject constructor(
    private val userProfileLocalDataSource: UserProfileLocalDataSource
) {
    fun getUserProfileFlow(userId: String): Flow<UserProfile?> {
        return userProfileLocalDataSource.getUserProfileFlow(userId)
    }

    suspend fun getUserProfile(userId: String): UserProfile? {
        return userProfileLocalDataSource.getUserProfile(userId)
    }

    suspend fun createUserProfile(profile: UserProfile): String {
        android.util.Log.d("UserProfileRepository", "사용자 프로필 생성: $profile")
        userProfileLocalDataSource.saveUserProfile(profile)
        android.util.Log.d("UserProfileRepository", "사용자 프로필 저장 완료")
        return profile.userId
    }

    suspend fun updateUserProfile(profile: UserProfile) {
        android.util.Log.d("UserProfileRepository", "사용자 프로필 업데이트: $profile")
        userProfileLocalDataSource.saveUserProfile(profile)
        android.util.Log.d("UserProfileRepository", "사용자 프로필 업데이트 완료")
    }

    suspend fun clearUserProfile(userId: String) {
        userProfileLocalDataSource.clearUserProfile(userId)
    }
    
    suspend fun clearAllProfiles() {
        userProfileLocalDataSource.clearAllProfiles()
    }
    
    suspend fun hasUserProfile(userId: String): Boolean {
        return userProfileLocalDataSource.hasUserProfile(userId)
    }
    
    suspend fun isSessionValid(userId: String): Boolean {
        return userProfileLocalDataSource.isSessionValid(userId)
    }
}