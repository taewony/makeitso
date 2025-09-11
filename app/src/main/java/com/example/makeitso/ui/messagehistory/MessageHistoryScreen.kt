package com.example.makeitso.ui.messagehistory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.makeitso.data.model.AiCharacter
import com.example.makeitso.data.model.AiMessage
import com.example.makeitso.data.model.TriggerType
import com.example.makeitso.ui.shared.CenterTopAppBar
import com.example.makeitso.ui.shared.LoadingIndicator
import com.example.makeitso.ui.theme.MakeItSoTheme
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Serializable
object MessageHistoryRoute

@Composable
fun MessageHistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: MessageHistoryViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadMessages()
    }

    MessageHistoryScreenContent(
        messages = messages,
        isLoading = isLoading,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageHistoryScreenContent(
    messages: List<AiMessage>,
    isLoading: Boolean,
    onNavigateBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = "과거 AI 메시지 기록",
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                iconDescription = "뒤로 가기",
                action = onNavigateBack,
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator()
            }
        } else if (messages.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "📝",
                        style = MaterialTheme.typography.displayMedium
                    )
                    Text(
                        text = "아직 AI 메시지가 없습니다",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "TODO를 추가하거나 AI 비서 버튼을 눌러보세요!",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { message ->
                    MessageHistoryItem(message = message)
                }
            }
        }
    }
}

@Composable
fun MessageHistoryItem(message: AiMessage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 헤더: 캐릭터 이름과 시간
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = getCharacterEmoji(message.character),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = message.character.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // 트리거 타입 표시
                    Surface(
                        color = if (message.triggerType == TriggerType.AUTO_CREATE) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.secondaryContainer
                        },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = if (message.triggerType == TriggerType.AUTO_CREATE) "자동" else "수동",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                
                Text(
                    text = formatDate(message.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // AI 메시지 내용
            Text(
                text = message.response,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun getCharacterEmoji(character: AiCharacter): String {
    return when (character) {
        AiCharacter.HARSH_CRITIC -> "😤"
        AiCharacter.NAGGING_GIRLFRIEND -> "😘"
        AiCharacter.COLD_PRINCESS -> "❄️"
    }
}

private fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
    return formatter.format(date)
}

@Preview(showBackground = true)
@Composable
fun MessageHistoryScreenPreview() {
    MakeItSoTheme {
        MessageHistoryScreenContent(
            messages = listOf(
                AiMessage(
                    id = "1",
                    userId = "user1",
                    prompt = "Test prompt",
                    response = "야, 할 일이 3개나 쌓여있는데 뭐하고 있어? 시간은 기다려주지 않는다고!",
                    character = AiCharacter.HARSH_CRITIC,
                    createdAt = Date(),
                    triggerType = TriggerType.MANUAL
                ),
                AiMessage(
                    id = "2",
                    userId = "user1",
                    prompt = "Test prompt 2",
                    response = "오빠~ 새로운 할 일을 추가했네요! 이번엔 꼭 완료해주세요~",
                    character = AiCharacter.NAGGING_GIRLFRIEND,
                    createdAt = Date(System.currentTimeMillis() - 3600000),
                    triggerType = TriggerType.AUTO_CREATE
                )
            ),
            isLoading = false,
            onNavigateBack = {}
        )
    }
}