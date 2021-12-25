package com.mineinabyss.launchy.ui.screens.main

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.HistoryEdu
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState

@Composable
fun UpdateInfoButton() {
    val state = LocalLaunchyState
    var toggled by remember { mutableStateOf(false) }
    Button(onClick = { toggled = !toggled }) {
        Column {
            Row {
                Icon(Icons.Rounded.Update, contentDescription = "Updates")
                Text("${state.queuedDownloads.size + state.queuedDeletions.size} Updates")
            }

            AnimatedVisibility(
                toggled,
                enter = expandIn(tween(200)) + fadeIn(tween(200, 100)),
                exit = fadeOut() + shrinkOut(tween(200, 100))
            ) {
                Column {
                    InfoText(
                        shown = !state.fabricUpToDate,
                        icon = Icons.Rounded.HistoryEdu,
                        desc = "Install fabric",
                    )
                    InfoText(
                        shown = state.updatesQueued,
                        icon = Icons.Rounded.Update,
                        desc = "Update",
                        extra = state.queuedUpdates.size.toString()
                    )
                    InfoText(
                        shown = state.installsQueued,
                        icon = Icons.Rounded.Download,
                        desc = "Download",
                        extra = state.queuedInstalls.size.toString()
                    )
                    InfoText(
                        shown = state.deletionsQueued,
                        icon = Icons.Rounded.Delete,
                        desc = "Remove",
                        extra = state.queuedDeletions.size.toString()
                    )
                }
            }
        }
    }
}

@Composable
fun InfoText(shown: Boolean, icon: ImageVector, desc: String, extra: String = "") {
    if (shown) Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, desc)
        Text(desc, Modifier.padding(5.dp))
        Text(extra)
    }
}


