package com.mineinabyss.launchy.data

import com.mineinabyss.launchy.util.OS
import kotlin.io.path.*

object Dirs {
    val home = Path(System.getProperty("user.home"))
    val minecraft = when (OS.get()) {
        OS.WINDOWS -> Path(System.getenv("APPDATA")) / ".minecraft"
        OS.MAC -> Path(System.getProperty("user.home")) / "Library/Application Support/minecraft"
        OS.LINUX -> Path(System.getProperty("user.home")) / ".minecraft"
    }

    val wynntils = when (OS.get()) {
        OS.WINDOWS -> Path(System.getenv("APPDATA")) / ".wynntils"
        OS.MAC -> Path(System.getProperty("user.home")) / "Library/Application Support/wynntils"
        OS.LINUX -> Path(System.getProperty("user.home")) / ".wynntils"
    }
    val mods = wynntils / "mods"
    val tmp = wynntils / ".tmp"

    val config = when (OS.get()) {
        OS.WINDOWS -> Path(System.getenv("APPDATA"))
        OS.MAC -> Path(System.getProperty("user.home")) / "Library/Application Support"
        OS.LINUX -> home / ".config"
    } / "wynntils"

    val configFile = config / "wynntils-launcher.yml"
    val versionsFile = config / "wynntils-versions.yml"

    fun createDirs() {
        config.createDirectories()
        wynntils.createDirectories()
        mods.createDirectories()
        tmp.createDirectories()
    }

    fun createConfigFiles() {
        if (configFile.notExists())
            configFile.createFile().writeText("{}")
        if (versionsFile.notExists())
            versionsFile.createFile().writeText("{}")
    }
}
