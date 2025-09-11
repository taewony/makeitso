package com.example.makeitso.data.datasource

import android.util.Log
import com.example.makeitso.data.dao.TodoItemDao
import com.example.makeitso.data.mapper.TodoItemMapper
import com.example.makeitso.data.model.TodoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoItemRemoteDataSource @Inject constructor(
    private val todoItemDao: TodoItemDao
) {

    fun getTodoItems(currentUserIdFlow: Flow<String?>): Flow<List<TodoItem>> {
        Log.d("TodoItemRemoteDataSource", "getTodoItems called with Room DB")
        return currentUserIdFlow.flatMapLatest { ownerId ->
            if (ownerId != null) {
                todoItemDao.getTodoItemsFlow(ownerId).map { entities ->
                    entities.map { TodoItemMapper.toDomain(it) }
                }
            } else {
                flowOf(emptyList())
            }
        }
    }

    suspend fun getTodoItem(itemId: String): TodoItem? {
        Log.d("TodoItemRemoteDataSource", "getTodoItem called with itemId: $itemId")
        return todoItemDao.getTodoItem(itemId)?.let { TodoItemMapper.toDomain(it) }
    }

    suspend fun create(todoItem: TodoItem): String {
        Log.d("TodoItemRemoteDataSource", "create called with todoItem: $todoItem")
        val itemId = if (todoItem.id.isEmpty()) {
            System.currentTimeMillis().toString()
        } else {
            todoItem.id
        }
        
        val entity = TodoItemMapper.toEntity(todoItem.copy(id = itemId))
        todoItemDao.insertTodoItem(entity)
        Log.d("TodoItemRemoteDataSource", "TodoItem created in Room DB with id: $itemId")
        return itemId
    }

    suspend fun update(todoItem: TodoItem) {
        Log.d("TodoItemRemoteDataSource", "update called with todoItem: $todoItem")
        val entity = TodoItemMapper.toEntity(todoItem)
        todoItemDao.updateTodoItem(entity)
        Log.d("TodoItemRemoteDataSource", "TodoItem updated in Room DB")
    }

    suspend fun delete(itemId: String) {
        Log.d("TodoItemRemoteDataSource", "delete called with itemId: $itemId")
        todoItemDao.deleteTodoItem(itemId)
        Log.d("TodoItemRemoteDataSource", "TodoItem deleted from Room DB")
    }

    suspend fun getAllTodoItems(userId: String): List<TodoItem> {
        Log.d("TodoItemRemoteDataSource", "getAllTodoItems called for userId: $userId")
        return todoItemDao.getTodoItemsFlow(userId).first().map { entity ->
            TodoItemMapper.toDomain(entity)
        }
    }

    companion object {
        private const val OWNER_ID_FIELD = "ownerId"
        private const val TODO_ITEMS_COLLECTION = "todoitems"
    }
}
