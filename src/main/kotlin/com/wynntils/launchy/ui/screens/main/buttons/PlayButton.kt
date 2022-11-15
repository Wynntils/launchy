package com.wynntils.launchy.ui.screens.main.buttons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.wynntils.launchy.LocalLaunchyState
import com.wynntils.launchy.ui.screens.main.showComingSoonDialog

@Composable
fun PlayButton(enabled: Boolean) {
    val state = LocalLaunchyState

    Button(
        enabled = enabled,
        onClick = {
            showComingSoonDialog.value = true
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(Icons.Rounded.PlayArrow, "Play")
        AnimatedVisibility(!state.minecraftValid) {
            Text("Invalid Minecraft")
        }
        AnimatedVisibility(state.minecraftValid) {
            Text("Play")
        }
    }
}
