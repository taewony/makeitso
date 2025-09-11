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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.font.FontFamily
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
    openOnboardingScreen: () -> Unit = {},
    openSignUpScreen: () -> Unit = {},
    openSignInScreen: () -> Unit = {},
    viewModel: TodoListViewModel = hiltViewModel()
) {
    val isLoadingUser by viewModel.isLoadingUser.collectAsStateWithLifecycle()
    val showAiNudgeDialog by viewModel.showAiNudgeDialog.collectAsStateWithLifecycle()
    val aiNudgeMessage by viewModel.aiNudgeMessage.collectAsStateWithLifecycle()
    val showPromptDialog by viewModel.showPromptDialog.collectAsStateWithLifecycle()
    val currentPrompt by viewModel.currentPrompt.collectAsStateWithLifecycle()
    val needsOnboarding by viewModel.needsOnboarding.collectAsStateWithLifecycle()
    val needsSignUp by viewModel.needsSignUp.collectAsStateWithLifecycle()
    val needsSignIn by viewModel.needsSignIn.collectAsStateWithLifecycle()

    LaunchedEffect(needsSignUp) {
        if (needsSignUp && !isLoadingUser) {
            openSignUpScreen()
        }
    }

    LaunchedEffect(needsSignIn) {
        if (needsSignIn && !isLoadingUser) {
            openSignInScreen()
        }
    }

    LaunchedEffect(needsOnboarding) {
        if (needsOnboarding && !isLoadingUser && !needsSignUp) {
            openOnboardingScreen()
        }
    }

    if (isLoadingUser) {
        LoadingIndicator()
    } else if (!needsSignUp && !needsSignIn && !needsOnboarding) {
        val todoItems = viewModel.todoItems.collectAsStateWithLifecycle(emptyList())

        TodoListScreenContent(
            todoItems = todoItems.value,
            showAiNudgeDialog = showAiNudgeDialog,
            aiNudgeMessage = aiNudgeMessage,
            showPromptDialog = showPromptDialog,
            currentPrompt = currentPrompt,
            onNudgeClick = viewModel::onNudgeButtonClick,
            onDialogDismiss = viewModel::onDialogDismiss,
            onShowPrompt = viewModel::showPrompt,
            onHidePrompt = viewModel::hidePrompt,
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
    aiNudgeMessage: String,
    showPromptDialog: Boolean,
    currentPrompt: String,
    onNudgeClick: () -> Unit,
    onDialogDismiss: () -> Unit,
    onShowPrompt: () -> Unit,
    onHidePrompt: () -> Unit,
    openSettingsScreen: () -> Unit,
    openTodoItemScreen: (String) -> Unit,
    updateItem: (todoItem: TodoItem) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val clipboardManager = LocalClipboardManager.current

    // 개선된 AI Nudge 다이얼로그 (확인+ 버튼 포함)
    if (showAiNudgeDialog) {
        AlertDialog(
            onDismissRequest = onDialogDismiss,
            title = { Text("AI 비서의 잔소리 💬") },
            text = { 
                Text(aiNudgeMessage.ifEmpty { "AI 비서가 잔소리를 준비 중입니다..." }) 
            },
            confirmButton = {
                TextButton(onClick = onDialogDismiss) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(onClick = onShowPrompt) {
                    Text("확인+")
                }
            }
        )
    }

    // 프롬프트 보기 다이얼로그
    if (showPromptDialog) {
        AlertDialog(
            onDismissRequest = onHidePrompt,
            title = { Text("AI 프롬프트 📋") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = currentPrompt,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onHidePrompt) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(currentPrompt))
                        // 복사 완료 피드백은 간단하게 로그로 처리
                        android.util.Log.d("TodoListScreen", "프롬프트가 클립보드에 복사되었습니다")
                    }
                ) {
                    Text("복사")
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
            aiNudgeMessage = "",
            showPromptDialog = false,
            currentPrompt = "",
            onNudgeClick = {},
            onDialogDismiss = {},
            onShowPrompt = {},
            onHidePrompt = {},
            openSettingsScreen = {},
            openTodoItemScreen = {},
            updateItem = {}
        )
    }
}
