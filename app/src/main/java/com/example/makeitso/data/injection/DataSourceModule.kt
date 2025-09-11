package com.example.makeitso.data.injection

import com.example.makeitso.data.datasource.AuthRemoteDataSource
import com.example.makeitso.data.datasource.TodoItemRemoteDataSource
import com.example.makeitso.data.datasource.TodoListRemoteDataSource
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
    fun provideAuthRemoteDataSource(): AuthRemoteDataSource {
        return AuthRemoteDataSource()
    }

    @Singleton
    @Provides
    fun provideTodoItemRemoteDataSource(): TodoItemRemoteDataSource {
        return TodoItemRemoteDataSource()
    }

    @Singleton
    @Provides
    fun provideTodoListRemoteDataSource(): TodoListRemoteDataSource {
        return TodoListRemoteDataSource()
    }
}