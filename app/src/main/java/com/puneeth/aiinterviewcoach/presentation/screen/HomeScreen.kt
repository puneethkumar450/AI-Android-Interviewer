package com.puneeth.aiinterviewcoach.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import com.puneeth.aiinterviewcoach.presentation.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onContinuePractice: (Long?) -> Unit,
    onOpenCategories: () -> Unit,
    onOpenCategory: (InterviewCategory) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 140.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Android Interview Prep", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Completely offline practice with local question packs, bookmarks, and progress tracking.")
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text("Total Questions: ${uiState.summary.totalQuestions}", fontWeight = FontWeight.SemiBold)
                    Text("Bookmarks: ${uiState.summary.bookmarksCount}")
                    Button(onClick = { onContinuePractice(uiState.summary.continueQuestionId) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Continue Practice")
                    }
                }
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = "Categories",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.clickable(onClick = onOpenCategories),
            )
        }
        items(uiState.summary.categories.size) { index ->
            val item = uiState.summary.categories[index]
            Card(modifier = Modifier.clickable { onOpenCategory(item.category) }) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(item.category.title, fontWeight = FontWeight.SemiBold)
                    Text("${item.questionCount} questions")
                }
            }
        }
    }
}
