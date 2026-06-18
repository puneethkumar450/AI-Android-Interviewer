package com.puneeth.aiinterviewcoach.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.ui.graphics.vector.ImageVector

sealed class CoachDestination(
    val route: String,
    val title: String,
    val icon: ImageVector,
) {
    data object QuestionBank : CoachDestination(
        route = "question_bank",
        title = "Question Bank",
        icon = Icons.Outlined.MenuBook,
    )

    data object MockInterview : CoachDestination(
        route = "mock_interview",
        title = "AI Mock",
        icon = Icons.Outlined.Psychology,
    )

    data object Progress : CoachDestination(
        route = "progress",
        title = "Progress",
        icon = Icons.Outlined.Analytics,
    )
}

val topLevelDestinations = listOf(
    CoachDestination.QuestionBank,
    CoachDestination.MockInterview,
    CoachDestination.Progress,
)
