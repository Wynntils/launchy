package com.mineinabyss.launchy.ui.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowScope

@Composable
fun BoxScope.BackgroundImage(windowScope: WindowScope) {
    windowScope.WindowDraggableArea {
        Image(
            painter = painterResource("mia_render.jpg"),
            contentDescription = "Main render",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
    BackgroundTint()
}

@Composable
fun BoxScope.BackgroundTint() {
    val colors = listOf(
        Color.Transparent,
        MaterialTheme.colorScheme.background,
    )

    BoxWithConstraints(Modifier.align(Alignment.BottomCenter)) {
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(maxHeight / 2)
                .background(Brush.verticalGradient(colors))
        )
    }
}

@Composable
fun LogoLarge(modifier: Modifier) {
    Image(
        painter = painterResource("mia_profile_icon.png"),
        contentDescription = "Mine in Abyss logo",
        modifier = Modifier.widthIn(0.dp, 500.dp).fillMaxSize().then(modifier),
        contentScale = ContentScale.FillWidth
    )
}
