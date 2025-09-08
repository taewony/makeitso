package com.example.makeitso.data.datasource

import com.example.makeitso.data.model.TodoItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import android.util.Log

class TodoItemRemoteDataSource(private val firestore: FirebaseFirestore?) {
    private val inMemoryTodoItems = MutableStateFlow<List<TodoItem>>(emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
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

    companion object {
        private const val OWNER_ID_FIELD = "ownerId"
        private const val TODO_ITEMS_COLLECTION = "todoitems"
    }
}
