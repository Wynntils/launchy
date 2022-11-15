package com.wynntils.launchy

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.wynntils.launchy.data.Config
import com.wynntils.launchy.data.Dirs
import com.wynntils.launchy.data.Versions
import com.wynntils.launchy.logic.LaunchyState
import com.wynntils.launchy.ui.rememberMIAColorScheme
import com.wynntils.launchy.ui.screens.Screens
import com.wynntils.launchy.ui.state.TopBarProvider
import com.wynntils.launchy.ui.state.TopBarState
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.inputStream

private val LaunchyStateProvider = compositionLocalOf<LaunchyState> { error("No local versions provided") }

// set to true to enable dev mode
const val DEV_MODE = false
val LocalLaunchyState: LaunchyState
    @Composable
    get() = LaunchyStateProvider.current

@ExperimentalComposeUiApi
fun main() {
    val version = System.getProperty("app.version") ?: "Development"
    println("Wynntils Launchy $version")
    application {
        val windowState = rememberWindowState(placement = WindowPlacement.Floating)
        val launchyState by produceState<LaunchyState?>(null) {
            val config = Config.read()
            val versions = Versions.readLatest(config.downloadUpdates)
            value = LaunchyState(config, versions)
        }
        val onClose: () -> Unit = {
            exitApplication()
            launchyState?.save()
        }
        Window(
            state = windowState,
            title = "Wynntils - Installer",
            icon = appIcon,
            onCloseRequest = onClose,
            undecorated = true,
        ) {
            val topBarState = remember { TopBarState(onClose, windowState, this) }
            val ready = launchyState != null
            val scheme = rememberMIAColorScheme(0.25f)
            MaterialTheme(colorScheme = scheme) {
                CompositionLocalProvider(TopBarProvider provides topBarState) {
                    Scaffold {
                        AnimatedVisibility(!ready, exit = fadeOut()) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Getting latest mod versions...")
                            }
                        }
                        AnimatedVisibility(ready, enter = fadeIn()) {
                            CompositionLocalProvider(
                                LaunchyStateProvider provides launchyState!!,
                            ) {
                                Dirs.createDirs()
                                Screens()
                            }
                        }
                    }
                }
            }
        }
    }
}

private val appIcon: Painter? by lazy {
    // app.dir is set when packaged to point at our collected inputs.
    val appDirProp = System.getProperty("app.dir")
    val appDir = appDirProp?.let { Path.of(it) }
    // On Windows we should use the .ico file. On Linux, there's no native compound image format and Compose can't render SVG icons,
    // so we pick the 128x128 icon and let the frameworks/desktop environment rescale.
    var iconPath = appDir?.resolve("app.ico")?.takeIf { it.exists() }
    iconPath = iconPath ?: appDir?.resolve("icon-square-128.png")?.takeIf { it.exists() }
    if (iconPath?.exists() == true) {
        BitmapPainter(iconPath.inputStream().buffered().use { loadImageBitmap(it) })
    } else {
        null
    }
}
