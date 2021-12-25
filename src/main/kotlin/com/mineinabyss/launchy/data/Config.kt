package com.mineinabyss.launchy.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import java.io.*
import java.util.zip.ZipFile
import kotlin.io.path.inputStream
import kotlin.io.path.writeText


@Serializable
data class Config(
    val minecraftDir: String? = null,
    val fullEnabledGroups: Set<GroupName> = setOf(),
    val fullDisabledGroups: Set<GroupName> = setOf(),
    val toggledMods: Set<ModName> = setOf(),
    val toggledConfigs: Set<ModName> = setOf(),
    val downloads: Map<ModName, DownloadURL> = mapOf(),
    val seenGroups: Set<GroupName> = setOf(),
    val installedFabricVersion: String? = null,
    val downloadUpdates: Boolean = true,
    val handledImportOptions: Boolean = false,
) {
    fun save() {
        Dirs.configFile.writeText(Formats.yaml.encodeToString(this))
    }

    companion object {
        fun read() = runCatching {
            Formats.yaml.decodeFromStream(serializer(), Dirs.configFile.inputStream())
        }.getOrDefault(Config())
    }
}

@Throws(IOException::class)
fun unzip(zipFilePath: File, destDirectory: String) {

    File(destDirectory).run {
        if (!exists()) {
            mkdirs()
        }
    }

    ZipFile(zipFilePath).use { zip ->
        zip.entries().asSequence().forEach { entry ->
            zip.getInputStream(entry).use { input ->
                val filePath = destDirectory + File.separator + entry.name

                if (!entry.isDirectory) extractFile(input, filePath)
                else File(filePath).mkdir()
            }
        }
    }
}

@Throws(IOException::class)
fun extractFile(inputStream: InputStream, destFilePath: String) {
    val bufferSize = 4096
    val buffer = BufferedOutputStream(FileOutputStream(destFilePath))
    val bytes = ByteArray(bufferSize)
    var read: Int
    while (inputStream.read(bytes).also { read = it } != -1) {
        buffer.write(bytes, 0, read)
    }
    buffer.close()
}

