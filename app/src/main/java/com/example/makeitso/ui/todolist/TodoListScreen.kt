package com.example.makeitso.ui.todolist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.makeitso.R
import com.example.makeitso.data.model.TodoItem
import com.example.makeitso.ui.shared.CenterTopAppBar
import com.example.makeitso.ui.shared.LoadingIndicator
import com.example.makeitso.ui.theme.DarkBlue
import com.example.makeitso.ui.theme.DarkGrey
import com.example.makeitso.ui.theme.LightGreen
import com.example.makeitso.ui.theme.MakeItSoTheme
import kotlinx.serialization.Serializable

@Serializable
object TodoListRoute

@Composable
fun TodoListScreen(
    openSettingsScreen: () -> Unit,
    openTodoItemScreen: (String) -> Unit,
    viewModel: TodoListViewModel = hiltViewModel()
) {
    val isLoadingUser by viewModel.isLoadingUser.collectAsStateWithLifecycle()
    val showAiNudgeDialog by viewModel.showAiNudgeDialog.collectAsStateWithLifecycle()

    if (isLoadingUser) {
        LoadingIndicator()
    } else {
        val todoItems = viewModel.todoItems.collectAsStateWithLifecycle(emptyList())

        TodoListScreenContent(
            todoItems = todoItems.value,
            showAiNudgeDialog = showAiNudgeDialog,
            onNudgeClick = viewModel::onNudgeButtonClick,
            onDialogDismiss = viewModel::onDialogDismiss,
            openSettingsScreen = openSettingsScreen,
            openTodoItemScreen = openTodoItemScreen,
            updateItem = viewModel::updateItem
        )
    }

    LaunchedEffect(true) {
        viewModel.loadCurrentUser()
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TodoListScreenContent(
    todoItems: List<TodoItem>,
    showAiNudgeDialog: Boolean,
    onNudgeClick: () -> Unit,
    onDialogDismiss: () -> Unit,
    openSettingsScreen: () -> Unit,
    openTodoItemScreen: (String) -> Unit,
    updateItem: (todoItem: TodoItem) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    if (showAiNudgeDialog) {
        AlertDialog(
            onDismissRequest = onDialogDismiss,
            title = { Text("AI Nudge") },
            text = { Text("AI 비서가 잔소리를 시작합니다.") },
            confirmButton = {
                TextButton(onClick = onDialogDismiss) {
                    Text("확인")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = stringResource(R.string.app_name),
                icon = Icons.Filled.Settings,
                iconDescription = "Settings screen icon",
                action = openSettingsScreen,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ExtendedFloatingActionButton(
                    containerColor = DarkBlue,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Filled.Add, "Create list button") },
                    text = { Text(text = stringResource(R.string.create_todo_item)) },
                    onClick = { openTodoItemScreen("") }
                )
                ExtendedFloatingActionButton(
                    containerColor = LightGreen,
                    contentColor = DarkGrey,
                    icon = { Icon(Icons.Filled.AutoAwesome, "AI Nudge button") },
                    text = { Text(text = "AI Nudge") },
                    onClick = onNudgeClick
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(todoItems) { todoItem ->
                TodoItem(todoItem, openTodoItemScreen) {
                    updateItem(todoItem.copy(completed = it))
                }
            }
        }
    }
}

@Composable
fun TodoItem(
    todoItem: TodoItem,
    openTodoItemScreen: (String) -> Unit,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = todoItem.completed,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = DarkBlue)
        )

        Text(
            text = todoItem.title,
            modifier = Modifier.weight(1f).clickable(
                interactionSource = null,
                indication = null
            ) { openTodoItemScreen(todoItem.id) }
        )

        if (todoItem.flagged) {
            Icon(
                painter = painterResource(R.drawable.ic_flag),
                contentDescription = "Flag icon",
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun TodoListScreenPreview() {
    MakeItSoTheme(darkTheme = true) {
        TodoListScreenContent(
            todoItems = listOf(TodoItem()),
            showAiNudgeDialog = false,
            onNudgeClick = {},
            onDialogDismiss = {},
            openSettingsScreen = {},
            openTodoItemScreen = {},
            updateItem = {}
        )
    }
}
