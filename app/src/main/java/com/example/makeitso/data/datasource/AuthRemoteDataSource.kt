package com.example.makeitso.data.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val auth: FirebaseAuth
) {
    // In-Memory user state
    private var currentUser: FirebaseUser? = null

    val currentUserId: String?
        get() = currentUser?.uid

    val hasUser: Boolean
        get() = currentUser != null

    suspend fun signInWithEmail(email: String, password: String) {
        // Firebase auth.signInWithEmailAndPassword(email, password).await()
        currentUser = createMockFirebaseUser(email)
    }

    suspend fun createUserWithEmail(email: String, password: String) {
        // Firebase auth.createUserWithEmailAndPassword(email, password).await()
        currentUser = createMockFirebaseUser(email)
    }

    suspend fun signInWithGoogle(idToken: String) {
        // val credential = GoogleAuthProvider.getCredential(idToken, null)
        // auth.signInWithCredential(credential).await()
        currentUser = createMockFirebaseUser("google.user@example.com")
    }

    fun signOut() {
        // auth.signOut()
        currentUser = null
    }

    // Mock FirebaseUser for In-Memory implementation
    private fun createMockFirebaseUser(email: String): FirebaseUser {
        return object : FirebaseUser() {
            override fun getUid(): String = UUID.randomUUID().toString()
            override fun getEmail(): String = email
            override fun isAnonymous(): Boolean = false
            override fun getProviderData(): MutableList<out com.google.firebase.auth.UserInfo> = mutableListOf()
            override fun reload() = null
            override fun getIdToken(forceRefresh: Boolean) = null
        }
    }
}
