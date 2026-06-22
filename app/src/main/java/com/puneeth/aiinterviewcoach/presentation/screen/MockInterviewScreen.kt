package com.puneeth.aiinterviewcoach.presentation.screen

import androidx.compose.runtime.Composable

@Composable
fun MockInterviewScreen() {
    SearchScreen(onOpenQuestion = { _, _ -> }, onOpenCategory = {}, onOpenDifficulty = {})
}
