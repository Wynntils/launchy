package com.mineinabyss.launchy.ui.screens.main.buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Feed
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun NewsButton(hasUpdates: Boolean) {
    Box {
        Button(onClick = {}) {
            Icon(Icons.Rounded.Feed, contentDescription = "Settings")
            Text("News")
        }
        if (hasUpdates) Surface(
            Modifier.size(12.dp).align(Alignment.TopEnd).offset((-2).dp, (2).dp),
            shape = CircleShape,
            color = Color(255, 138, 128)
        ) {}
    }
}
