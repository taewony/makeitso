package com.example.makeitso.data.datasource

import com.example.makeitso.data.model.TodoList
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Date

class TodoListRemoteDataSource(private val firestore: FirebaseFirestore?) {
    private val inMemoryTodoLists = MutableStateFlow<List<TodoList>>(emptyList())

    fun getTodoLists(userId: String): Flow<List<TodoList>> {
        // return firestore.collection(TODO_LIST_COLLECTION)
        //     .whereEqualTo(USER_ID_FIELD, userId)
        //     .orderBy(CREATED_AT_FIELD, Query.Direction.DESCENDING)
        //     .dataObjects()
        return inMemoryTodoLists
    }

    suspend fun getTodoList(listId: String): TodoList? {
        // return firestore.collection(TODO_LIST_COLLECTION).document(listId).get().await().toObject()
        return inMemoryTodoLists.value.firstOrNull { it.id == listId }
    }

    suspend fun create(todoList: TodoList): String {
        // return firestore.collection(TODO_LIST_COLLECTION).add(todoList).await().id
        val newTodoList = todoList.copy(id = System.currentTimeMillis().toString())
        inMemoryTodoLists.value = inMemoryTodoLists.value + newTodoList
        return newTodoList.id
    }

    suspend fun update(todoList: TodoList) {
        // firestore.collection(TODO_LIST_COLLECTION).document(todoList.id).set(todoList).await()
        val updatedList = inMemoryTodoLists.value.filterNot { it.id == todoList.id } + todoList
        inMemoryTodoLists.value = updatedList
    }

    suspend fun delete(listId: String) {
        // firestore.collection(TODO_LIST_COLLECTION).document(listId).delete().await()
        inMemoryTodoLists.value = inMemoryTodoLists.value.filterNot { it.id == listId }
    }

    companion object {
        private const val USER_ID_FIELD = "userId"
        private const val CREATED_AT_FIELD = "createdAt"
        private const val TODO_LIST_COLLECTION = "todolist"
    }
}
