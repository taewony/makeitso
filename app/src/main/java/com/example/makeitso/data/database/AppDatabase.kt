package com.example.makeitso.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.makeitso.data.dao.AiMessageDao
import com.example.makeitso.data.dao.TodoItemDao
import com.example.makeitso.data.dao.UserProfileDao
import com.example.makeitso.data.model.entity.AiMessageEntity
import com.example.makeitso.data.model.entity.TodoItemEntity
import com.example.makeitso.data.model.entity.UserProfileEntity

@Database(
    entities = [
        UserProfileEntity::class,
        TodoItemEntity::class,
        AiMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun userProfileDao(): UserProfileDao
    abstract fun todoItemDao(): TodoItemDao
    abstract fun aiMessageDao(): AiMessageDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "makeitso_database"
                )
                .fallbackToDestructiveMigration() // Phase 2에서는 스키마 변경 시 DB 재생성
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}