package com.wynntils.launchy.ui.screens.presets

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.ResourceLoader
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.wynntils.launchy.LocalLaunchyState
import com.wynntils.launchy.data.Preset
import java.awt.Desktop
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

object Browser {
    val desktop = Desktop.getDesktop()
    fun browse(url: String) = synchronized(desktop) { desktop.browse(URI.create(url)) }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PresetInfo(preset: Preset) {
    val state = LocalLaunchyState

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { state.setPreset(preset) },
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(Modifier.padding(2.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Image for preset
                Image(
                    painter = painterResource(
                        resourcePath = preset.icon,
                        loader = object : ResourceLoader {
                            override fun load(resourcePath: String): InputStream {
                                val url = URL(resourcePath)
                                val connection = url.openConnection() as HttpURLConnection
                                connection.doInput = true
                                connection.connect()
                                return connection.getInputStream()
                            }
                        }),
                    contentDescription = "Preset image",
                    modifier = Modifier
                        .size(64.dp)
                        .padding(4.dp)
                )
                Row(Modifier.weight(6f)) {
                    Column {
                        Text(
                            preset.name
                                    + (if (preset.version.isNotEmpty()) " (v${preset.version})" else "")
                                    + (if (preset.author.isNotEmpty()) " by ${preset.author}" else ""),
                            style = MaterialTheme.typography.bodyLarge)
                        Text(
                            preset.description,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.alpha(0.5f)
                        )
                    }
                }
                if (preset.url.isNotEmpty()) {
                    TooltipArea(
                        modifier = Modifier.alpha(0.5f),
                        tooltip = {
                            Text(
                                text = "Open URL",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    ) {
                        IconButton(
                            modifier = Modifier.alpha(0.5f),
                            onClick = { Browser.browse(preset.url) }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.OpenInNew,
                                contentDescription = "URL"
                            )
                        }
                    }
                }
            }
        }
    }
}
