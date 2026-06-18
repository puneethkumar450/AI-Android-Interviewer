package com.puneeth.aiinterviewcoach.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.puneeth.aiinterviewcoach.presentation.screen.MockInterviewScreen
import com.puneeth.aiinterviewcoach.presentation.screen.ProgressScreen
import com.puneeth.aiinterviewcoach.presentation.screen.QuestionBankScreen

@Composable
fun CoachNavRoot() {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    Scaffold(
        bottomBar = {
            NavigationBar {
                topLevelDestinations.forEach { destination ->
                    val selected = navBackStackEntry?.destination?.hierarchy?.any {
                        it.route == destination.route
                    } == true
                    NavigationBarItem(
                        selected = selected,
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
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = CoachDestination.QuestionBank.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(CoachDestination.QuestionBank.route) {
                QuestionBankScreen()
            }
            composable(CoachDestination.MockInterview.route) {
                MockInterviewScreen()
            }
            composable(CoachDestination.Progress.route) {
                ProgressScreen()
            }
        }
    }
}
