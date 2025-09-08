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

class AuthRemoteDataSource(private val auth: FirebaseAuth?) {
    // val currentUser: FirebaseUser? get() = auth.currentUser
    var currentUser: FirebaseUser? = null

    // val currentUserIdFlow: Flow<String?>
    //     get() = callbackFlow {
    //         val listener = FirebaseAuth.AuthStateListener { _ -> this.trySend(currentUser?.uid) }
    //         auth.addAuthStateListener(listener)
    //         awaitClose { auth.removeAuthStateListener(listener) }
    //     }
    val currentUserIdFlow: MutableStateFlow<String?> = MutableStateFlow(UUID.randomUUID().toString())


    suspend fun createGuestAccount() {
        // auth.signInAnonymously().await()
        currentUserIdFlow.value = UUID.randomUUID().toString()
    }

    suspend fun signIn(email: String, password: String) {
        // auth.signInWithEmailAndPassword(email, password).await()
        currentUserIdFlow.value = UUID.randomUUID().toString()
    }

    suspend fun linkAccount(email: String, password: String) {
        // val credential = EmailAuthProvider.getCredential(email, password)
        // auth.currentUser!!.linkWithCredential(credential).await()
    }

    fun signOut() {
        // if (auth.currentUser!!.isAnonymous) {
        //     auth.currentUser!!.delete()
        // }
        // auth.signOut()
        currentUser = null
        currentUserIdFlow.value = null
    }

    suspend fun deleteAccount() {
        // auth.currentUser!!.delete().await()
        currentUser = null
        currentUserIdFlow.value = null
    }
}
