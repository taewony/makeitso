package com.example.makeitso.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.makeitso.R
import com.example.makeitso.data.model.AiCharacter
import com.example.makeitso.ui.theme.MakeItSoTheme
import kotlinx.serialization.Serializable

@Serializable
object OnboardingRoute

@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isComplete) {
        println("OnboardingScreen: LaunchedEffect triggered, isComplete=${uiState.isComplete}")
        if (uiState.isComplete) {
            println("OnboardingScreen: Calling onOnboardingComplete()")
            onOnboardingComplete()
        }
    }

    OnboardingScreenContent(
        uiState = uiState,
        onShortTermGoalChange = viewModel::updateShortTermGoal,
        onLongTermGoalChange = viewModel::updateLongTermGoal,
        onCharacterSelect = viewModel::selectCharacter,
        onComplete = viewModel::completeOnboarding
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreenContent(
    uiState: OnboardingUiState,
    onShortTermGoalChange: (String) -> Unit,
    onLongTermGoalChange: (String) -> Unit,
    onCharacterSelect: (AiCharacter) -> Unit,
    onComplete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 헤더
        Text(
            text = "AI 비서 설정",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "개인 목표와 AI 캐릭터를 설정해주세요",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // 목표 설정 섹션
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "개인 목표 설정",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = uiState.shortTermGoal,
                    onValueChange = onShortTermGoalChange,
                    label = { Text("단기 목표") },
                    placeholder = { Text("예: 이번 달 운동 10회 하기") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = uiState.longTermGoal,
                    onValueChange = onLongTermGoalChange,
                    label = { Text("장기 목표") },
                    placeholder = { Text("예: 1년 내 건강한 체중 달성하기") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }

        // AI 캐릭터 선택 섹션
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "AI 캐릭터 선택",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "당신을 동기부여할 AI 캐릭터를 선택하세요",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                AiCharacter.values().forEach { character ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = uiState.selectedCharacter == character,
                                onClick = { onCharacterSelect(character) }
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = uiState.selectedCharacter == character,
                            onClick = { onCharacterSelect(character) }
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
        }

        // 완료 버튼
        Button(
            onClick = onComplete,
            enabled = uiState.canComplete,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "설정 완료",
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (!uiState.canComplete) {
            Text(
                text = "모든 항목을 입력해주세요",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    MakeItSoTheme {
        OnboardingScreenContent(
            uiState = OnboardingUiState(
                shortTermGoal = "운동하기",
                longTermGoal = "건강해지기",
                selectedCharacter = AiCharacter.HARSH_CRITIC
            ),
            onShortTermGoalChange = {},
            onLongTermGoalChange = {},
            onCharacterSelect = {},
            onComplete = {}
        )
    }
}