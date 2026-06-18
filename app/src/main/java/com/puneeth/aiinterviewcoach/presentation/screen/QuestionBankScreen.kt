package com.puneeth.aiinterviewcoach.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import com.puneeth.aiinterviewcoach.domain.model.PracticeQuestion
import com.puneeth.aiinterviewcoach.presentation.viewmodel.QuestionBankViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuestionBankScreen(
    viewModel: QuestionBankViewModel = hiltViewModel(),
) {
    val uiState by viewModel.screenState.collectAsStateWithLifecycle()

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
                    Text(
                        text = "Offline Question Bank",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Search Android interview questions, bookmark what matters, and review ideal answers even without connectivity.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = viewModel::updateSearch,
                        label = { Text("Search questions") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    FilterRow(
                        selectedCategory = uiState.selectedCategory,
                        selectedDifficulty = uiState.selectedDifficulty,
                        bookmarksOnly = uiState.bookmarksOnly,
                        onCategorySelected = viewModel::selectCategory,
                        onDifficultySelected = viewModel::selectDifficulty,
                        onBookmarksClicked = viewModel::toggleBookmarksOnly,
                    )
                }
            }
            items(uiState.questions, key = { it.id }) { question ->
                QuestionCard(
                    question = question,
                    onToggleBookmark = { viewModel.toggleBookmark(question.id) },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterRow(
    selectedCategory: InterviewCategory?,
    selectedDifficulty: InterviewDifficulty?,
    bookmarksOnly: Boolean,
    onCategorySelected: (InterviewCategory?) -> Unit,
    onDifficultySelected: (InterviewDifficulty?) -> Unit,
    onBookmarksClicked: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(
                onClick = onBookmarksClicked,
                label = { Text(if (bookmarksOnly) "Bookmarked only" else "All questions") },
                leadingIcon = { Icon(Icons.Outlined.BookmarkBorder, contentDescription = null) },
            )
            InterviewCategory.entries.forEach { category ->
                FilterChip(
                    selected = category == selectedCategory,
                    onClick = { onCategorySelected(if (selectedCategory == category) null else category) },
                    label = { Text(category.title) },
                )
            }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InterviewDifficulty.entries.forEach { difficulty ->
                FilterChip(
                    selected = difficulty == selectedDifficulty,
                    onClick = { onDifficultySelected(if (selectedDifficulty == difficulty) null else difficulty) },
                    label = { Text(difficulty.title) },
                )
            }
        }
    }
}

@Composable
private fun QuestionCard(
    question: PracticeQuestion,
    onToggleBookmark: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(question.category.title, color = MaterialTheme.colorScheme.primary)
                    Text(question.difficulty.title, color = MaterialTheme.colorScheme.secondary)
                }
                Icon(
                    imageVector = if (question.bookmarked) Icons.Outlined.Star else Icons.Outlined.BookmarkBorder,
                    contentDescription = null,
                    modifier = Modifier.clickable(onClick = onToggleBookmark),
                    tint = if (question.bookmarked) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(question.question, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(question.idealAnswer, style = MaterialTheme.typography.bodyMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                question.tags.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), MaterialTheme.shapes.small)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                    ) {
                        Text(tag, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}
