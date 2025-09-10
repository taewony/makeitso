package com.example.makeitso.data.datasource

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

// Mock FirebaseUser for Phase 1 local implementation
data class MockFirebaseUser(
    val uid: String,
    val email: String? = null,
    val isAnonymous: Boolean = false
)

class AuthRemoteDataSource(private val auth: FirebaseAuth?) {
    private var mockCurrentUser: MockFirebaseUser? = null
    
    val currentUser: FirebaseUser? 
        get() = mockCurrentUser as? FirebaseUser
    
    // For Phase 1, we'll use a simple approach to get current user ID
    fun getCurrentUserId(): String? = mockCurrentUser?.uid
    
    val currentUserIdFlow: MutableStateFlow<String?> = MutableStateFlow(null)


    suspend fun createGuestAccount() {
        val userId = UUID.randomUUID().toString()
        mockCurrentUser = MockFirebaseUser(uid = userId, isAnonymous = true)
        currentUserIdFlow.value = userId
        println("AuthRemoteDataSource: Created guest account with ID: $userId")
    }

    suspend fun signIn(email: String, password: String) {
        val userId = UUID.randomUUID().toString()
        mockCurrentUser = MockFirebaseUser(uid = userId, email = email, isAnonymous = false)
        currentUserIdFlow.value = userId
        println("AuthRemoteDataSource: Signed in user with ID: $userId, email: $email")
    }

    suspend fun linkAccount(email: String, password: String) {
        // For Phase 1: Create a new account instead of linking
        val userId = UUID.randomUUID().toString()
        mockCurrentUser = MockFirebaseUser(uid = userId, email = email, isAnonymous = false)
        currentUserIdFlow.value = userId
        println("AuthRemoteDataSource: Created new account with ID: $userId, email: $email")
    }

    fun signOut() {
        println("AuthRemoteDataSource: Signing out user: ${mockCurrentUser?.uid}")
        mockCurrentUser = null
        currentUserIdFlow.value = null
    }

    suspend fun deleteAccount() {
        println("AuthRemoteDataSource: Deleting account: ${mockCurrentUser?.uid}")
        mockCurrentUser = null
        currentUserIdFlow.value = null
    }
}
