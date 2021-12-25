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

    val mineinabyss = when (OS.get()) {
        OS.WINDOWS -> Path(System.getenv("APPDATA")) / ".mineinabyss"
        OS.MAC -> Path(System.getProperty("user.home")) / "Library/Application Support/mineinabyss"
        OS.LINUX -> Path(System.getProperty("user.home")) / ".mineinabyss"
    }
    val mods = mineinabyss / "mods"
    val configZip = mineinabyss / "configs.zip"

    val config = when (OS.get()) {
        OS.WINDOWS -> Path(System.getenv("APPDATA"))
        OS.MAC -> Path(System.getProperty("user.home")) / "Library/Application Support"
        OS.LINUX -> home / ".config"
    } / "mineinabyss"

    val configFile = config / "mia-launcher.yml"
    val versionsFile = config / "mia-versions.yml"

    fun createDirs() {
        config.createDirectories()
        mineinabyss.createDirectories()
    }

    fun createConfigFiles() {
        if (configFile.notExists())
            configFile.createFile().writeText("{}")
        if (versionsFile.notExists())
            versionsFile.createFile().writeText("{}")
    }
}
