package com.puneeth.aiinterviewcoach.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.puneeth.aiinterviewcoach.presentation.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
        item {
            Card {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Dark mode")
                    Switch(
                        checked = uiState.preferences.darkMode,
                        onCheckedChange = viewModel::updateDarkMode,
                    )
                    Text("Dynamic color")
                    Switch(
                        checked = uiState.preferences.dynamicColor,
                        onCheckedChange = viewModel::updateDynamicColor,
                    )
                }
            }
        }
        item {
            Button(onClick = viewModel::resetProgress, modifier = Modifier.fillMaxWidth()) {
                Text("Reset Progress")
            }
        }
        item {
            Button(onClick = viewModel::exportBookmarks, modifier = Modifier.fillMaxWidth()) {
                Text("Export Bookmarks")
            }
        }
        uiState.exportedFilePath?.let { path ->
            item {
                Text("Exported to: $path")
            }
        }
    }
}
