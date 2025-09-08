package com.example.makeitso.data.injection

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.mockk.mockk

@Module
@InstallIn(SingletonComponent::class)
object FirebaseHiltModule {
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = mockk(relaxed = true)

    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = mockk(relaxed = true)
}
