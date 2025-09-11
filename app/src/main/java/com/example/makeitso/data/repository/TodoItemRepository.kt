package com.example.makeitso.data.repository

import com.example.makeitso.data.datasource.TodoItemRemoteDataSource
import com.example.makeitso.data.model.TodoItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import android.util.Log

class TodoItemRepository @Inject constructor(
    private val todoItemRemoteDataSource: TodoItemRemoteDataSource
) {
    fun getTodoItems(currentUserIdFlow: Flow<String?>): Flow<List<TodoItem>> {
        Log.d("TodoItemRepository", "getTodoItems called.")
        return todoItemRemoteDataSource.getTodoItems(currentUserIdFlow)
    }

    suspend fun getTodoItem(itemId: String): TodoItem? {
        Log.d("TodoItemRepository", "getTodoItem called with itemId: $itemId")
        return todoItemRemoteDataSource.getTodoItem(itemId)
    }

    suspend fun create(todoItem: TodoItem): String {
        Log.d("TodoItemRepository", "TODO 아이템 생성: $todoItem")
        val newId = todoItemRemoteDataSource.create(todoItem)
        Log.d("TodoItemRepository", "TODO 아이템 생성 완료, ID: $newId")
        return newId
    }

    suspend fun update(todoItem: TodoItem) {
        Log.d("TodoItemRepository", "TODO 아이템 업데이트: $todoItem")
        todoItemRemoteDataSource.update(todoItem)
        Log.d("TodoItemRepository", "TODO 아이템 업데이트 완료")
    }

    suspend fun delete(itemId: String) {
        Log.d("TodoItemRepository", "delete called with itemId: $itemId")
        todoItemRemoteDataSource.delete(itemId)
    }

    suspend fun getAllTodoItems(userId: String): List<TodoItem> {
        Log.d("TodoItemRepository", "getAllTodoItems called for userId: $userId")
        return todoItemRemoteDataSource.getAllTodoItems(userId)
    }
}
