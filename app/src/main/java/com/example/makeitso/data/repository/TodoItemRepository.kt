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
        Log.d("TodoItemRepository", "create called with todoItem: $todoItem")
        return todoItemRemoteDataSource.create(todoItem)
    }

    suspend fun update(todoItem: TodoItem) {
        Log.d("TodoItemRepository", "update called with todoItem: $todoItem")
        todoItemRemoteDataSource.update(todoItem)
    }

    suspend fun delete(itemId: String) {
        Log.d("TodoItemRepository", "delete called with itemId: $itemId")
        todoItemRemoteDataSource.delete(itemId)
    }
}
