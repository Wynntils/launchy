package com.mineinabyss.launchy.logic

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.writeBytes

object Downloader {
    val httpClient = HttpClient()

    suspend fun download(
        url: String,
        writeTo: Path,
        onProgressUpdate: (progress: Progress) -> Unit = {},
    ) {
        val response = httpClient.get<HttpStatement>(url) {
            onDownload { bytesSentTotal, contentLength ->
                onProgressUpdate(Progress(bytesSentTotal, contentLength))
            }
        }.receive<ByteArray>()
        writeTo.parent.createDirectories()
        if (!writeTo.exists())
            writeTo.createFile()
        writeTo.writeBytes(response)
    }
}

data class Progress(val bytesDownloaded: Long, val totalBytes: Long) {
    val percent: Float
        get() = bytesDownloaded.toFloat() / totalBytes.toFloat()
}
