package com.wynntils.launchy.ui.screens

import androidx.compose.animation.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.wynntils.launchy.ui.AppTopBar
import com.wynntils.launchy.ui.screens.main.MainScreen
import com.wynntils.launchy.ui.screens.settings.SettingsScreen
import com.wynntils.launchy.ui.state.TopBar

sealed class Screen(val transparentTopBar: Boolean = false) {
    object Default : Screen(transparentTopBar = true)
    object Settings : Screen()
}

var screen: Screen by mutableStateOf(Screen.Default)

@Composable
@Preview
fun Screens() {
    TransitionFade(screen == Screen.Default) {
        MainScreen()
    }

    TranslucentTopBar(screen) {
        TransitionSlideUp(screen == Screen.Settings) {
            SettingsScreen()
        }
    }

    AppTopBar(
        TopBar,
        screen.transparentTopBar,
        showBackButton = screen != Screen.Default,
        onBackButtonClicked = { screen = Screen.Default }
    )
}

@Composable
fun TranslucentTopBar(currentScreen: Screen, content: @Composable () -> Unit) {
    Column {
        AnimatedVisibility(!currentScreen.transparentTopBar, enter = fadeIn(), exit = fadeOut()) {
            Spacer(Modifier.height(40.dp))
        }
        content()
    }
}

@Composable
fun TransitionFade(enabled: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(enabled, enter = fadeIn(), exit = fadeOut()) {
        content()
    }
}

@Composable
fun TransitionSlideUp(enabled: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        enabled,
        enter = fadeIn() + slideIn(initialOffset = { IntOffset(0, 100) }),
        exit = fadeOut() + slideOut(targetOffset = { IntOffset(0, 100) }),
    ) {
        content()
    }
}

