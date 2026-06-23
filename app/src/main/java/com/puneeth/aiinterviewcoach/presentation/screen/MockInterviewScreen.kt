package com.puneeth.aiinterviewcoach.presentation.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.presentation.viewmodel.MockInterviewUiState
import com.puneeth.aiinterviewcoach.presentation.viewmodel.MockInterviewViewModel
import com.puneeth.aiinterviewcoach.presentation.viewmodel.MockRunningState
import com.puneeth.aiinterviewcoach.presentation.viewmodel.MockResultState
import com.puneeth.aiinterviewcoach.presentation.viewmodel.MockSetupState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MockInterviewScreen(
    onNavigateBack: () -> Unit,
    viewModel: MockInterviewViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (uiState) {
                            is MockInterviewUiState.Setup -> "Mock Interview"
                            is MockInterviewUiState.Running -> "In Progress"
                            is MockInterviewUiState.Result -> "Results"
                        },
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (uiState is MockInterviewUiState.Running) {
                            viewModel.resetToSetup()
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is MockInterviewUiState.Setup -> SetupContent(
                    config = state.config,
                    onSetCount = viewModel::setQuestionCount,
                    onSetTime = viewModel::setTimeLimit,
                    onSetCategory = viewModel::setCategory,
                    onStart = viewModel::startSession,
                )
                is MockInterviewUiState.Running -> RunningContent(
                    state = state.state,
                    onAnswered = viewModel::markAnswered,
                    onSkip = viewModel::skip,
                )
                is MockInterviewUiState.Result -> ResultContent(
                    state = state.state,
                    onRetake = viewModel::resetToSetup,
                    onDone = onNavigateBack,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SetupContent(
    config: MockSetupState,
    onSetCount: (Int) -> Unit,
    onSetTime: (Int) -> Unit,
    onSetCategory: (InterviewCategory?) -> Unit,
    onStart: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Questions", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(5, 10, 15, 20).forEach { count ->
                        FilterChip(
                            selected = config.questionCount == count,
                            onClick = { onSetCount(count) },
                            label = { Text("$count") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            ),
                        )
                    }
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Time per question", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(30 to "30s", 60 to "1 min", 90 to "90s", 120 to "2 min").forEach { (secs, label) ->
                        FilterChip(
                            selected = config.timeLimitSeconds == secs,
                            onClick = { onSetTime(secs) },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            ),
                        )
                    }
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Category (optional)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = config.category == null,
                        onClick = { onSetCategory(null) },
                        label = { Text("All") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                    )
                    InterviewCategory.entries.forEach { cat ->
                        FilterChip(
                            selected = config.category == cat,
                            onClick = { onSetCategory(cat) },
                            label = { Text(cat.title) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            ),
                        )
                    }
                }
            }
        }
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                shape = MaterialTheme.shapes.extraLarge,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    SummaryPill(label = "Questions", value = "${config.questionCount}")
                    SummaryPill(label = "Per question", value = formatTime(config.timeLimitSeconds))
                    SummaryPill(label = "Total time", value = formatTime(config.questionCount * config.timeLimitSeconds))
                }
            }
        }
        item {
            Button(
                onClick = onStart,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = MaterialTheme.shapes.extraLarge,
            ) {
                Text("Start Mock Interview", style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}

@Composable
private fun RunningContent(
    state: MockRunningState,
    onAnswered: () -> Unit,
    onSkip: () -> Unit,
) {
    val timerProgress by animateFloatAsState(
        targetValue = state.timeLeftSeconds.toFloat() / state.timeLimitSeconds.toFloat(),
        animationSpec = tween(durationMillis = 800),
        label = "timer",
    )
    val timerColor = when {
        timerProgress > 0.5f -> Color(0xFF34D399)
        timerProgress > 0.25f -> Color(0xFFF59E0B)
        else -> Color(0xFFEF4444)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        LinearProgressIndicator(
            progress = { state.progress },
            modifier = Modifier.fillMaxWidth(),
            strokeCap = StrokeCap.Round,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )
        Text(
            text = "${state.currentIndex + 1} of ${state.questions.size}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Box(
            modifier = Modifier.size(80.dp).align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                progress = { timerProgress },
                modifier = Modifier.fillMaxSize(),
                color = timerColor,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                strokeCap = StrokeCap.Round,
                strokeWidth = 6.dp,
            )
            Text(
                text = "${state.timeLeftSeconds}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth().weight(1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryPill(state.currentQuestion.category.title)
                    CategoryPill(state.currentQuestion.difficulty.title)
                }
                Text(
                    text = state.currentQuestion.question,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = MaterialTheme.typography.titleMedium.lineHeight,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Think through your answer before tapping below",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onSkip,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = MaterialTheme.shapes.extraLarge,
            ) {
                Icon(Icons.Outlined.SkipNext, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                Text("Skip")
            }
            Button(
                onClick = onAnswered,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34D399)),
            ) {
                Icon(Icons.Outlined.Check, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                Text("Answered")
            }
        }
    }
}

@Composable
private fun ResultContent(
    state: MockResultState,
    onRetake: () -> Unit,
    onDone: () -> Unit,
) {
    val scoreColor = when {
        state.scorePercent >= 80 -> Color(0xFF34D399)
        state.scorePercent >= 50 -> Color(0xFFF59E0B)
        else -> Color(0xFFEF4444)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(140.dp),
            contentAlignment = Alignment.Center,
        ) {
            val animatedScore by animateFloatAsState(
                targetValue = state.scorePercent / 100f,
                animationSpec = tween(durationMillis = 1000),
                label = "score",
            )
            CircularProgressIndicator(
                progress = { animatedScore },
                modifier = Modifier.fillMaxSize(),
                color = scoreColor,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                strokeCap = StrokeCap.Round,
                strokeWidth = 10.dp,
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${state.scorePercent}%",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = scoreColor,
                )
                Text(
                    text = "score",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ResultRow(label = "Answered", value = "${state.answeredCount}", color = Color(0xFF34D399))
                ResultRow(label = "Skipped", value = "${state.skippedCount}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                ResultRow(label = "Total questions", value = "${state.totalQuestions}", color = MaterialTheme.colorScheme.onSurface)
                ResultRow(label = "Time per question", value = formatTime(state.timeLimitSeconds), color = MaterialTheme.colorScheme.onSurface)
                if (state.category != null) {
                    ResultRow(label = "Category", value = state.category, color = MaterialTheme.colorScheme.primary)
                }
                ResultRow(
                    label = "Duration",
                    value = formatTime((state.durationMs / 1000).toInt()),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onRetake,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = MaterialTheme.shapes.extraLarge,
            ) {
                Text("Retake")
            }
            Button(
                onClick = onDone,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = MaterialTheme.shapes.extraLarge,
            ) {
                Text("Done")
            }
        }
    }
}

@Composable
private fun SummaryPill(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun CategoryPill(text: String) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(text, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Composable
private fun ResultRow(label: String, value: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = color)
    }
}

private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return if (m > 0) "${m}m ${s}s" else "${s}s"
}
