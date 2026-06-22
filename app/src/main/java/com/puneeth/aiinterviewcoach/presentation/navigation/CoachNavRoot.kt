package com.puneeth.aiinterviewcoach.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.puneeth.aiinterviewcoach.presentation.screen.BookmarksScreen
import com.puneeth.aiinterviewcoach.presentation.screen.CategoriesScreen
import com.puneeth.aiinterviewcoach.presentation.screen.HomeScreen
import com.puneeth.aiinterviewcoach.presentation.screen.ProgressScreen
import com.puneeth.aiinterviewcoach.presentation.screen.QuestionsScreen
import com.puneeth.aiinterviewcoach.presentation.screen.SearchScreen
import com.puneeth.aiinterviewcoach.presentation.screen.SettingsScreen

@Composable
fun CoachNavRoot(
    isReady: Boolean,
) {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    if (!isReady) {
        Box(modifier = Modifier, contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val shouldShowBottomBar = topLevelDestinations.any { destination ->
        navBackStackEntry?.destination?.hierarchy?.any { it.route == destination.route } == true
    }
    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    tonalElevation = 8.dp,
                ) {
                    topLevelDestinations.forEach { destination ->
                        val selected = navBackStackEntry?.destination?.hierarchy?.any {
                            it.route == destination.route
                        } == true
                        NavigationBarItem(
                            selected = selected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            ),
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(destination.icon, contentDescription = destination.title) },
                            label = { Text(destination.title) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = CoachDestination.Home.route,
            modifier = Modifier.padding(top = 21.dp, bottom = padding.calculateBottomPadding(),
                start = 4.dp, end = 4.dp),
        ) {
            composable(CoachDestination.Home.route) {
                HomeScreen(
                    onContinuePractice = { questionId ->
                        navController.navigate(questionRoute(startId = questionId))
                    },
                    onRandomQuestion = { questionId ->
                        navController.navigate(questionRoute(startId = questionId))
                    },
                    onOpenBookmarks = {
                        navController.navigate(questionRoute(bookmarksOnly = true))
                    },
                    onOpenHardQuestions = {
                        navController.navigate(questionRoute(hardOnly = true))
                    },
                    onOpenAllQuestions = {
                        navController.navigate(questionRoute())
                    },
                )
            }
            composable(CoachDestination.Search.route) {
                SearchScreen(
                    onOpenQuestion = { questionId, category ->
                        navController.navigate(questionRoute(startId = questionId, category = category.title))
                    },
                    onOpenCategory = { category ->
                        navController.navigate(questionRoute(category = category.title))
                    },
                    onOpenDifficulty = { difficulty ->
                        navController.navigate(questionRoute(difficulty = difficulty.title))
                    },
                )
            }
            composable(CoachDestination.Bookmarks.route) {
                BookmarksScreen(
                    onOpenQuestion = { questionId, category ->
                        navController.navigate(questionRoute(startId = questionId, category = category.title, bookmarksOnly = true))
                    },
                )
            }
            composable(CoachDestination.Progress.route) {
                ProgressScreen()
            }
            composable(CoachDestination.Settings.route) {
                SettingsScreen()
            }
            composable(CoachDestination.Categories.route) {
                CategoriesScreen(
                    onNavigateBack = navController::navigateUp,
                    onOpenCategory = { category ->
                        navController.navigate(questionRoute(category = category.title))
                    },
                )
            }
            composable(
                route = CoachDestination.Questions.route,
                arguments = listOf(
                    navArgument("category") { type = NavType.StringType; nullable = true; defaultValue = null },
                    navArgument("difficulty") { type = NavType.StringType; nullable = true; defaultValue = null },
                    navArgument("search") { type = NavType.StringType; nullable = true; defaultValue = null },
                    navArgument("bookmarksOnly") { type = NavType.BoolType; defaultValue = false },
                    navArgument("hardOnly") { type = NavType.BoolType; defaultValue = false },
                    navArgument("startId") { type = NavType.LongType; defaultValue = -1L },
                ),
            ) {
                QuestionsScreen(onNavigateBack = navController::navigateUp)
            }
        }
    }
}

fun questionRoute(
    category: String? = null,
    difficulty: String? = null,
    search: String? = null,
    bookmarksOnly: Boolean = false,
    hardOnly: Boolean = false,
    startId: Long? = null,
): String {
    val categoryValue = category.orEmpty()
    val difficultyValue = difficulty.orEmpty()
    val searchValue = search.orEmpty()
    val startIdValue = startId ?: -1L
    return "questions?category=$categoryValue&difficulty=$difficultyValue&search=$searchValue&bookmarksOnly=$bookmarksOnly&hardOnly=$hardOnly&startId=$startIdValue"
}
