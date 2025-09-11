package com.example.makeitso.data.datasource

import com.example.makeitso.data.model.TodoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import android.util.Log

class TodoItemRemoteDataSource {
    private val inMemoryTodoItems = MutableStateFlow<List<TodoItem>>(emptyList())

    fun getTodoItems(currentUserIdFlow: Flow<String?>): Flow<List<TodoItem>> {
        Log.d("TodoItemRemoteDataSource", "getTodoItems called. Current inMemoryTodoItems: ${inMemoryTodoItems.value}")
        // return currentUserIdFlow.flatMapLatest { ownerId ->
        //     firestore
        //         .collection(TODO_ITEMS_COLLECTION)
        //         .whereEqualTo(OWNER_ID_FIELD, ownerId)
        //         .dataObjects()
        // }
        return inMemoryTodoItems
    }

    suspend fun getTodoItem(itemId: String): TodoItem? {
        Log.d("TodoItemRemoteDataSource", "getTodoItem called with itemId: $itemId")
        // return firestore.collection(TODO_ITEMS_COLLECTION).document(itemId).get().await().toObject()
        return inMemoryTodoItems.value.firstOrNull { it.id == itemId }
    }

    suspend fun create(todoItem: TodoItem): String {
        Log.d("TodoItemRemoteDataSource", "create called with todoItem: $todoItem")
        // return firestore.collection(TODO_ITEMS_COLLECTION).add(todoItem).await().id
        val newTodoItem = todoItem.copy(id = System.currentTimeMillis().toString())
        inMemoryTodoItems.value = inMemoryTodoItems.value + newTodoItem
        Log.d("TodoItemRemoteDataSource", "After create, inMemoryTodoItems: ${inMemoryTodoItems.value}")
        return newTodoItem.id
    }

    suspend fun update(todoItem: TodoItem) {
        Log.d("TodoItemRemoteDataSource", "update called with todoItem: $todoItem")
        // firestore.collection(TODO_ITEMS_COLLECTION).document(todoItem.id).set(todoItem).await()
        val updatedList = inMemoryTodoItems.value.filterNot { it.id == todoItem.id } + todoItem
        inMemoryTodoItems.value = updatedList
        Log.d("TodoItemRemoteDataSource", "After update, inMemoryTodoItems: ${inMemoryTodoItems.value}")
    }

    suspend fun delete(itemId: String) {
        Log.d("TodoItemRemoteDataSource", "delete called with itemId: $itemId")
        // firestore.collection(TODO_ITEMS_COLLECTION).document(itemId).delete().await()
        inMemoryTodoItems.value = inMemoryTodoItems.value.filterNot { it.id == itemId }
        Log.d("TodoItemRemoteDataSource", "After delete, inMemoryTodoItems: ${inMemoryTodoItems.value}")
    }

    suspend fun getAllTodoItems(userId: String): List<TodoItem> {
        Log.d("TodoItemRemoteDataSource", "getAllTodoItems called for userId: $userId")
        // Phase 1에서는 메모리 기반이므로 userId 필터링 없이 모든 아이템 반환
        // Phase 3에서는 Firebase에서 userId로 필터링된 결과 반환
        return inMemoryTodoItems.value.filter { it.ownerId == userId }
    }

    companion object {
        private const val OWNER_ID_FIELD = "ownerId"
        private const val TODO_ITEMS_COLLECTION = "todoitems"
    }
}
