package com.puneeth.aiinterviewcoach.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.puneeth.aiinterviewcoach.domain.model.CategoryConfidenceSummary
import com.puneeth.aiinterviewcoach.domain.model.CategoryProgress
import com.puneeth.aiinterviewcoach.domain.model.DifficultyProgress
import com.puneeth.aiinterviewcoach.presentation.viewmodel.ProgressViewModel

@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val summary = uiState.summary
    val overallPercent = if (summary.totalQuestions == 0) 0
        else (summary.completedQuestions * 100) / summary.totalQuestions

    val confidenceByCategory = uiState.categoryConfidence.associateBy { it.category }
    val totalEasy = uiState.categoryConfidence.sumOf { it.easyCount }
    val totalOkay = uiState.categoryConfidence.sumOf { it.okayCount }
    val totalHard = uiState.categoryConfidence.sumOf { it.hardCount }
    val totalRated = totalEasy + totalOkay + totalHard

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = "Progress",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
        }

        item {
            OverviewCard(
                overallPercent = overallPercent,
                completed = summary.completedQuestions,
                viewed = summary.viewedQuestions,
                bookmarks = summary.bookmarksCount,
                streakDays = summary.currentStreak,
            ) {
                MetricCard(
                    title = "Viewed",
                    value = summary.viewedQuestions.toString(),
                    supportingText = "Questions opened",
                    modifier = Modifier.weight(1f),
                )
                MetricCard(
                    title = "Completed",
                    value = summary.completedQuestions.toString(),
                    supportingText = "Answers revealed",
                    modifier = Modifier.weight(1f),
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                MetricCard(
                    title = "Bookmarks",
                    value = summary.bookmarksCount.toString(),
                    supportingText = "Saved for later",
                    modifier = Modifier.weight(1f),
                )
                MetricCard(
                    title = "Streak",
                    value = "${summary.currentStreak} days",
                    supportingText = "Practice rhythm",
                    modifier = Modifier.weight(1f),
                )
            }
        }

        if (totalRated > 0) {
            item {
                ConfidenceOverviewCard(
                    totalEasy = totalEasy,
                    totalOkay = totalOkay,
                    totalHard = totalHard,
                )
            }
        }

        item {
            ProgressSectionCard(title = "Category mastery") {
                if (summary.categoryProgress.isEmpty()) {
                    EmptyStateText()
                } else {
                    summary.categoryProgress.forEach { item ->
                        CategoryProgressRow(
                            progress = item,
                            confidence = confidenceByCategory[item.category],
                        )
                    }
                }
            }
        }

        item {
            ProgressSectionCard(title = "Difficulty progress") {
                if (summary.difficultyProgress.isEmpty()) {
                    EmptyStateText()
                } else {
                    summary.difficultyProgress.forEach { item ->
                        DifficultyProgressRow(item)
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewCard(
    overallPercent: Int,
    completed: Int,
    viewed: Int,
    bookmarks: Int,
    streakDays: Int,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { overallPercent / 100f },
                        modifier = Modifier.size(96.dp),
                        strokeWidth = 10.dp,
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$overallPercent%",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "overall",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "You're building momentum.",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "$completed completed from $viewed viewed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "$bookmarks bookmarks · $streakDays day streak",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            LinearProgressIndicator(
                progress = { overallPercent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(999.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                content()
            }
        }
    }
}

@Composable
private fun ConfidenceOverviewCard(
    totalEasy: Int,
    totalOkay: Int,
    totalHard: Int,
) {
    val total = totalEasy + totalOkay + totalHard
    val easyFraction = if (total == 0) 0f else totalEasy.toFloat() / total
    val okayFraction = if (total == 0) 0f else totalOkay.toFloat() / total
    val hardFraction = if (total == 0) 0f else totalHard.toFloat() / total

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "Confidence breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "$total questions rated out of $total",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            // Stacked bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(999.dp)),
            ) {
                if (easyFraction > 0f) {
                    Box(
                        modifier = Modifier
                            .weight(easyFraction)
                            .fillMaxHeight()
                            .background(colorEasy),
                    )
                }
                if (okayFraction > 0f) {
                    Box(
                        modifier = Modifier
                            .weight(okayFraction)
                            .fillMaxHeight()
                            .background(colorOkay),
                    )
                }
                if (hardFraction > 0f) {
                    Box(
                        modifier = Modifier
                            .weight(hardFraction)
                            .fillMaxHeight()
                            .background(colorHard),
                    )
                }
                // Ensure bar is never empty if all fractions are 0
                if (easyFraction == 0f && okayFraction == 0f && hardFraction == 0f) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                    )
                }
            }

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ConfidenceLegendItem(label = "Easy", count = totalEasy, color = colorEasy)
                ConfidenceLegendItem(label = "Okay", count = totalOkay, color = colorOkay)
                ConfidenceLegendItem(label = "Hard", count = totalHard, color = colorHard)
            }
        }
    }
}

@Composable
private fun ConfidenceLegendItem(label: String, count: Int, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(color),
        )
        Text(
            text = "$label: $count",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CategoryProgressRow(
    progress: CategoryProgress,
    confidence: CategoryConfidenceSummary?,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    progress.category.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "${progress.completedCount}/${progress.totalCount} completed",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = "${progress.completionPercent}%",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(999.dp),
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            )
        }
        LinearProgressIndicator(
            progress = { progress.completionPercent / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(999.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
        if (confidence != null && confidence.totalRated > 0) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (confidence.easyCount > 0) {
                    ConfidenceDot(label = "Easy ${confidence.easyCount}", color = colorEasy)
                }
                if (confidence.okayCount > 0) {
                    ConfidenceDot(label = "Okay ${confidence.okayCount}", color = colorOkay)
                }
                if (confidence.hardCount > 0) {
                    ConfidenceDot(label = "Hard ${confidence.hardCount}", color = colorHard)
                }
            }
        }
    }
}

@Composable
private fun ConfidenceDot(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(color),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun DifficultyProgressRow(progress: DifficultyProgress) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    progress.difficulty.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "${progress.completedCount}/${progress.totalCount} completed",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = "${progress.completionPercent}%",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(999.dp),
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            )
        }
        LinearProgressIndicator(
            progress = { progress.completionPercent / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(999.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
private fun ProgressSectionCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            content()
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    supportingText: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelLarge)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun EmptyStateText() {
    Text(
        text = "No progress yet — start answering questions",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

private val colorEasy = Color(0xFF34D399)
private val colorOkay = Color(0xFF60A5FA)
private val colorHard = Color(0xFFEF4444)
