package com.mineinabyss.launchy.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.data.Group
import com.mineinabyss.launchy.data.Mod
import java.awt.Desktop
import java.net.URI

object Browser {
    val desktop = Desktop.getDesktop()
    fun browse(url: String) = synchronized(desktop) { desktop.browse(URI.create(url)) }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModInfo(group: Group, mod: Mod) {
    val state = LocalLaunchyState
    val modEnabled by derivedStateOf { mod in state.enabledMods }
    val configEnabled by derivedStateOf { mod in state.enabledConfigs }
    var configExpanded by remember { mutableStateOf(false) }
    val configTabState by animateFloatAsState(targetValue = if (configExpanded) 180f else 0f)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (!group.forceEnabled && !group.forceDisabled) state.setModEnabled(mod, !modEnabled) },
        color = when (mod) {
            in state.queuedDeletions -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
            in state.queuedInstalls -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f)//Color(105, 240, 174, alpha = 25)
            else -> MaterialTheme.colorScheme.surface
        }
    ) {
        Column(Modifier.padding(2.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Checkbox(
                    enabled = !group.forceEnabled && !group.forceDisabled,
                    checked = modEnabled,
                    onCheckedChange = { state.setModEnabled(mod, !modEnabled) }
                )

                Row(Modifier.weight(6f)) {
                    Text(mod.name, style = MaterialTheme.typography.bodyLarge)
                    // build list of mods that are incompatible with this mod
                    val incompatibleMods = state.versions.modGroups.flatMap { it.value }
                        .filter { it.incompatibleWith.contains(mod.name) || mod.incompatibleWith.contains(it.name) }
                        .map { it.name }
                    if (mod.requires.isNotEmpty() || incompatibleMods.isNotEmpty()) {
                        TooltipArea(
                            modifier = Modifier.alpha(0.5f),
                            tooltip = {
                                Box(Modifier.background(MaterialTheme.colorScheme.background)) {
                                    if (mod.requires.isNotEmpty()) {
                                        Text(
                                            text = "Requires: ${mod.requires.joinToString()}",
                                            modifier = Modifier.padding(4.dp),
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                    if (incompatibleMods.isNotEmpty()) {
                                        Text(
                                            text = "Incompatible with: ${incompatibleMods.joinToString()}",
                                            modifier = Modifier.padding(4.dp),
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = "Requires",
                                modifier = Modifier.scale(0.75f)
                            )
                        }
                    }
                }
                Text(
                    mod.desc,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.alpha(0.5f)
                )
                if (mod.configUrl.isNotEmpty()) {
                    TooltipArea(
                        modifier = Modifier.alpha(0.5f),
                        tooltip = {
                            Text(
                                text = "Config",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "ConfigTab",
                            modifier = Modifier
                                .scale(0.75f)
                                .rotate(configTabState)
                                .clickable { configExpanded = !configExpanded }
                        )
                    }
                }
                if (mod.homepage.isNotEmpty()) {
                    TooltipArea(
                        modifier = Modifier.alpha(0.5f),
                        tooltip = {
                            Text(
                                text = "Open homepage",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    ) {
                        IconButton(
                            modifier = Modifier.alpha(0.5f),
                            onClick = { Browser.browse(mod.homepage) }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.OpenInNew,
                                contentDescription = "Homepage"
                            )
                        }
                    }
                }
            }
            AnimatedVisibility(configExpanded) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        if (!mod.forceConfigDownload) state.setModConfigEnabled(mod, !configEnabled)
                    }.fillMaxWidth()
                ) {
                    Spacer(Modifier.width(20.dp))
                    Checkbox(
                        checked = configEnabled || mod.forceConfigDownload,
                        onCheckedChange = {
                            if (!mod.forceConfigDownload) state.setModConfigEnabled(mod, !configEnabled)
                        },
                        enabled = !mod.forceConfigDownload,
                    )
                    Column {
                        Text(
                            "Download our recommended configuration",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (mod.configDesc.isNotEmpty()) {
                            Spacer(Modifier.width(4.dp))
                            Text(
                                mod.configDesc,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.alpha(0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}
