package com.example.makeitso.data.injection

import android.content.Context
import android.content.SharedPreferences
import com.example.makeitso.data.dao.AiMessageDao
import com.example.makeitso.data.dao.TodoItemDao
import com.example.makeitso.data.dao.UserProfileDao
import com.example.makeitso.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDataModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("makeitso_prefs", Context.MODE_PRIVATE)
    }
    
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }
    
    @Provides
    fun provideUserProfileDao(database: AppDatabase): UserProfileDao {
        return database.userProfileDao()
    }
    
    @Provides
    fun provideTodoItemDao(database: AppDatabase): TodoItemDao {
        return database.todoItemDao()
    }
    
    @Provides
    fun provideAiMessageDao(database: AppDatabase): AiMessageDao {
        return database.aiMessageDao()
    }
}