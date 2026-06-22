package com.puneeth.aiinterviewcoach.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.puneeth.aiinterviewcoach.domain.model.PracticeQuestion
import com.puneeth.aiinterviewcoach.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onContinuePractice: (Long?) -> Unit,
    onRandomQuestion: (Long) -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenHardQuestions: () -> Unit,
    onOpenAllQuestions: () -> Unit,
    onOpenQuestion: (Long) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val summary = uiState.summary
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
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
        item {
            DailyGoalCard(
                viewedToday = uiState.viewedTodayCount,
                goal = uiState.dailyGoal,
                onIncrement = { viewModel.adjustDailyGoal(+1) },
                onDecrement = { viewModel.adjustDailyGoal(-1) },
            )
        }
        item {
            QuickFiltersRow(
                bookmarksCount = summary.bookmarksCount,
                unviewedCount = uiState.unviewedCount,
                hardRatedCount = uiState.hardRatedCount,
                onOpenBookmarks = onOpenBookmarks,
                onOpenHardQuestions = onOpenHardQuestions,
                onOpenAllQuestions = onOpenAllQuestions,
            )
        }
        if (uiState.suggestedQuestions.isNotEmpty()) {
            item {
                Text(
                    text = "Suggested",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            items(uiState.suggestedQuestions, key = { it.id }) { question ->
                SuggestedQuestionCard(question = question, onClick = { onOpenQuestion(question.id) })
            }
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
private fun DailyGoalCard(
    viewedToday: Int,
    goal: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
) {
    val progress = (viewedToday.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
    val done = viewedToday >= goal

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (done)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (done) "🎯 Goal reached!" else "🎯 Today's Goal",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    IconButton(
                        onClick = onDecrement,
                        modifier = Modifier.size(32.dp),
                        enabled = goal > 1,
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Decrease goal",
                            modifier = Modifier.size(16.dp),
                        )
                    }
                    Text(
                        text = "$goal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    IconButton(
                        onClick = onIncrement,
                        modifier = Modifier.size(32.dp),
                        enabled = goal < 50,
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Increase goal",
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                strokeCap = StrokeCap.Round,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            )
            Text(
                text = "$viewedToday of $goal questions reviewed today",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SuggestedQuestionCard(
    question: PracticeQuestion,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = question.question,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
            )
            Text(
                text = "${question.category.title} · ${question.difficulty.title}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun StreakBadge(streak: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
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
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Quick access",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuickChip(label = "Bookmarked", count = bookmarksCount, onClick = onOpenBookmarks)
            QuickChip(label = "Unread", count = unviewedCount, onClick = onOpenAllQuestions)
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
