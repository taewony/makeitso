package com.example.makeitso.ui.todoitem

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.makeitso.R
import com.example.makeitso.data.model.ErrorMessage
import com.example.makeitso.data.model.Priority
import com.example.makeitso.data.model.Priority.HIGH
import com.example.makeitso.data.model.Priority.LOW
import com.example.makeitso.data.model.Priority.MEDIUM
import com.example.makeitso.data.model.Priority.NONE
import com.example.makeitso.data.model.TodoItem
import com.example.makeitso.data.model.isHighPriority
import com.example.makeitso.data.model.isLowPriority
import com.example.makeitso.data.model.isMediumPriority
import com.example.makeitso.data.model.isNonePriority
import com.example.makeitso.ui.shared.AppSwitch
import com.example.makeitso.ui.shared.CenterTopAppBar
import com.example.makeitso.ui.shared.LoadingIndicator
import com.example.makeitso.ui.shared.StandardButton
import com.example.makeitso.ui.theme.DarkGrey
import com.example.makeitso.ui.theme.MakeItSoTheme
import com.example.makeitso.ui.theme.MediumGrey
import com.example.makeitso.ui.theme.MediumYellow
import kotlinx.serialization.Serializable

@Serializable
data class TodoItemRoute(val itemId: String)

@Composable
fun TodoItemScreen(
    openTodoListScreen: () -> Unit,
    showErrorSnackbar: (ErrorMessage) -> Unit,
    viewModel: TodoItemViewModel = hiltViewModel()
) {
    val navigateToTodoList by viewModel.navigateToTodoList.collectAsStateWithLifecycle()
    val showAutoAiNudge by viewModel.showAutoAiNudge.collectAsStateWithLifecycle()
    val autoAiNudgeMessage by viewModel.autoAiNudgeMessage.collectAsStateWithLifecycle()
    val showAutoPromptDialog by viewModel.showAutoPromptDialog.collectAsStateWithLifecycle()
    val currentAutoPrompt by viewModel.currentAutoPrompt.collectAsStateWithLifecycle()

    if (navigateToTodoList) {
        openTodoListScreen()
    } else {
        val todoItem by viewModel.todoItem.collectAsStateWithLifecycle()

        TodoItemScreen(
            todoItem = todoItem,
            showErrorSnackbar = showErrorSnackbar,
            saveItem = viewModel::saveItem,
            deleteItem = viewModel::deleteItem,
            loadItem = viewModel::loadItem,
            showAutoAiNudge = showAutoAiNudge,
            autoAiNudgeMessage = autoAiNudgeMessage,
            showAutoPromptDialog = showAutoPromptDialog,
            currentAutoPrompt = currentAutoPrompt,
            dismissAutoAiNudge = viewModel::dismissAutoAiNudge,
            showAutoPrompt = viewModel::showAutoPrompt,
            hideAutoPrompt = viewModel::hideAutoPrompt
        )
    }
}

@Composable
fun TodoItemScreen(
    todoItem: TodoItem?,
    showErrorSnackbar: (ErrorMessage) -> Unit,
    saveItem: (TodoItem, (ErrorMessage) -> Unit) -> Unit,
    deleteItem: (TodoItem) -> Unit,
    loadItem: () -> Unit,
    showAutoAiNudge: Boolean,
    autoAiNudgeMessage: String,
    showAutoPromptDialog: Boolean,
    currentAutoPrompt: String,
    dismissAutoAiNudge: () -> Unit,
    showAutoPrompt: () -> Unit,
    hideAutoPrompt: () -> Unit
) {
    if (todoItem == null) {
        LoadingIndicator()
    } else {
        TodoItemScreenContent(
            todoItem = todoItem,
            showErrorSnackbar = showErrorSnackbar,
            saveItem = saveItem,
            deleteItem = deleteItem
        )
    }

    // 자동 AI Nudge 다이얼로그
    if (showAutoAiNudge) {
        EnhancedAutoAiNudgeDialog(
            message = autoAiNudgeMessage,
            onDismiss = dismissAutoAiNudge,
            onShowPrompt = showAutoPrompt
        )
    }

    // 자동 AI 프롬프트 다이얼로그
    if (showAutoPromptDialog) {
        AutoPromptDialog(
            prompt = currentAutoPrompt,
            onDismiss = hideAutoPrompt
        )
    }

    LaunchedEffect(true) {
        loadItem()
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TodoItemScreenContent(
    todoItem: TodoItem,
    showErrorSnackbar: (ErrorMessage) -> Unit,
    saveItem: (TodoItem, (ErrorMessage) -> Unit) -> Unit,
    deleteItem: (TodoItem) -> Unit
) {
    val editableItem = remember { mutableStateOf(todoItem) }
    val backgroundColor = if (isSystemInDarkTheme()) MediumGrey else MediumYellow
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = stringResource(R.string.edit_todo_item),
                icon = Icons.Filled.Check,
                iconDescription = "Save Todo Item icon",
                action = {
                    saveItem(editableItem.value, showErrorSnackbar)
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = 4.dp,
                    end = 4.dp,
                    bottom = 4.dp
                )
        ) {
            Spacer(Modifier.size(24.dp))

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                value = editableItem.value.title,
                onValueChange = { editableItem.value = editableItem.value.copy(title = it) },
                label = { Text(stringResource(R.string.title)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                    autoCorrect = true
                ),
                singleLine = false,
                maxLines = 3
            )

            Spacer(Modifier.size(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(48f))
                    .background(backgroundColor),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(16.dp).weight(1f),
                    text = stringResource(R.string.flag)
                )

                AppSwitch(editableItem.value.flagged) {
                    editableItem.value = editableItem.value.copy(flagged = it)
                }
            }

            Spacer(Modifier.size(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(48f))
                    .background(backgroundColor)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                    text = stringResource(R.string.priority)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PriorityChip(HIGH, editableItem.value.isHighPriority) {
                        editableItem.value = editableItem.value.copy(
                            priority = HIGH.value
                        )
                    }

                    PriorityChip(MEDIUM, editableItem.value.isMediumPriority) {
                        editableItem.value = editableItem.value.copy(
                            priority = MEDIUM.value
                        )
                    }

                    PriorityChip(LOW, editableItem.value.isLowPriority) {
                        editableItem.value = editableItem.value.copy(
                            priority = LOW.value
                        )
                    }

                    PriorityChip(NONE, editableItem.value.isNonePriority) {
                        editableItem.value = editableItem.value.copy(
                            priority = NONE.value
                        )
                    }
                }
            }

            Spacer(Modifier.size(24.dp))

            StandardButton(
                label = R.string.delete_todo_item,
                onButtonClick = {
                    deleteItem(todoItem)
                }
            )
        }
    }
}

@Composable
fun PriorityChip(
    priority: Priority,
    selected: Boolean,
    onPrioritySelected: () -> Unit
) {
    FilterChip(
        onClick = {
            if (selected) return@FilterChip
            else onPrioritySelected()
        },
        label = { Text(priority.title) },
        selected = selected,
        colors = getPriorityChipColors(priority.selectedColor),
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else null
    )
}

@Composable
private fun getPriorityChipColors(selectedColor: Color): SelectableChipColors {
    val containerColor = if (isSystemInDarkTheme()) MediumGrey else MediumYellow
    val contentColor = if (isSystemInDarkTheme()) Color.White else DarkGrey

    return SelectableChipColors(
        containerColor = containerColor,
        labelColor = contentColor,
        leadingIconColor = contentColor,
        trailingIconColor = contentColor,
        disabledContainerColor = containerColor,
        disabledLabelColor = contentColor,
        disabledLeadingIconColor = contentColor,
        disabledTrailingIconColor = contentColor,
        selectedContainerColor = selectedColor,
        disabledSelectedContainerColor = DarkGrey,
        selectedLabelColor = DarkGrey,
        selectedLeadingIconColor = DarkGrey,
        selectedTrailingIconColor = DarkGrey
    )
}

@Composable
fun EnhancedAutoAiNudgeDialog(
    message: String,
    onDismiss: () -> Unit,
    onShowPrompt: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("AI 비서의 한마디 💬") },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
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

@Composable
fun AutoPromptDialog(
    prompt: String,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("AI 프롬프트 📋") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = prompt,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("확인")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    clipboardManager.setText(AnnotatedString(prompt))
                    android.util.Log.d("TodoItemScreen", "프롬프트가 클립보드에 복사되었습니다")
                }
            ) {
                Text("복사")
            }
        }
    )
}

@Composable
@Preview(showSystemUi = true)
fun TodoItemScreenPreview() {
    MakeItSoTheme(darkTheme = true) {
        TodoItemScreenContent(
            todoItem = TodoItem(),
            showErrorSnackbar = {},
            saveItem = { _, _ -> },
            deleteItem = {}
        )
    }
}
