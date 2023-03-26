package com.wynntils.launchy.data

import com.wynntils.launchy.DEV_MODE
import com.wynntils.launchy.logic.Downloader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlin.io.path.Path
import kotlin.io.path.inputStream

// This class is used to store the data of a preset. A preset is a collection of mods and groups that can be enabled or disabled at once.

@Serializable
data class Presets(
    val presets: List<Preset>
 ) {

    companion object {
        const val PRESETS_URL = "https://raw.githubusercontent.com/Wynntils/launchy/master/presets.yml"

        suspend fun readLatest(download: Boolean): Presets = withContext(Dispatchers.IO) {
            // check if in development mode
            if (DEV_MODE) {
                val file = Path("presets.yml")
                Formats.yaml.decodeFromStream(serializer(), file.inputStream())
            } else {
                if (download) Downloader.download(PRESETS_URL, Dirs.presetsFile)
                Formats.yaml.decodeFromStream(serializer(), Dirs.presetsFile.inputStream())
            }
        }
    }
}

@Serializable
data class Preset(
    val name: String,
    val version: String,
    val description: String,
    val icon: String,
    val author: String,
    val url: String,
    val mods: Set<ModName>,
    val groups: Set<GroupName>,
)
