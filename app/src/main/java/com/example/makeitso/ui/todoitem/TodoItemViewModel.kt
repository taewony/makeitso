package com.example.makeitso.ui.todoitem

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.example.makeitso.MainViewModel
import com.example.makeitso.R
import com.example.makeitso.data.model.ErrorMessage
import com.example.makeitso.data.model.TodoItem
import com.example.makeitso.data.repository.AuthRepository
import com.example.makeitso.data.repository.TodoItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import android.util.Log

@HiltViewModel
class TodoItemViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val todoItemRepository: TodoItemRepository
) : MainViewModel() {
    private val _navigateToTodoList = MutableStateFlow(false)
    val navigateToTodoList: StateFlow<Boolean>
        get() = _navigateToTodoList.asStateFlow()

    private val todoItemRoute = savedStateHandle.toRoute<TodoItemRoute>()
    private val itemId: String = todoItemRoute.itemId

    private val _todoItem = MutableStateFlow<TodoItem?>(null)
    val todoItem: StateFlow<TodoItem?>
        get() = _todoItem.asStateFlow()

    fun loadItem() {
        Log.d("TodoItemViewModel", "loadItem called with itemId: $itemId")
        launchCatching {
            if (itemId.isBlank()) {
                _todoItem.value = TodoItem()
                Log.d("TodoItemViewModel", "New TodoItem created: ${_todoItem.value}")
            } else {
                _todoItem.value = todoItemRepository.getTodoItem(itemId)
                Log.d("TodoItemViewModel", "Loaded TodoItem: ${_todoItem.value}")
            }
        }
    }

    fun saveItem(
        item: TodoItem,
        showErrorSnackbar: (ErrorMessage) -> Unit
    ) {
        val ownerId = authRepository.currentUserIdFlow.value
        Log.d("TodoItemViewModel", "saveItem called with ownerId: $ownerId, item: $item")

        if (ownerId.isNullOrBlank()) {
            showErrorSnackbar(ErrorMessage.IdError(R.string.could_not_find_account))
            Log.e("TodoItemViewModel", "Owner ID is null or blank.")
            return
        }

        if (item.title.isBlank()) {
            showErrorSnackbar(ErrorMessage.IdError(R.string.item_without_title))
            Log.e("TodoItemViewModel", "Item title is blank.")
            return
        }

        launchCatching {
            if (itemId.isBlank()) {
                val newId = todoItemRepository.create(item.copy(ownerId = ownerId))
                Log.d("TodoItemViewModel", "Created new TodoItem with ID: $newId")
            } else {
                todoItemRepository.update(item)
                Log.d("TodoItemViewModel", "Updated TodoItem: $item")
            }

            _navigateToTodoList.value = true
            Log.d("TodoItemViewModel", "Navigation to TodoList triggered.")
        }
    }

    fun deleteItem(item: TodoItem) {
        Log.d("TodoItemViewModel", "deleteItem called with item: $item")
        launchCatching {
            if (itemId.isNotBlank()) {
                todoItemRepository.delete(item.id)
                Log.d("TodoItemViewModel", "Deleted TodoItem with ID: ${item.id}")
            }
            _navigateToTodoList.value = true
            Log.d("TodoItemViewModel", "Navigation to TodoList triggered after deletion.")
        }
    }
}
