package com.puneeth.aiinterviewcoach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.puneeth.aiinterviewcoach.presentation.navigation.CoachNavRoot
import com.puneeth.aiinterviewcoach.presentation.theme.AIAndroidInterviewCoachTheme
import com.puneeth.aiinterviewcoach.presentation.viewmodel.RootViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: RootViewModel = hiltViewModel()
            val preferences by viewModel.preferences.collectAsStateWithLifecycle()
            AIAndroidInterviewCoachTheme(
                darkTheme = preferences.darkMode,
            ) {
                CoachNavRoot()
            }
        }
    }
}
