package com.example.makeitso.data.injection

import com.example.makeitso.data.datasource.AuthRemoteDataSource
import com.example.makeitso.data.datasource.TodoItemRemoteDataSource
import com.example.makeitso.data.datasource.TodoListRemoteDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Singleton
    @Provides
    fun provideAuthRemoteDataSource(auth: FirebaseAuth?): AuthRemoteDataSource {
        return AuthRemoteDataSource(auth)
    }

    @Singleton
    @Provides
    fun provideTodoItemRemoteDataSource(firestore: FirebaseFirestore?): TodoItemRemoteDataSource {
        return TodoItemRemoteDataSource(firestore)
    }

    @Singleton
    @Provides
    fun provideTodoListRemoteDataSource(firestore: FirebaseFirestore?): TodoListRemoteDataSource {
        return TodoListRemoteDataSource(firestore)
    }
}