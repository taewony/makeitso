package com.example.makeitso.data.dao

import androidx.room.*
import com.example.makeitso.data.model.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    
    @Query("SELECT * FROM user_profiles WHERE userId = :userId")
    suspend fun getUserProfile(userId: String): UserProfileEntity?
    
    @Query("SELECT * FROM user_profiles WHERE userId = :userId")
    fun getUserProfileFlow(userId: String): Flow<UserProfileEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileEntity)
    
    @Update
    suspend fun updateUserProfile(profile: UserProfileEntity)
    
    @Query("DELETE FROM user_profiles WHERE userId = :userId")
    suspend fun deleteUserProfile(userId: String)
    
    @Query("DELETE FROM user_profiles")
    suspend fun clearAllProfiles()
    
    @Query("SELECT COUNT(*) FROM user_profiles WHERE userId = :userId")
    suspend fun hasUserProfile(userId: String): Int
    
    @Query("SELECT sessionExpiryTimestamp FROM user_profiles WHERE userId = :userId")
    suspend fun getSessionExpiry(userId: String): Long?
}