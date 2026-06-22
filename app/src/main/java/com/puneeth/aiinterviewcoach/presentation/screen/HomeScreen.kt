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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import com.puneeth.aiinterviewcoach.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onContinuePractice: (Long?) -> Unit,
    onOpenCategories: () -> Unit,
    onOpenDifficulty: (InterviewDifficulty) -> Unit,
    onOpenCategory: (InterviewCategory) -> Unit,
    onRandomQuestion: (Long) -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenHardQuestions: () -> Unit,
    onOpenAllQuestions: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val summary = uiState.summary
    val scope = rememberCoroutineScope()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Today card
        item(span = { GridItemSpan(maxLineSpan) }) {
            TodayCard(
                totalQuestions = summary.totalQuestions,
                completedQuestions = uiState.completedQuestions,
                streak = uiState.streak,
                recentActivityLabel = uiState.recentActivity?.let {
                    "Last practiced: ${it.categoryTitle}, ${formatRelativeTime(it.lastViewedAt)}"
                },
                onContinuePractice = { onContinuePractice(summary.continueQuestionId) },
                onRandomQuestion = {
                    scope.launch {
                        viewModel.getRandomQuestionId()?.let { onRandomQuestion(it) }
                    }
                },
            )
        }

        // Quick filters
        item(span = { GridItemSpan(maxLineSpan) }) {
            QuickFiltersRow(
                bookmarksCount = summary.bookmarksCount,
                unviewedCount = uiState.unviewedCount,
                hardRatedCount = uiState.hardRatedCount,
                onOpenBookmarks = onOpenBookmarks,
                onOpenHardQuestions = onOpenHardQuestions,
                onOpenAllQuestions = onOpenAllQuestions,
                onOpenCategories = onOpenCategories,
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
private fun TodayCard(
    totalQuestions: Int,
    completedQuestions: Int,
    streak: Int,
    recentActivityLabel: String?,
    onContinuePractice: () -> Unit,
    onRandomQuestion: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Android Interview Prep",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "$totalQuestions questions · $completedQuestions completed",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                if (streak > 0) {
                    StreakBadge(streak = streak)
                }
            }

            if (recentActivityLabel != null) {
                Text(
                    text = recentActivityLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onContinuePractice, modifier = Modifier.weight(1f)) {
                    Text("Continue")
                }
                OutlinedButton(onClick = onRandomQuestion, modifier = Modifier.weight(1f)) {
                    Text("Random")
                }
            }
        }
    }
}

@Composable
private fun StreakBadge(streak: Int) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(text = "🔥", style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "$streak day${if (streak != 1) "s" else ""}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}


@Composable
private fun QuickFiltersRow(
    bookmarksCount: Int,
    unviewedCount: Int,
    hardRatedCount: Int,
    onOpenBookmarks: () -> Unit,
    onOpenHardQuestions: () -> Unit,
    onOpenAllQuestions: () -> Unit,
    onOpenCategories: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Quick access",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            QuickChip(
                label = "Bookmarked",
                count = bookmarksCount,
                onClick = onOpenBookmarks,
            )
            QuickChip(
                label = "Unread",
                count = unviewedCount,
                onClick = onOpenAllQuestions,
            )
            if (hardRatedCount > 0) {
                QuickChip(
                    label = "Hard",
                    count = hardRatedCount,
                    onClick = onOpenHardQuestions,
                    accentColor = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun QuickChip(
    label: String,
    count: Int?,
    onClick: () -> Unit,
    accentColor: Color? = null,
) {
    FilterChip(
        selected = false,
        onClick = onClick,
        label = {
            Text(
                text = if (count != null) "$label ($count)" else label,
                style = MaterialTheme.typography.labelMedium,
                color = accentColor ?: Color.Unspecified,
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = if (accentColor != null)
                accentColor.copy(alpha = 0.10f)
            else
                MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = false,
            borderColor = accentColor?.copy(alpha = 0.4f) ?: MaterialTheme.colorScheme.outlineVariant,
        ),
    )
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = subtitle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun DifficultyCard(
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
                    .clip(MaterialTheme.shapes.small)
                    .background(accent),
            )
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(accent),
            )
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(
                text = "$count questions",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun CategoryCard(
    title: String,
    count: Int,
    accent: Color,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(accent),
            )
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(
                text = "$count questions",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun formatRelativeTime(timestampMs: Long): String {
    val diffMs = System.currentTimeMillis() - timestampMs
    val minutes = diffMs / 60_000
    return when {
        minutes < 1 -> "just now"
        minutes < 60 -> "$minutes min ago"
        minutes < 24 * 60 -> "${minutes / 60} hr ago"
        else -> "${minutes / (24 * 60)} days ago"
    }
}

private data class DifficultyPalette(val accent: Color)

private fun difficultyPalette(difficulty: InterviewDifficulty): DifficultyPalette {
    return when (difficulty) {
        InterviewDifficulty.BEGINNER -> DifficultyPalette(accent = Color(0xFF2DD4BF))
        InterviewDifficulty.INTERMEDIATE -> DifficultyPalette(accent = Color(0xFFF59E0B))
        InterviewDifficulty.ADVANCED -> DifficultyPalette(accent = Color(0xFFF97316))
        InterviewDifficulty.EXPERT -> DifficultyPalette(accent = Color(0xFF8B5CF6))
    }
}

private fun categoryAccent(index: Int): Color {
    val palette = listOf(
        Color(0xFF60A5FA),
        Color(0xFF34D399),
        Color(0xFFF472B6),
        Color(0xFFFBBF24),
        Color(0xFFA78BFA),
        Color(0xFF22D3EE),
    )
    return palette[index % palette.size]
}

