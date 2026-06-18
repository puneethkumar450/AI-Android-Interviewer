package com.puneeth.aiinterviewcoach.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.presentation.viewmodel.BookmarksViewModel

@Composable
fun BookmarksScreen(
    onOpenQuestion: (Long, InterviewCategory) -> Unit,
    viewModel: BookmarksViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("Bookmarks", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
        items(uiState.questions, key = { it.id }) { question ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = question.question,
                        modifier = Modifier.clickable { onOpenQuestion(question.id, question.category) },
                        fontWeight = FontWeight.SemiBold,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { onOpenQuestion(question.id, question.category) }) {
                            Text("Open")
                        }
                        Button(onClick = { viewModel.removeBookmark(question.id) }) {
                            Text("Remove")
                        }
                    }
                }
            }
        }
    }
}
