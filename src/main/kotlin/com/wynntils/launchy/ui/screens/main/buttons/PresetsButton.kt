package com.wynntils.launchy.ui.screens.main.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.List
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.wynntils.launchy.ui.screens.Screen
import com.wynntils.launchy.ui.screens.screen

@Composable
fun PresetsButton() {
    Button(onClick = { screen = Screen.Presets }) {
        Icon(Icons.Rounded.List, contentDescription = "Presets")
        Text("Presets")
    }
}
