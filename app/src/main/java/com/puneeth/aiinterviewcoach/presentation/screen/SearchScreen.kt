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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.puneeth.aiinterviewcoach.domain.model.CategorySummary
import com.puneeth.aiinterviewcoach.domain.model.HomeDifficultySummary
import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import com.puneeth.aiinterviewcoach.presentation.viewmodel.SearchViewModel

@Composable
fun SearchScreen(
    onOpenQuestion: (Long, InterviewCategory) -> Unit,
    onOpenCategory: (InterviewCategory) -> Unit,
    onOpenDifficulty: (InterviewDifficulty) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isBrowsing = uiState.query.isBlank()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Browse", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = viewModel::updateQuery,
                    label = { Text("Search questions, categories, tags…") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
            }
        }

        if (isBrowsing) {
            item {
                DifficultySection(
                    summaries = uiState.difficultySummaries,
                    onOpenDifficulty = onOpenDifficulty,
                )
            }
            item {
                Text(
                    text = "Categories",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            item {
                CategoryGrid(
                    categories = uiState.categories,
                    onOpenCategory = onOpenCategory,
                )
            }
        } else {
            if (uiState.questions.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        shape = MaterialTheme.shapes.extraLarge,
                    ) {
                        Text(
                            text = "No matching questions. Try a different keyword.",
                            modifier = Modifier.padding(20.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            } else {
                item {
                    Text(
                        text = "${uiState.questions.size} results",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                items(uiState.questions, key = { it.id }) { question ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onOpenQuestion(question.id, question.category) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                        shape = MaterialTheme.shapes.extraLarge,
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Text(question.question, fontWeight = FontWeight.SemiBold)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                AssistChip(onClick = {}, label = { Text(question.category.title) })
                                AssistChip(onClick = {}, label = { Text(question.difficulty.title) })
                            }
                            if (question.tags.isNotEmpty()) {
                                Text(
                                    text = question.tags.joinToString(" · "),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DifficultySection(
    summaries: List<HomeDifficultySummary>,
    onOpenDifficulty: (InterviewDifficulty) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Difficulty",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            summaries.forEach { item ->
                val accent = difficultyAccent(item.difficulty)
                AssistChip(
                    onClick = { onOpenDifficulty(item.difficulty) },
                    label = { Text("${item.difficulty.title}  ${item.questionCount}") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = accent.copy(alpha = 0.12f),
                        labelColor = accent,
                    ),
                    border = AssistChipDefaults.assistChipBorder(
                        enabled = true,
                        borderColor = accent.copy(alpha = 0.3f),
                    ),
                )
            }
        }
    }
}

@Composable
private fun CategoryGrid(
    categories: List<CategorySummary>,
    onOpenCategory: (InterviewCategory) -> Unit,
) {
    // Can't nest LazyVerticalGrid inside LazyColumn — render as plain Column of rows
    val rows = categories.chunked(2)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        rows.forEachIndexed { rowIndex, rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowItems.forEachIndexed { colIndex, item ->
                    CategoryCard(
                        title = item.category.title,
                        count = item.questionCount,
                        accent = categoryAccent(rowIndex * 2 + colIndex),
                        modifier = Modifier.weight(1f),
                        onClick = { onOpenCategory(item.category) },
                    )
                }
                if (rowItems.size == 1) {
                    Box(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    title: String,
    count: Int,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(accent),
            )
            Text(title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "$count questions",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun difficultyAccent(difficulty: InterviewDifficulty): Color = when (difficulty) {
    InterviewDifficulty.BEGINNER -> Color(0xFF2DD4BF)
    InterviewDifficulty.INTERMEDIATE -> Color(0xFFF59E0B)
    InterviewDifficulty.ADVANCED -> Color(0xFFF97316)
    InterviewDifficulty.EXPERT -> Color(0xFF8B5CF6)
}

private fun categoryAccent(index: Int): Color {
    val palette = listOf(
        Color(0xFF60A5FA), Color(0xFF34D399), Color(0xFFF472B6),
        Color(0xFFFBBF24), Color(0xFFA78BFA), Color(0xFF22D3EE),
    )
    return palette[index % palette.size]
}
