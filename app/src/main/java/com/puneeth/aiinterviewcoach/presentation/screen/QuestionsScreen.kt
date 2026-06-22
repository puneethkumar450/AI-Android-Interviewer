package com.puneeth.aiinterviewcoach.presentation.screen

import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeOff
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.puneeth.aiinterviewcoach.domain.model.ConfidenceRating
import com.puneeth.aiinterviewcoach.domain.model.InterviewCategory
import com.puneeth.aiinterviewcoach.domain.model.InterviewDifficulty
import com.puneeth.aiinterviewcoach.domain.model.PracticeQuestion
import com.puneeth.aiinterviewcoach.presentation.component.CoachTopAppBar
import com.puneeth.aiinterviewcoach.presentation.theme.AIAndroidInterviewCoachTheme
import com.puneeth.aiinterviewcoach.presentation.viewmodel.QuestionsUiState
import com.puneeth.aiinterviewcoach.presentation.viewmodel.QuestionsViewModel
import java.util.Locale

@Composable
fun QuestionsScreen(
    onNavigateBack: () -> Unit,
    viewModel: QuestionsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    QuestionsScreenContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onPrevious = viewModel::previousQuestion,
        onRevealAnswer = viewModel::revealAnswer,
        onRateConfidence = viewModel::saveConfidenceRating,
        onToggleBookmark = viewModel::toggleBookmark,
        onNext = viewModel::nextQuestion,
    )
}

@Composable
private fun QuestionsScreenContent(
    uiState: QuestionsUiState,
    onNavigateBack: () -> Unit,
    onPrevious: () -> Unit,
    onRevealAnswer: () -> Unit,
    onRateConfidence: (ConfidenceRating) -> Unit,
    onToggleBookmark: () -> Unit,
    onNext: () -> Unit,
) {
    val question = uiState.question
    val speaker = rememberQuestionSpeaker(
        questionId = question?.id,
        speechRate = uiState.preferences.speechRate,
        speechPitch = uiState.preferences.speechPitch,
        preferOffline = uiState.preferences.speechPreferOffline,
    )

    LaunchedEffect(question?.id, uiState.preferences.speechAutoRead) {
        if (uiState.preferences.speechAutoRead && question != null) {
            speaker.speak(SpeechTarget.Question, question.question)
        }
    }

    Scaffold(
        topBar = {
            CoachTopAppBar(
                title = uiState.title,
                onNavigateBack = onNavigateBack,
            )
        },
        bottomBar = {
            QuestionActionBar(
                hasPrevious = uiState.hasPrevious,
                hasNext = uiState.hasNext,
                isBookmarked = question?.isBookmarked == true,
                showAnswer = uiState.showAnswer,
                confidenceRating = uiState.confidenceRating,
                onPrevious = onPrevious,
                onRevealAnswer = onRevealAnswer,
                onRateConfidence = onRateConfidence,
                onToggleBookmark = onToggleBookmark,
                onNext = onNext,
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
           /* item {
                PromptMeta(
                    progress = "Question ${uiState.currentIndex + 1} of ${uiState.questionIds.size}",
                    category = question?.category?.title.orEmpty(),
                    difficulty = question?.difficulty?.title.orEmpty(),
                )
            }*/

            item {
                QuestionSection(
                    progress = "${uiState.currentIndex + 1} of ${uiState.questionIds.size}",
                    body = question?.question ?: "No question found.",
                    category = question?.category?.title.orEmpty(),
                    difficulty = question?.difficulty?.title.orEmpty(),
                    isSpeaking = speaker.speakingTarget == SpeechTarget.Question,
                    highlightRange = speaker.highlightRangeFor(SpeechTarget.Question, uiState.preferences.speechHighlight),
                    onToggleSpeech = { speaker.toggle(SpeechTarget.Question, question?.question.orEmpty()) },
                )
            }

            item {
                if (uiState.showAnswer && question != null) {
                    AnswerSection(
                        answer = question.answer,
                        explanation = question.explanation,
                        isAnswerSpeaking = speaker.speakingTarget == SpeechTarget.Answer,
                        isExplanationSpeaking = speaker.speakingTarget == SpeechTarget.Explanation,
                        answerHighlightRange = speaker.highlightRangeFor(SpeechTarget.Answer, uiState.preferences.speechHighlight),
                        explanationHighlightRange = speaker.highlightRangeFor(SpeechTarget.Explanation, uiState.preferences.speechHighlight),
                        onToggleAnswerSpeech = { speaker.toggle(SpeechTarget.Answer, question.answer) },
                        onToggleExplanationSpeech = { speaker.toggle(SpeechTarget.Explanation, question.explanation) },
                    )
                } else {
                    LockedSection()
                }
            }
        }
    }
}

private enum class SpeechTarget {
    Question,
    Answer,
    Explanation,
}

private class QuestionSpeakerState(
    private val ttsProvider: () -> TextToSpeech?,
    private val isReadyProvider: () -> Boolean,
    private val queueSpeech: (SpeechTarget, String) -> Unit,
    private val configureSpeech: (TextToSpeech) -> Unit,
    private val setSpeakingTarget: (SpeechTarget?) -> Unit,
    val speakingTarget: SpeechTarget?,
    private val highlightedTarget: SpeechTarget?,
    private val highlightedRange: IntRange?,
) {
    fun highlightRangeFor(target: SpeechTarget, enabled: Boolean): IntRange? =
        highlightedRange.takeIf { enabled && highlightedTarget == target }

    fun speak(target: SpeechTarget, text: String) {
        if (speakingTarget != target) toggle(target, text)
    }

    fun toggle(target: SpeechTarget, text: String) {
        val tts = ttsProvider()
        if (speakingTarget == target) {
            tts?.stop()
            setSpeakingTarget(null)
            return
        }
        if (tts == null || !isReadyProvider()) {
            setSpeakingTarget(target)
            queueSpeech(target, text)
            return
        }
        tts.stop()
        configureSpeech(tts)
        setSpeakingTarget(target)
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, target.name)
    }
}

@Composable
private fun rememberQuestionSpeaker(
    questionId: Long?,
    speechRate: Float,
    speechPitch: Float,
    preferOffline: Boolean,
): QuestionSpeakerState {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    var textToSpeech by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTextToSpeechReady by remember { mutableStateOf(false) }
    var pendingSpeech by remember { mutableStateOf<Pair<SpeechTarget, String>?>(null) }
    var speakingTarget by remember { mutableStateOf<SpeechTarget?>(null) }
    var highlightedTarget by remember { mutableStateOf<SpeechTarget?>(null) }
    var highlightedRange by remember { mutableStateOf<IntRange?>(null) }

    DisposableEffect(context, isPreview) {
        if (isPreview) {
            onDispose { }
        } else {
            val mainHandler = Handler(Looper.getMainLooper())
            lateinit var tts: TextToSpeech
            tts = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        private fun resetIfCurrent(utteranceId: String?) {
                            mainHandler.post {
                                if (speakingTarget?.name == utteranceId) {
                                    speakingTarget = null
                                    highlightedTarget = null
                                    highlightedRange = null
                                }
                            }
                        }

                        override fun onStart(utteranceId: String?) = Unit

                        override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                            mainHandler.post {
                                val target = SpeechTarget.entries.firstOrNull { it.name == utteranceId }
                                if (target != null && speakingTarget == target) {
                                    highlightedTarget = target
                                    highlightedRange = start until end
                                }
                            }
                        }

                        override fun onDone(utteranceId: String?) {
                            resetIfCurrent(utteranceId)
                        }

                        @Deprecated("Deprecated by the Android TTS API")
                        override fun onError(utteranceId: String?) {
                            resetIfCurrent(utteranceId)
                        }

                        override fun onError(utteranceId: String?, errorCode: Int) {
                            onError(utteranceId)
                        }

                        override fun onStop(utteranceId: String?, interrupted: Boolean) {
                            resetIfCurrent(utteranceId)
                        }
                    })
                    val languageResult = tts.setLanguage(Locale.getDefault())
                    isTextToSpeechReady = languageResult != TextToSpeech.LANG_MISSING_DATA &&
                        languageResult != TextToSpeech.LANG_NOT_SUPPORTED
                    if (isTextToSpeechReady) {
                        configureTextToSpeech(tts, speechRate, speechPitch, preferOffline)
                        pendingSpeech?.let { (target, text) ->
                            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, target.name)
                            pendingSpeech = null
                        }
                    } else {
                        speakingTarget = null
                        pendingSpeech = null
                    }
                }
            }
            textToSpeech = tts
            onDispose {
                tts.stop()
                tts.shutdown()
                textToSpeech = null
                isTextToSpeechReady = false
                pendingSpeech = null
            }
        }
    }

    DisposableEffect(questionId) {
        onDispose {
            textToSpeech?.stop()
            speakingTarget = null
            pendingSpeech = null
            highlightedTarget = null
            highlightedRange = null
        }
    }

    return QuestionSpeakerState(
        ttsProvider = { textToSpeech },
        isReadyProvider = { isTextToSpeechReady },
        queueSpeech = { target, text -> pendingSpeech = target to text },
        configureSpeech = { configureTextToSpeech(it, speechRate, speechPitch, preferOffline) },
        setSpeakingTarget = { speakingTarget = it },
        speakingTarget = speakingTarget,
        highlightedTarget = highlightedTarget,
        highlightedRange = highlightedRange,
    )
}

private fun configureTextToSpeech(
    tts: TextToSpeech,
    speechRate: Float,
    speechPitch: Float,
    preferOffline: Boolean,
) {
    tts.setSpeechRate(speechRate)
    tts.setPitch(speechPitch)
    if (preferOffline) {
        val locale = Locale.getDefault()
        tts.voices
            ?.filter { !it.isNetworkConnectionRequired && it.locale.language == locale.language }
            ?.maxByOrNull { it.quality }
            ?.let { tts.voice = it }
    }
}

@Composable
private fun QuestionActionBar(
    hasPrevious: Boolean,
    hasNext: Boolean,
    isBookmarked: Boolean,
    showAnswer: Boolean,
    confidenceRating: ConfidenceRating?,
    onPrevious: () -> Unit,
    onRevealAnswer: () -> Unit,
    onRateConfidence: (ConfidenceRating) -> Unit,
    onToggleBookmark: () -> Unit,
    onNext: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedButton(
                    onClick = onPrevious,
                    enabled = hasPrevious,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Previous")
                }
                FilledTonalButton(
                    onClick = onRevealAnswer,
                    enabled = !showAnswer,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Reveal Answer")
                }
            }
            if (showAnswer) {
                ConfidenceRatingRow(
                    selected = confidenceRating,
                    onRate = onRateConfidence,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                Button(
                    onClick = onToggleBookmark,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(if (isBookmarked) "Remove Bookmark" else "Bookmark")
                }
                Button(
                    onClick = onNext,
                    enabled = hasNext,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Next")
                }
            }
        }
    }
}

@Composable
private fun ConfidenceRatingRow(
    selected: ConfidenceRating?,
    onRate: (ConfidenceRating) -> Unit,
) {
    val ratings = listOf(
        ConfidenceRating.EASY to MaterialTheme.colorScheme.tertiary,
        ConfidenceRating.OKAY to MaterialTheme.colorScheme.primary,
        ConfidenceRating.HARD to MaterialTheme.colorScheme.error,
    )
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "How did you find it?",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ratings.forEach { (rating, color) ->
                val isSelected = selected == rating
                Button(
                    onClick = { onRate(rating) },
                    modifier = Modifier.weight(1f),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) color else color.copy(alpha = 0.12f),
                        contentColor = if (isSelected) androidx.compose.ui.graphics.Color.White else color,
                    ),
                ) {
                    Text(rating.label, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun PromptMeta(
    progress: String,
    category: String,
    difficulty: String,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.70f)),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = progress,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (category.isNotBlank()) {
                    Tag(text = category)
                }
                if (difficulty.isNotBlank()) {
                    Tag(text = difficulty)
                }
            }
        }
    }
}

@Composable
private fun QuestionSection(
    progress: String,
    body: String,
    category: String,
    difficulty: String,
    isSpeaking: Boolean,
    highlightRange: IntRange?,
    onToggleSpeech: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Question",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = progress,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (category.isNotBlank()) {
                        Tag(text = category)
                    }
                    if (difficulty.isNotBlank()) {
                        Tag(text = difficulty)
                    }
                }
            }
            Text(
                text = highlightedText(body, highlightRange),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
            )
            Box(modifier = Modifier.fillMaxWidth()) {
                SpeakerButton(
                    isSpeaking = isSpeaking,
                    onClick = onToggleSpeech,
                    modifier = Modifier.align(Alignment.BottomEnd),
                )
            }
        }
    }
}

@Composable
private fun AnswerSection(
    answer: String,
    explanation: String,
    isAnswerSpeaking: Boolean,
    isExplanationSpeaking: Boolean,
    answerHighlightRange: IntRange?,
    explanationHighlightRange: IntRange?,
    onToggleAnswerSpeech: () -> Unit,
    onToggleExplanationSpeech: () -> Unit,
) {
    SectionCard(
        title = "Answer",
        speaker = {
            SpeakerButton(
                isSpeaking = isAnswerSpeaking,
                onClick = onToggleAnswerSpeech,
            )
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = highlightedText(answer, answerHighlightRange),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SectionDividerLabel("Explanation")
                Spacer(modifier = Modifier.weight(1f))
                SpeakerButton(
                    isSpeaking = isExplanationSpeaking,
                    onClick = onToggleExplanationSpeech,
                )
            }
            Text(
                text = highlightedText(explanation, explanationHighlightRange),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun highlightedText(text: String, range: IntRange?): AnnotatedString {
    if (range == null || range.first !in text.indices) return AnnotatedString(text)
    val endExclusive = (range.last + 1).coerceAtMost(text.length)
    return AnnotatedString.Builder(text).apply {
        addStyle(
            style = SpanStyle(
                background = MaterialTheme.colorScheme.primaryContainer,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            start = range.first,
            end = endExclusive,
        )
    }.toAnnotatedString()
}

@Composable
private fun LockedSection() {
    SectionCard(
        title = "Answer",
    ) {
        Text(
            text = "Tap “Reveal Answer” to show the answer and explanation in separate sections.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SectionCard(
    title: String,
    speaker: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                speaker?.invoke()
            }
            content()
        }
    }
}

@Composable
private fun SpeakerButton(
    isSpeaking: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(40.dp),
    ) {
        Icon(
            imageVector = if (isSpeaking) Icons.AutoMirrored.Outlined.VolumeUp else Icons.AutoMirrored.Outlined.VolumeOff,
            contentDescription = if (isSpeaking) "Speak off" else "Speak on",
            tint = if (isSpeaking) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SectionDividerLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun Tag(text: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun QuestionsScreenPreview() {
    val sampleQuestion = PracticeQuestion(
        id = 1L,
        category = InterviewCategory.JETPACK_COMPOSE,
        difficulty = InterviewDifficulty.INTERMEDIATE,
        question = "What is the difference between remember and rememberSaveable?",
        answer = "remember stores objects in the Composition, while rememberSaveable survives configuration changes and process death.",
        explanation = "remember stores objects in the Composition, and it forgets the object when the composable that called it is removed from the Composition. rememberSaveable, on the other hand, stores objects in a Bundle so that they can be restored even after the activity is recreated.",
        tags = listOf("Compose", "State"),
        isBookmarked = false
    )

    val sampleUiState = QuestionsUiState(
        title = "Jetpack Compose",
        questionIds = listOf(1L, 2L, 3L),
        currentIndex = 0,
        question = sampleQuestion,
        showAnswer = false
    )

    AIAndroidInterviewCoachTheme {
        QuestionsScreenContent(
            uiState = sampleUiState,
            onNavigateBack = {},
            onPrevious = {},
            onRevealAnswer = {},
            onRateConfidence = {},
            onToggleBookmark = {},
            onNext = {}
        )
    }
}
