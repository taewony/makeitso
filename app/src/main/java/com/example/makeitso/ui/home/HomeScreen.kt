package com.example.makeitso.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.makeitso.R
import com.example.makeitso.data.model.TodoList
import com.example.makeitso.ui.shared.CenterTopAppBar
import com.example.makeitso.ui.shared.LoadingIndicator
import com.example.makeitso.ui.theme.DarkBlue
import com.example.makeitso.ui.theme.MakeItSoTheme
import com.example.makeitso.ui.theme.MediumYellow
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Composable
fun HomeScreen(
    openSettingsScreen: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val isLoadingUser by viewModel.isLoadingUser.collectAsStateWithLifecycle()
    val showAiNudgeDialog by viewModel.showAiNudgeDialog.collectAsStateWithLifecycle()

    if (isLoadingUser) {
        LoadingIndicator()
    } else {
        HomeScreenContent(
            openSettingsScreen = openSettingsScreen,
            showAiNudgeDialog = showAiNudgeDialog,
            onNudgeClick = { viewModel.onNudgeButtonClick() },
            onDialogDismiss = { viewModel.onDialogDismiss() }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeScreenContent(
    openSettingsScreen: () -> Unit,
    showAiNudgeDialog: Boolean,
    onNudgeClick: () -> Unit,
    onDialogDismiss: () -> Unit
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
                    text = { Text(text = stringResource(R.string.create_todo_list)) },
                    onClick = { }
                )
                ExtendedFloatingActionButton(
                    containerColor = DarkBlue,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Filled.AutoAwesome, "AI Nudge button") },
                    text = { Text(text = "AI Nudge") },
                    onClick = onNudgeClick
                )
            }
        }
    ) { innerPadding ->
        val listState = rememberLazyListState()

        LazyColumn(
            state = listState,
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(listOf(TodoList(title = "1"), TodoList(title = "2"))) { todoList ->
                //TODO: Use uiState
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(48f))
                        .background(MediumYellow)
                        .clickable { }
                ) {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = todoList.title
                    )
                }

                Spacer(Modifier.padding(4.dp))
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun HomeScreenPreview() {
    MakeItSoTheme(darkTheme = true) {
        HomeScreenContent(
            openSettingsScreen = {},
            showAiNudgeDialog = false,
            onNudgeClick = {},
            onDialogDismiss = {}
        )
    }
}
