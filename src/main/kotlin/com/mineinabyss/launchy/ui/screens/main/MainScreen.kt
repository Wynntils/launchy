package com.mineinabyss.launchy.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowScope
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.ui.screens.main.buttons.InstallButton
import com.mineinabyss.launchy.ui.screens.main.buttons.SettingsButton

@Preview
@Composable
fun MainScreen(windowScope: WindowScope, onSettings: () -> Unit) {
    val state = LocalLaunchyState

    Box {
        BackgroundImage(windowScope)

        Column(
            modifier =
            Modifier.align(Alignment.Center)
                .heightIn(0.dp, 500.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LogoLarge(Modifier.weight(3f))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth().weight(1f),
            ) {
                InstallButton(!state.isDownloading && state.operationsQueued && state.minecraftValid)
                Spacer(Modifier.width(10.dp))
                AnimatedVisibility(state.operationsQueued) {
                    UpdateInfoButton()
                }
                Spacer(Modifier.width(10.dp))
//                NewsButton(hasUpdates = true)
//                Spacer(Modifier.width(10.dp))
                SettingsButton(onSettings)
            }
        }

        HandleImportSettings(windowScope)
    }
}
