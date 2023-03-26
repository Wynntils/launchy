package com.wynntils.launchy.ui.screens.presets

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wynntils.launchy.LocalLaunchyState
import com.wynntils.launchy.ui.screens.settings.InfoBar

// This screen is used to display the presets that are available to the user. It is accessed by clicking the "Presets" button in the main screen.
@Composable
@Preview
fun PresetsScreen() {
    val state = LocalLaunchyState
    Scaffold(
        bottomBar = { InfoBar() },
    ) { paddingValues ->
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
                modifier = Modifier.padding(5.dp)
            ) {
                Box(
                    Modifier.padding(paddingValues)
                        .padding(start = 10.dp, top = 5.dp)
                ) {
                    val lazyListState = rememberLazyListState()
                    LazyColumn(Modifier.fillMaxSize().padding(end = 12.dp), lazyListState) {
                        items(state.presets.presets.toList()) { preset ->
                            PresetInfo(preset)
                        }
                    }
                    VerticalScrollbar(
                        modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd),
                        adapter = rememberScrollbarAdapter(lazyListState)
                    )
                }
            }
        }
    }
}

