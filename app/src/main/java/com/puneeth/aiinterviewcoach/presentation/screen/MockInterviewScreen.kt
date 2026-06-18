package com.puneeth.aiinterviewcoach.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import com.puneeth.aiinterviewcoach.domain.model.MessageRole
import com.puneeth.aiinterviewcoach.presentation.viewmodel.MockInterviewViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MockInterviewScreen(
    viewModel: MockInterviewViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val feedback = uiState.finalFeedback
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("AI Mock Interview", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "Run a chat-style Android interview with adaptive follow-ups, answer scoring, and end-of-session coaching feedback.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        InterviewCategory.entries.forEach { category ->
                            FilterChip(
                                selected = uiState.category == category,
                                onClick = { viewModel.updateCategory(category) },
                                label = { Text(category.title) },
                            )
                        }
                    }
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        InterviewDifficulty.entries.forEach { difficulty ->
                            FilterChip(
                                selected = uiState.difficulty == difficulty,
                                onClick = { viewModel.updateDifficulty(difficulty) },
                                label = { Text(difficulty.title) },
                            )
                        }
                    }
                    Button(onClick = viewModel::startInterview, modifier = Modifier.fillMaxWidth()) {
                        Text("Start mock interview")
                    }
                    if (uiState.isLoading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
            items(uiState.messages, key = { it.id }) { message ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (message.role == MessageRole.INTERVIEWER) {
                            MaterialTheme.colorScheme.surfaceVariant
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = if (message.role == MessageRole.INTERVIEWER) "AI Interviewer" else "Your Answer",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                        )
                        Text(message.content)
                    }
                }
            }
            if (uiState.messages.isNotEmpty() && !uiState.sessionComplete) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = uiState.currentAnswer,
                            onValueChange = viewModel::updateAnswer,
                            label = { Text("Type your answer") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 5,
                        )
                        if (uiState.latestFeedback.isNotBlank()) {
                            Card {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Text("Instant feedback", fontWeight = FontWeight.SemiBold)
                                    Text(uiState.latestFeedback)
                                    Text("Provisional score: ${uiState.provisionalScore}/100")
                                }
                            }
                        }
                        Button(onClick = viewModel::submitAnswer, modifier = Modifier.fillMaxWidth()) {
                            Text("Submit answer")
                        }
                        Button(onClick = viewModel::finishInterview, modifier = Modifier.fillMaxWidth()) {
                            Text("Finish and evaluate")
                        }
                    }
                }
            }
            if (uiState.sessionComplete && feedback != null) {
                item {
                    Card {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text("Interview result", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text("Score: ${feedback.score}/100")
                            Text(feedback.summary)
                            Text("Strengths: ${feedback.strengths.joinToString()}")
                            Text("Weaknesses: ${feedback.weaknesses.joinToString()}")
                            Text("Suggestions: ${feedback.improvementSuggestions.joinToString()}")
                            Text("Recommended topics: ${feedback.recommendedTopics.joinToString()}")
                        }
                    }
                }
            }
        }
    }
}
