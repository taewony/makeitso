package com.example.makeitso.data.repository

import com.example.makeitso.data.datasource.AuthRemoteDataSource
import com.example.makeitso.data.datasource.MockFirebaseUser
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource
) {
    val currentUser: MockFirebaseUser? 
        get() = authRemoteDataSource.currentUser
    
    // For Phase 1, we need a way to get current user ID
    fun getCurrentUserId(): String? = authRemoteDataSource.getCurrentUserId()
    
    val currentUserIdFlow: StateFlow<String?> = authRemoteDataSource.currentUserIdFlow

    suspend fun createGuestAccount() {
        authRemoteDataSource.createGuestAccount()
    }

    suspend fun signIn(email: String, password: String) {
        authRemoteDataSource.signIn(email, password)
    }

    suspend fun signUp(email: String, password: String) {
       authRemoteDataSource.linkAccount(email, password)
    }

    fun signOut() {
        authRemoteDataSource.signOut()
    }

    suspend fun deleteAccount() {
        authRemoteDataSource.deleteAccount()
    }
    
    fun hasUserSignedOut(): Boolean {
        return authRemoteDataSource.hasUserSignedOut()
    }
    
    fun getCurrentUserEmail(): String? {
        return authRemoteDataSource.getCurrentUserEmail()
    }
}
