package com.mineinabyss.launchy.ui.screens.main.buttons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.mineinabyss.launchy.LocalLaunchyState
import kotlinx.coroutines.launch

@Composable
fun InstallButton(enabled: Boolean) {
    val state = LocalLaunchyState
    val coroutineScope = rememberCoroutineScope()
    Button(
        enabled = enabled,
        onClick = {
            coroutineScope.launch { state.install() }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(Icons.Rounded.Download, "Download")
        AnimatedVisibility(!state.minecraftValid) {
            Text("Invalid Minecraft")
        }
        AnimatedVisibility(state.minecraftValid) {
            AnimatedVisibility(state.operationsQueued && !state.isDownloading) {
                Text("Install")
            }
            AnimatedVisibility(!state.operationsQueued) {
                Text("Installed")
            }
            AnimatedVisibility(state.isDownloading) {
                Text("Installing...")
            }
        }
    }
}
