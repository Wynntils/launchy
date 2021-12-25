package com.mineinabyss.launchy.ui.screens.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mineinabyss.launchy.LocalLaunchyState
import com.mineinabyss.launchy.data.Group
import com.mineinabyss.launchy.data.Mod
import com.mineinabyss.launchy.util.Option

@Composable
fun ToggleButtons(
    onSwitch: (Option) -> Unit,
    group: Group,
    mods: Collection<Mod>,
) {
    val state = LocalLaunchyState
    val offColor = Color.Transparent
    val offTextColor = MaterialTheme.colorScheme.surface
    val forced = group.forceEnabled || group.forceDisabled
    Surface(shape = RoundedCornerShape(20.0.dp)) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(20.0.dp),
            tonalElevation = 10.dp
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.width(140.dp)/*.padding(2.dp)*/
            ) {
                val fullEnable = state.enabledMods.containsAll(mods)
                val fullDisable = mods.none { it in state.enabledMods }

                val disableColorContainer by animateColorAsState(
                    if (fullDisable) MaterialTheme.colorScheme.errorContainer
                    else offColor,
                )
                val disableColor by animateColorAsState(
                    if (fullDisable) MaterialTheme.colorScheme.error
                    else offTextColor,
                )
                if (!forced)
                    TripleSwitchButton(
                        setTo = Option.DISABLED,
                        color = ButtonDefaults.buttonColors(
                            containerColor = disableColorContainer,
                            contentColor = disableColor
                        ),
                        onSwitch = onSwitch,
                        enabled = true,
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.Rounded.Close, "Disabled")
                    }

                val enableColorContainer by animateColorAsState(
                    if (fullEnable) MaterialTheme.colorScheme.onPrimaryContainer
                    else if (!fullDisable) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    else offColor
                )
                val enableColor by animateColorAsState(
                    if (fullEnable) MaterialTheme.colorScheme.onPrimary
                    else if (!fullDisable) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                    else offTextColor
                )
                TripleSwitchButton(
                    setTo = Option.ENABLED,
                    color = ButtonDefaults.buttonColors(
                        containerColor = enableColorContainer,
                        contentColor = enableColor
                    ),
                    onSwitch = onSwitch,
                    enabled = !forced,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Rounded.Check, "Enabled")
                }
            }
        }
    }
}

@Composable
fun TripleSwitchButton(
    setTo: Option,
    color: ButtonColors,
    onSwitch: (Option) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Button(
        enabled = enabled,
        colors = color,
        onClick = { onSwitch(setTo) },
        modifier = modifier.fillMaxHeight()
    ) {
        content()
    }
}
