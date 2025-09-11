package com.example.makeitso.data.injection

import android.content.Context
import com.example.makeitso.data.dao.TodoItemDao
import com.example.makeitso.data.datasource.AuthRemoteDataSource
import com.example.makeitso.data.datasource.TodoItemRemoteDataSource
import com.example.makeitso.data.datasource.TodoListRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Singleton
    @Provides
    fun provideAuthRemoteDataSource(@ApplicationContext context: Context): AuthRemoteDataSource {
        return AuthRemoteDataSource(context)
    }

    @Singleton
    @Provides
    fun provideTodoItemRemoteDataSource(todoItemDao: TodoItemDao): TodoItemRemoteDataSource {
        return TodoItemRemoteDataSource(todoItemDao)
    }

    @Singleton
    @Provides
    fun provideTodoListRemoteDataSource(): TodoListRemoteDataSource {
        return TodoListRemoteDataSource()
    }
}