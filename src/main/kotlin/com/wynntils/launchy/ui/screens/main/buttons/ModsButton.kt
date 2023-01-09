package com.wynntils.launchy.ui.screens.main.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import com.wynntils.launchy.LocalLaunchyState
import com.wynntils.launchy.ui.screens.Screen
import com.wynntils.launchy.ui.screens.screen

@Composable
fun ModsButton() {
    val state = LocalLaunchyState

    Button(onClick = { screen = Screen.Mods }) {
        Icon(
            Icons.Rounded.Settings,
            contentDescription = "Mods",
            // if state.operationsQueued is true, then make the icon rotate constantly
//            modifier = if (state.modrinthChecked.value) Modifier.rotate(360f) else Modifier
        )
        Text("Mods")
    }
}
