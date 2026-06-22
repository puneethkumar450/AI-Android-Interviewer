package com.puneeth.aiinterviewcoach.presentation.screen

import android.content.Context
import android.content.Intent
import android.os.Build
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Slider
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.PackageInfoCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.puneeth.aiinterviewcoach.presentation.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                shape = MaterialTheme.shapes.extraLarge,
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SettingRow(
                        title = "Dark mode",
                        subtitle = "Comfortable low-light experience",
                        checked = uiState.preferences.darkMode,
                        onCheckedChange = viewModel::updateDarkMode,
                    )
                    SettingRow(
                        title = "Dynamic color",
                        subtitle = "Match the device accent palette",
                        checked = uiState.preferences.dynamicColor,
                        onCheckedChange = viewModel::updateDynamicColor,
                    )
                }
            }
        }
        item {
            InfoCard(title = "Speech") {
                SpeechSlider(
                    title = "Speech rate",
                    subtitle = "Control how fast questions and answers are spoken",
                    value = uiState.preferences.speechRate,
                    valueText = "${"%.1f".format(uiState.preferences.speechRate)}x",
                    range = 0.5f..2.0f,
                    steps = 14,
                    onValueChange = viewModel::updateSpeechRate,
                )
                SpeechSlider(
                    title = "Pitch",
                    subtitle = "Adjust how high or low the voice sounds",
                    value = uiState.preferences.speechPitch,
                    valueText = "${"%.1f".format(uiState.preferences.speechPitch)}x",
                    range = 0.5f..1.5f,
                    steps = 9,
                    onValueChange = viewModel::updateSpeechPitch,
                )
                SettingRow(
                    title = "Auto-read",
                    subtitle = "Read each question aloud automatically",
                    checked = uiState.preferences.speechAutoRead,
                    onCheckedChange = viewModel::updateSpeechAutoRead,
                )
                SettingRow(
                    title = "Highlight while reading",
                    subtitle = "Highlight words as the speech engine reads them",
                    checked = uiState.preferences.speechHighlight,
                    onCheckedChange = viewModel::updateSpeechHighlight,
                )
                SettingRow(
                    title = "Prefer offline voices",
                    subtitle = "Use installed offline voices when available",
                    checked = uiState.preferences.speechPreferOffline,
                    onCheckedChange = viewModel::updateSpeechPreferOffline,
                )
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
        item {
            InfoCard(title = "Privacy Policy") {
                Text(
                    text = "Android Interview Coach stores your progress, bookmarks, preferences, and question packs locally on your device. The app does not sell personal data. Network access is used only for app features that explicitly require online AI or interview services.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                OutlinedButton(
                    onClick = { openUrl(context, context.getString(com.puneeth.aiinterviewcoach.R.string.privacy_policy_url)) },
                ) {
                    Text("Open Privacy Policy")
                }
            }
        }
        item {
            InfoCard(title = "Version") {
                Text(
                    text = appVersionLabel(context),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        item {
            InfoCard(title = "About Developer") {
                Text(
                    text = "Built by Puneeth for Android developers preparing for practical interviews across Kotlin, Compose, architecture, testing, performance, system design, and release engineering.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        uiState.exportedFilePath?.let { path ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                    shape = MaterialTheme.shapes.extraLarge,
                ) {
                    Text(
                        text = "Exported to: $path",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(title, fontWeight = FontWeight.SemiBold)
            content()
        }
    }
}

@Composable
private fun SettingRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SpeechSlider(
    title: String,
    subtitle: String,
    value: Float,
    valueText: String,
    range: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = valueText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            steps = steps,
        )
    }
}

private fun appVersionLabel(context: Context): String {
    val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.packageManager.getPackageInfo(
            context.packageName,
            android.content.pm.PackageManager.PackageInfoFlags.of(0),
        )
    } else {
        @Suppress("DEPRECATION")
        context.packageManager.getPackageInfo(context.packageName, 0)
    }
    return "${packageInfo.versionName} (${PackageInfoCompat.getLongVersionCode(packageInfo)})"
}

private fun openUrl(context: Context, url: String) {
    context.startActivity(
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        },
    )
}
