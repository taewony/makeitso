package com.example.makeitso.data.datasource

import com.example.makeitso.data.dao.UserProfileDao
import com.example.makeitso.data.mapper.UserProfileMapper
import com.example.makeitso.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileLocalDataSource @Inject constructor(
    private val userProfileDao: UserProfileDao
) {
    
    fun getUserProfileFlow(userId: String): Flow<UserProfile?> {
        return userProfileDao.getUserProfileFlow(userId).map { entity ->
            entity?.let { UserProfileMapper.toDomain(it) }
        }
    }

    suspend fun getUserProfile(userId: String): UserProfile? {
        println("UserProfileLocalDataSource: Getting profile for user: $userId")
        val entity = userProfileDao.getUserProfile(userId)
        return entity?.let { 
            val profile = UserProfileMapper.toDomain(it)
            println("UserProfileLocalDataSource: Found profile: $profile")
            
            // 세션 만료 체크
            if (!UserProfileMapper.isSessionValid(it)) {
                println("UserProfileLocalDataSource: Session expired for user: $userId")
                return null
            }
            
            profile
        }
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        println("UserProfileLocalDataSource: Saving profile: $profile")
        val entity = UserProfileMapper.toEntity(profile)
        userProfileDao.insertUserProfile(entity)
        println("UserProfileLocalDataSource: Profile saved successfully to Room DB")
    }

    suspend fun clearUserProfile(userId: String) {
        println("UserProfileLocalDataSource: Clearing profile for user: $userId")
        userProfileDao.deleteUserProfile(userId)
    }
    
    suspend fun clearAllProfiles() {
        println("UserProfileLocalDataSource: Clearing all profiles")
        userProfileDao.clearAllProfiles()
    }
    
    suspend fun hasUserProfile(userId: String): Boolean {
        return userProfileDao.hasUserProfile(userId) > 0
    }
    
    suspend fun isSessionValid(userId: String): Boolean {
        val expiryTime = userProfileDao.getSessionExpiry(userId) ?: return false
        return System.currentTimeMillis() < expiryTime
    }
}