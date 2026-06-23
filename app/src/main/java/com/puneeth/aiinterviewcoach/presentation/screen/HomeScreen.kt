package com.puneeth.aiinterviewcoach.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import com.puneeth.aiinterviewcoach.domain.model.MockInterviewSession
import com.puneeth.aiinterviewcoach.presentation.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    onContinuePractice: (Long?) -> Unit,
    onOpenCategories: () -> Unit,
    onOpenDifficulty: (InterviewDifficulty) -> Unit,
    onOpenCategory: (InterviewCategory) -> Unit,
    onStartMockInterview: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val summary = uiState.summary

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            HeroCard(
                totalQuestions = summary.totalQuestions,
                bookmarks = summary.bookmarksCount,
                onContinuePractice = { onContinuePractice(summary.continueQuestionId) },
                onOpenCategories = onOpenCategories,
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            MockInterviewCard(
                lastSession = uiState.lastMockSession,
                onStartMockInterview = onStartMockInterview,
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            SectionHeader(
                title = "Difficulty levels",
                subtitle = "Jump into the right practice mode",
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                summary.difficultySummaries.chunked(2).forEach { rowItems ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        rowItems.forEach { item ->
                            val palette = difficultyPalette(item.difficulty)
                            DifficultyCard(
                                title = item.difficulty.title,
                                count = item.questionCount,
                                accent = palette.accent,
                                modifier = Modifier.weight(1f),
                                onClick = { onOpenDifficulty(item.difficulty) },
                            )
                        }
                        if (rowItems.size == 1) {
                            Box(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            SectionHeader(
                title = "Categories",
                subtitle = "Pick a topic and keep the momentum going",
            )
        }
        items(summary.categories.size) { index ->
            val item = summary.categories[index]
            CategoryCard(
                title = item.category.title,
                count = item.questionCount,
                accent = categoryAccent(index),
                onClick = { onOpenCategory(item.category) },
            )
        }
    }
}

@Composable
private fun MockInterviewCard(
    lastSession: MockInterviewSession?,
    onStartMockInterview: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "🎯 Mock Interview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Timed session — no answer reveal",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (lastSession != null) {
                val scoreColor = when {
                    lastSession.scorePercent >= 80 -> Color(0xFF34D399)
                    lastSession.scorePercent >= 50 -> Color(0xFFF59E0B)
                    else -> Color(0xFFEF4444)
                }
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Last session",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = "${lastSession.answeredCount}/${lastSession.totalQuestions} answered",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                            if (lastSession.category != null) {
                                Text(
                                    text = lastSession.category,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Text(
                                text = "${lastSession.scorePercent}%",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = scoreColor,
                            )
                            Text(
                                text = SimpleDateFormat("MMM d", Locale.getDefault())
                                    .format(Date(lastSession.completedAt)),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            Button(
                onClick = onStartMockInterview,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
            ) {
                Text(if (lastSession != null) "Start New Session" else "Start Mock Interview")
            }
        }
    }
}

@Composable
private fun HeroCard(
    totalQuestions: Int,
    bookmarks: Int,
    onContinuePractice: () -> Unit,
    onOpenCategories: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.20f),
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f),
                            ),
                        ),
                    )
                    .padding(16.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Android Interview Prep",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Offline practice with modern progress tracking and smarter revision flows.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "$totalQuestions questions available",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                shape = MaterialTheme.shapes.extraLarge,
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text("Bookmarks: $bookmarks", fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = onContinuePractice, modifier = Modifier.weight(1f)) {
                            Text("Continue")
                        }
                        OutlinedButton(onClick = onOpenCategories, modifier = Modifier.weight(1f)) {
                            Text("Categories")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Text(text = subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun DifficultyCard(title: String, count: Int, accent: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier.fillMaxWidth().height(4.dp).clip(MaterialTheme.shapes.small).background(accent))
            Box(modifier = Modifier.size(12.dp).clip(MaterialTheme.shapes.small).background(accent))
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(text = "$count questions", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CategoryCard(title: String, count: Int, accent: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.fillMaxWidth().height(4.dp).clip(MaterialTheme.shapes.small).background(accent))
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(text = "$count questions", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private data class DifficultyPalette(val accent: Color)

private fun difficultyPalette(difficulty: InterviewDifficulty): DifficultyPalette = when (difficulty) {
    InterviewDifficulty.BEGINNER -> DifficultyPalette(Color(0xFF2DD4BF))
    InterviewDifficulty.INTERMEDIATE -> DifficultyPalette(Color(0xFFF59E0B))
    InterviewDifficulty.ADVANCED -> DifficultyPalette(Color(0xFFF97316))
    InterviewDifficulty.EXPERT -> DifficultyPalette(Color(0xFF8B5CF6))
}

private fun categoryAccent(index: Int): Color {
    val palette = listOf(
        Color(0xFF60A5FA), Color(0xFF34D399), Color(0xFFF472B6),
        Color(0xFFFBBF24), Color(0xFFA78BFA), Color(0xFF22D3EE),
    )
    return palette[index % palette.size]
}
