package com.example.makeitso.data.dao

import androidx.room.*
import com.example.makeitso.data.model.entity.TodoItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoItemDao {
    
    @Query("SELECT * FROM todo_items WHERE ownerId = :ownerId ORDER BY createdAt DESC")
    fun getTodoItemsFlow(ownerId: String): Flow<List<TodoItemEntity>>
    
    @Query("SELECT * FROM todo_items WHERE id = :itemId")
    suspend fun getTodoItem(itemId: String): TodoItemEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodoItem(item: TodoItemEntity)
    
    @Update
    suspend fun updateTodoItem(item: TodoItemEntity)
    
    @Query("DELETE FROM todo_items WHERE id = :itemId")
    suspend fun deleteTodoItem(itemId: String)
    
    @Query("DELETE FROM todo_items WHERE ownerId = :ownerId")
    suspend fun deleteAllTodoItems(ownerId: String)
    
    @Query("SELECT * FROM todo_items WHERE ownerId = :ownerId AND completed = 0")
    suspend fun getIncompleteTodoItems(ownerId: String): List<TodoItemEntity>
    
    @Query("SELECT * FROM todo_items WHERE ownerId = :ownerId AND completed = 1")
    suspend fun getCompletedTodoItems(ownerId: String): List<TodoItemEntity>
    
    @Query("SELECT * FROM todo_items WHERE ownerId = :ownerId AND flagged = 1")
    suspend fun getFlaggedTodoItems(ownerId: String): List<TodoItemEntity>
}