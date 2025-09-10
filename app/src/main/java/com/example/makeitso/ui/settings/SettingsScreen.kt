package com.example.makeitso.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.makeitso.R
import com.example.makeitso.data.model.AiCharacter
import com.example.makeitso.data.model.UserGoals
import com.example.makeitso.data.model.UserProfile
import com.example.makeitso.ui.shared.CenterTopAppBar
import com.example.makeitso.ui.shared.StandardButton
import com.example.makeitso.ui.shared.StandardTextButton
import com.example.makeitso.ui.theme.DarkBlue
import com.example.makeitso.ui.theme.DarkGrey
import com.example.makeitso.ui.theme.LightRed
import com.example.makeitso.ui.theme.MakeItSoTheme
import kotlinx.serialization.Serializable

@Serializable
object SettingsRoute

@Composable
fun SettingsScreen(
    openHomeScreen: () -> Unit,
    openSignInScreen: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val shouldRestartApp by viewModel.shouldRestartApp.collectAsStateWithLifecycle()

    if (shouldRestartApp) {
        openHomeScreen()
    } else {
        val isAnonymous by viewModel.isAnonymous.collectAsStateWithLifecycle()
        val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
        val showGoalsDialog by viewModel.showGoalsDialog.collectAsStateWithLifecycle()
        val showCharacterDialog by viewModel.showCharacterDialog.collectAsStateWithLifecycle()
        val showHistoryDialog by viewModel.showHistoryDialog.collectAsStateWithLifecycle()

        SettingsScreenContent(
            loadCurrentUser = viewModel::loadCurrentUser,
            openSignInScreen = openSignInScreen,
            signOut = viewModel::signOut,
            deleteAccount = viewModel::deleteAccount,
            isAnonymous = isAnonymous,
            userProfile = userProfile,
            showGoalsDialog = showGoalsDialog,
            showCharacterDialog = showCharacterDialog,
            showHistoryDialog = showHistoryDialog,
            onShowGoalsDialog = viewModel::showGoalsDialog,
            onHideGoalsDialog = viewModel::hideGoalsDialog,
            onShowCharacterDialog = viewModel::showCharacterDialog,
            onHideCharacterDialog = viewModel::hideCharacterDialog,
            onShowHistoryDialog = viewModel::showHistoryDialog,
            onHideHistoryDialog = viewModel::hideHistoryDialog,
            onUpdateGoals = viewModel::updateGoals,
            onUpdateCharacter = viewModel::updateCharacter
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SettingsScreenContent(
    loadCurrentUser: () -> Unit,
    openSignInScreen: () -> Unit,
    signOut: () -> Unit,
    deleteAccount: () -> Unit,
    isAnonymous: Boolean,
    userProfile: UserProfile?,
    showGoalsDialog: Boolean,
    showCharacterDialog: Boolean,
    showHistoryDialog: Boolean,
    onShowGoalsDialog: () -> Unit,
    onHideGoalsDialog: () -> Unit,
    onShowCharacterDialog: () -> Unit,
    onHideCharacterDialog: () -> Unit,
    onShowHistoryDialog: () -> Unit,
    onHideHistoryDialog: () -> Unit,
    onUpdateGoals: (String, String) -> Unit,
    onUpdateCharacter: (AiCharacter) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(true) {
        loadCurrentUser()
    }

    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = stringResource(R.string.settings),
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

            if (isAnonymous) {
                StandardButton(
                    label = R.string.sign_in,
                    onButtonClick = {
                        openSignInScreen()
                    }
                )
            } else {
                // AI 설정 섹션
                if (userProfile != null) {
                    Text(
                        text = "AI 비서 설정",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    
                    Spacer(Modifier.size(16.dp))
                    
                    StandardTextButton(
                        label = "목표 수정",
                        onButtonClick = onShowGoalsDialog
                    )
                    
                    Spacer(Modifier.size(8.dp))
                    
                    StandardTextButton(
                        label = "AI 캐릭터 변경",
                        onButtonClick = onShowCharacterDialog
                    )
                    
                    Spacer(Modifier.size(8.dp))
                    
                    StandardTextButton(
                        label = "과거 기록 보기",
                        onButtonClick = onShowHistoryDialog
                    )
                    
                    Spacer(Modifier.size(24.dp))
                }

                StandardButton(
                    label = R.string.sign_out,
                    onButtonClick = {
                        signOut()
                    }
                )

                Spacer(Modifier.size(16.dp))

                DeleteAccountButton(deleteAccount)
            }
        }
        
        // 다이얼로그들
        if (showGoalsDialog && userProfile != null) {
            GoalsEditDialog(
                currentGoals = userProfile.goals,
                onDismiss = onHideGoalsDialog,
                onConfirm = onUpdateGoals
            )
        }
        
        if (showCharacterDialog && userProfile != null) {
            CharacterSelectDialog(
                currentCharacter = userProfile.selectedCharacter,
                onDismiss = onHideCharacterDialog,
                onConfirm = onUpdateCharacter
            )
        }
        
        if (showHistoryDialog) {
            HistoryDialog(
                onDismiss = onHideHistoryDialog
            )
        }
    }
}

@Composable
fun DeleteAccountButton(deleteAccount: () -> Unit) {
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    StandardButton(
        label = R.string.delete_account,
        onButtonClick = {
            showDeleteAccountDialog = true
        }
    )

    if (showDeleteAccountDialog) {
        AlertDialog(
            containerColor = LightRed,
            textContentColor = DarkBlue,
            titleContentColor = DarkBlue,
            title = { Text(stringResource(R.string.delete_account_title)) },
            text = { Text(stringResource(R.string.delete_account_description)) },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteAccountDialog = false },
                    colors = getDialogButtonColors()
                ) {
                    Text(text = stringResource(R.string.cancel), fontSize = 16.sp)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteAccountDialog = false
                        deleteAccount()
                    },
                    colors = getDialogButtonColors()
                ) {
                    Text(text = stringResource(R.string.delete), fontSize = 16.sp)
                }
            },
            onDismissRequest = { showDeleteAccountDialog = false }
        )
    }
}

private fun getDialogButtonColors(): ButtonColors {
    return ButtonColors(
        containerColor = LightRed,
        contentColor = DarkBlue,
        disabledContainerColor = LightRed,
        disabledContentColor = DarkGrey
    )
}

@Composable
@Preview(showSystemUi = true)
fun SettingsScreenPreview() {
    MakeItSoTheme(darkTheme = true) {
        SettingsScreenContent(
            loadCurrentUser = {},
            openSignInScreen = {},
            signOut = {},
            deleteAccount = {},
            isAnonymous = false,
            userProfile = UserProfile(
                userId = "test",
                goals = UserGoals("운동하기", "건강해지기"),
                selectedCharacter = AiCharacter.HARSH_CRITIC,
                isOnboardingComplete = true
            ),
            showGoalsDialog = false,
            showCharacterDialog = false,
            showHistoryDialog = false,
            onShowGoalsDialog = {},
            onHideGoalsDialog = {},
            onShowCharacterDialog = {},
            onHideCharacterDialog = {},
            onShowHistoryDialog = {},
            onHideHistoryDialog = {},
            onUpdateGoals = { _, _ -> },
            onUpdateCharacter = {}
        )
    }
}

@Composable
fun GoalsEditDialog(
    currentGoals: UserGoals,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var shortTermGoal by remember { mutableStateOf(currentGoals.shortTermGoal) }
    var longTermGoal by remember { mutableStateOf(currentGoals.longTermGoal) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("목표 수정") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = shortTermGoal,
                    onValueChange = { shortTermGoal = it },
                    label = { Text("단기 목표") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = longTermGoal,
                    onValueChange = { longTermGoal = it },
                    label = { Text("장기 목표") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(shortTermGoal, longTermGoal)
                },
                enabled = shortTermGoal.isNotBlank() && longTermGoal.isNotBlank()
            ) {
                Text("확인")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

@Composable
fun CharacterSelectDialog(
    currentCharacter: AiCharacter,
    onDismiss: () -> Unit,
    onConfirm: (AiCharacter) -> Unit
) {
    var selectedCharacter by remember { mutableStateOf(currentCharacter) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("AI 캐릭터 선택") },
        text = {
            Column {
                AiCharacter.values().forEach { character ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedCharacter == character,
                                onClick = { selectedCharacter = character }
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedCharacter == character,
                            onClick = { selectedCharacter = character }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = character.displayName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = character.promptPersona,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(selectedCharacter)
                }
            ) {
                Text("확인")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

@Composable
fun HistoryDialog(
    onDismiss: () -> Unit
) {
    var showCompletedTodos by remember { mutableStateOf(true) }
    var showAiMessages by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("과거 기록 보기") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("표시 옵션을 선택하세요:")
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = showCompletedTodos,
                        onCheckedChange = { showCompletedTodos = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("완료된 할 일 포함하여 보기")
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = showAiMessages,
                        onCheckedChange = { showAiMessages = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("과거 잔소리 메시지 함께 보기")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "선택된 옵션에 따라 과거 기록이 표시됩니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("확인")
            }
        }
    )
}