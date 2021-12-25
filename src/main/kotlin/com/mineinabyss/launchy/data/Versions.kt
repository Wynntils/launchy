package com.mineinabyss.launchy.data

import com.mineinabyss.launchy.logic.Downloader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.io.path.inputStream

@Serializable
data class Versions(
    val groups: Set<Group>,
    @SerialName("modGroups")
    private val _modGroups: Map<GroupName, Set<Mod>>,
    val fabricVersion: String,
    val minecraftVersion: String
) {
    val nameToGroup: Map<GroupName, Group> = groups.associateBy { it.name }

    @Transient
    val modGroups: Map<Group, Set<Mod>> = _modGroups.mapNotNull { nameToGroup[it.key]?.to(it.value) }.toMap()
    val nameToMod: Map<ModName, Mod> = modGroups.values
        .flatten()
        .associateBy { it.name }

    companion object {
        const val VERSIONS_URL = "https://raw.githubusercontent.com/MineInAbyss/launchy/master/versions.yml"

        suspend fun readLatest(download: Boolean): Versions = withContext(Dispatchers.IO) {
            if (download) Downloader.download(VERSIONS_URL, Dirs.versionsFile)
            Formats.yaml.decodeFromStream(serializer(), Dirs.versionsFile.inputStream())
        }
    }
}
