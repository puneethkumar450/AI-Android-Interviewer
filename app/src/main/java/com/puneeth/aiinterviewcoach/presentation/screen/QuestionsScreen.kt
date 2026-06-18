package com.puneeth.aiinterviewcoach.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.puneeth.aiinterviewcoach.presentation.viewmodel.QuestionsViewModel

@Composable
fun QuestionsScreen(
    viewModel: QuestionsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val question = uiState.question
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(uiState.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
        item {
            Card {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Question ${uiState.currentIndex + 1} of ${uiState.questionIds.size}")
                    Text(question?.question ?: "No question found", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text("${question?.category?.title.orEmpty()} • ${question?.difficulty?.title.orEmpty()}")
                    if (uiState.showAnswer && question != null) {
                        Text("Answer", fontWeight = FontWeight.Bold)
                        Text(question.answer)
                        Text("Explanation", fontWeight = FontWeight.Bold)
                        Text(question.explanation)
                    }
                }
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(onClick = viewModel::previousQuestion, enabled = uiState.hasPrevious, modifier = Modifier.weight(1f)) {
                    Text("Previous")
                }
                Button(onClick = viewModel::revealAnswer, modifier = Modifier.weight(1f)) {
                    Text("Reveal Answer")
                }
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(onClick = viewModel::toggleBookmark, modifier = Modifier.weight(1f)) {
                    Text(if (question?.isBookmarked == true) "Remove Bookmark" else "Bookmark")
                }
                Button(onClick = viewModel::nextQuestion, enabled = uiState.hasNext, modifier = Modifier.weight(1f)) {
                    Text("Next")
                }
            }
        }
    }
}
