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
    fun getUserProfileFlow(): Flow<UserProfile?> {
        return userProfileLocalDataSource.userProfileFlow
    }

    suspend fun getUserProfile(userId: String): UserProfile? {
        return userProfileLocalDataSource.getUserProfile(userId)
    }

    suspend fun createUserProfile(profile: UserProfile): String {
        userProfileLocalDataSource.saveUserProfile(profile)
        return profile.userId
    }

    suspend fun updateUserProfile(profile: UserProfile) {
        userProfileLocalDataSource.saveUserProfile(profile)
    }

    suspend fun clearUserProfile() {
        userProfileLocalDataSource.clearUserProfile()
    }
}