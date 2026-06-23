package com.puneeth.aiinterviewcoach.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class CoachDestination(
    val route: String,
    val title: String,
    val icon: ImageVector,
) {
    data object Home : CoachDestination(
        route = "home",
        title = "Home",
        icon = Icons.Outlined.Home,
    )

    data object Search : CoachDestination(
        route = "search",
        title = "Search",
        icon = Icons.Outlined.Search,
    )

    data object Bookmarks : CoachDestination(
        route = "bookmarks",
        title = "Bookmarks",
        icon = Icons.Outlined.Bookmarks,
    )

    data object Progress : CoachDestination(
        route = "progress",
        title = "Progress",
        icon = Icons.Outlined.Analytics,
    )

    data object Settings : CoachDestination(
        route = "settings",
        title = "Settings",
        icon = Icons.Outlined.Settings,
    )

    data object Categories : CoachDestination(
        route = "categories",
        title = "Categories",
        icon = Icons.Outlined.MenuBook,
    )

    data object Questions : CoachDestination(
        route = "questions?category={category}&difficulty={difficulty}&search={search}&bookmarksOnly={bookmarksOnly}&startId={startId}",
        title = "Questions",
        icon = Icons.Outlined.MenuBook,
    )

    data object MockInterview : CoachDestination(
        route = "mock_interview",
        title = "Mock Interview",
        icon = Icons.Outlined.MenuBook,
    )
}

val topLevelDestinations = listOf(
    CoachDestination.Home,
    CoachDestination.Search,
    CoachDestination.Bookmarks,
    CoachDestination.Progress,
    CoachDestination.Settings,
)
