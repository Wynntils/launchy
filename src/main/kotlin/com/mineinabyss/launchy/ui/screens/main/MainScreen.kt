package com.mineinabyss.launchy.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.ui.screens.main.buttons.InstallButton
import com.mineinabyss.launchy.ui.screens.main.buttons.PlayButton
import com.mineinabyss.launchy.ui.screens.main.buttons.SettingsButton
import com.mineinabyss.launchy.ui.state.windowScope

val showComingSoonDialog = mutableStateOf(false)

@ExperimentalComposeUiApi
@Preview
@Composable
fun MainScreen() {
    val state = LocalLaunchyState

    Box {
        BackgroundImage(windowScope)

        Column(
            modifier =
            Modifier.align(Alignment.Center)
                .heightIn(0.dp, 550.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LogoLarge(Modifier.weight(3f))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().weight(1f),
            ) {
                PlayButton(!state.isDownloading && !state.operationsQueued && state.minecraftValid)
                Spacer(Modifier.width(10.dp))
                InstallButton(!state.isDownloading && state.operationsQueued && state.minecraftValid)
                Spacer(Modifier.width(10.dp))
                AnimatedVisibility(state.operationsQueued) {
                    UpdateInfoButton()
                }
                Spacer(Modifier.width(10.dp))
//                NewsButton(hasUpdates = true)
//                Spacer(Modifier.width(10.dp))
                SettingsButton()
            }
        }

        FirstLaunchDialog()

        HandleImportSettings()

        if (showComingSoonDialog.value) ComingSoonDialog()
    }
}
