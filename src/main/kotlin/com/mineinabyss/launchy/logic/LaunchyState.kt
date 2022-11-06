package com.mineinabyss.launchy.logic

import androidx.compose.runtime.*
import com.mineinabyss.launchy.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.io.path.deleteIfExists
import kotlin.io.path.div
import kotlin.io.path.exists

class LaunchyState(
    // Config should never be mutated unless it also updates UI state
    private val config: Config,
    // Versions are immutable, we don't care for reading
    val versions: Versions,
//    val scaffoldState: ScaffoldState
) {
    val enabledMods = mutableStateSetOf<Mod>().apply {
        addAll(config.toggledMods.mapNotNull { it.toMod() })
        val defaultEnabled = versions.groups
            .filter { it.enabledByDefault }
            .map { it.name } - config.seenGroups
        val fullEnabled = config.fullEnabledGroups
        val forceEnabled = versions.groups.filter { it.forceEnabled }.map { it.name }
        val forceDisabled = versions.groups.filter { it.forceDisabled }
        val fullDisabled = config.fullDisabledGroups
        addAll(((fullEnabled + defaultEnabled + forceEnabled).toSet())
            .mapNotNull { it.toGroup() }
            .mapNotNull { versions.modGroups[it] }.flatten()
        )
        removeAll((forceDisabled + fullDisabled).toSet().mapNotNull { versions.modGroups[it] }.flatten().toSet())
    }

    val disabledMods: Set<Mod> by derivedStateOf { versions.nameToMod.values.toSet() - enabledMods }

    val downloadURLs = mutableStateMapOf<Mod, DownloadURL>().apply {
        putAll(config.downloads
            .mapNotNull { it.key.toMod()?.to(it.value) }
            .toMap()
        )
    }

    val downloadConfigURLs = mutableStateMapOf<Mod, DownloadURL>().apply {
        putAll(config.downloads
            .mapNotNull { it.key.toMod()?.to(it.value) }
            .toMap()
        )
    }

    var installedFabricVersion by mutableStateOf(config.installedFabricVersion)

    var notPresentDownloads by mutableStateOf(setOf<Mod>())
        private set

    init {
        updateNotPresent()
    }

    val upToDate: Set<Mod> by derivedStateOf {
        (downloadURLs - notPresentDownloads).filter { (mod, url) -> mod.url == url }.keys
    }

    val queuedDownloads by derivedStateOf { enabledMods - upToDate }
    val queuedUpdates by derivedStateOf { queuedDownloads.filter { it.isDownloaded }.toSet() }
    val queuedInstalls by derivedStateOf { queuedDownloads - queuedUpdates }
    private var _deleted by mutableStateOf(0)
    val queuedDeletions by derivedStateOf {
        _deleted
        disabledMods.filter { it.isDownloaded }.also { if (it.isEmpty()) updateNotPresent() }
    }

    var notPresentConfigDownloads by mutableStateOf(setOf<Mod>())
        private set

    init {
        configUpdateNotPresent()
    }

    val enabledConfigs: MutableSet<Mod> = mutableStateSetOf<Mod>().apply {
        addAll(config.toggledConfigs.mapNotNull { it.toMod() })
    }

    init {
        // trigger update incase we have dependencies
        enabledMods.forEach { setModEnabled(it, true) }
    }

    val downloading = mutableStateMapOf<Mod, Progress>()
    val downloadingConfigs = mutableStateMapOf<Mod, Progress>()
    val isDownloading by derivedStateOf { downloading.isNotEmpty() || downloadingConfigs.isNotEmpty() }

    fun isDownloading(mod: Mod) = downloading[mod] != null || downloadingConfigs[mod] != null

    var installingProfile by mutableStateOf(false)
    val fabricUpToDate by derivedStateOf {
        installedFabricVersion == versions.fabricVersion && FabricInstaller.isProfileInstalled(
            Dirs.minecraft,
            "Wynncraft"
        )
    }
    val updatesQueued by derivedStateOf { queuedUpdates.isNotEmpty() }
    val installsQueued by derivedStateOf { queuedInstalls.isNotEmpty() }
    val deletionsQueued by derivedStateOf { queuedDeletions.isNotEmpty() }
    val minecraftValid = Dirs.minecraft.exists()
    val operationsQueued by derivedStateOf { updatesQueued || installsQueued || deletionsQueued || !fabricUpToDate }

    // If any state is true, we consider import handled and move on
    var handledImportOptions by mutableStateOf(
        config.handledImportOptions ||
                (Dirs.wynntils / "options.txt").exists() ||
                !Dirs.minecraft.exists()
    )

    var handledFirstLaunch by mutableStateOf(config.handledFirstLaunch)

    fun setModEnabled(mod: Mod, enabled: Boolean) {
        if (enabled) {
            enabledMods += mod
            enabledMods.filter { it.name in mod.incompatibleWith || it.incompatibleWith.contains(mod.name) }.forEach { setModEnabled(it, false) }
            disabledMods.filter { it.name in mod.requires }.forEach { setModEnabled(it, true) }
        } else {
            enabledMods -= mod
            // if a mod is disabled, disable all mods that depend on it
            enabledMods.filter { it.requires.contains(mod.name) }.forEach { setModEnabled(it, false) }
            // if a mod is disabled, and the dependency is only used by this mod, disable the dependency too, unless it's not marked as a dependency
            enabledMods.filter { dep ->
                mod.requires.contains(dep.name)  // if the mod depends on this dependency
                        && dep.dependency // if the dependency is marked as a dependency
                        && enabledMods.none { it.requires.contains(dep.name) }  // and no other mod depends on this dependency
//                        && !versions.modGroups.filterValues { it.contains(dep) }.keys.any { it.forceEnabled } // and the group the dependency is in is not force enabled
            }.forEach { setModEnabled(it, false) }
        }
        setModConfigEnabled(mod, enabled)
    }

    fun setModConfigEnabled(mod: Mod, enabled: Boolean) {
        if (mod.configUrl.isNotBlank() && enabled) enabledConfigs.add(mod)
        else enabledConfigs.remove(mod)
    }

    suspend fun install() = coroutineScope {
        updateNotPresent()
        if (!fabricUpToDate)
            installFabric()
        for (mod in queuedDownloads)
            launch(Dispatchers.IO) {
                download(mod)
                updateNotPresent()
            }
        for (mod in queuedDeletions) {
            launch(Dispatchers.IO) {
                try {
                    mod.file.deleteIfExists()
                } catch (e: FileSystemException) {
                    return@launch
                } finally {
                    _deleted++
                }
            }
        }
    }

    fun installFabric() {
        installingProfile = true
        FabricInstaller.installToLauncher(
            Dirs.minecraft,
            Dirs.wynntils,
            "Wynncraft",
            versions.minecraftVersion,
            "fabric-loader",
            versions.fabricVersion,
        )
        installingProfile = false
        installedFabricVersion = "Installing..."
        installedFabricVersion = versions.fabricVersion
    }

    suspend fun download(mod: Mod) {
        runCatching {
            downloading[mod] = Progress(0, 0) // set progress to 0
            Downloader.download(url = mod.url, writeTo = mod.file) progress@{
                downloading[mod] = it
            }
            downloadURLs[mod] = mod.url
            save()

            if (mod.configUrl.isNotBlank() && (mod in enabledConfigs)) {
                Downloader.download(url = mod.configUrl, writeTo = Dirs.configZip) progress@{
                    downloadingConfigs[mod] = it
                }
                downloadConfigURLs[mod] = mod.configUrl
                unzip((Dirs.configZip).toFile(), Dirs.wynntils.toString())
                (Dirs.configZip).toFile().delete()
            }
        }.onFailure {
//            Badge {
//                Text("Failed to download ${mod.name}: ${it.localizedMessage}!"/*, "OK"*/)
//            }
//            scaffoldState.snackbarHostState.showSnackbar(
//                "Failed to download ${mod.name}: ${it.localizedMessage}!", "OK"
//            )
        }.onSuccess {
            downloading -= mod
            downloadingConfigs -= mod
        }
    }

    fun save() {
        config.copy(
            fullEnabledGroups = versions.modGroups
                .filter { enabledMods.containsAll(it.value) }.keys
                .map { it.name }.toSet(),
            toggledMods = enabledMods.mapTo(mutableSetOf()) { it.name },
            toggledConfigs = enabledConfigs.mapTo(mutableSetOf()) { it.name } + enabledMods.filter { it.forceConfigDownload }.mapTo(mutableSetOf()) { it.name },
            downloads = downloadURLs.mapKeys { it.key.name },
            seenGroups = versions.groups.map { it.name }.toSet(),
            installedFabricVersion = installedFabricVersion,
            handledImportOptions = handledImportOptions,
            handledFirstLaunch = handledFirstLaunch,
        ).save()
    }

    fun ModName.toMod(): Mod? = versions.nameToMod[this]
    fun GroupName.toGroup(): Group? = versions.nameToGroup[this]

    val Mod.file get() = Dirs.mods / "${name}.jar"
    val Mod.isDownloaded get() = file.exists()

    private fun updateNotPresent(): Set<Mod> {
        return downloadURLs.filter { !it.key.isDownloaded }.keys.also { notPresentDownloads = it }
    }

    private fun configUpdateNotPresent(): Set<Mod> {
        return downloadConfigURLs.filter { !it.key.isDownloaded }.keys.also { notPresentConfigDownloads = it }
    }

    fun launch() {
        TODO()
    }
}

fun <T> mutableStateSetOf() = Collections.newSetFromMap(mutableStateMapOf<T, Boolean>())
