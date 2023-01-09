package com.wynntils.launchy.logic

import com.wynntils.launchy.LocalLaunchyState
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object ModrinthAPI {
    // ktor httpclient with custom user agent
    val httpClient = HttpClient {
        defaultRequest {
            header("User-Agent", "Wynntils/Launchy/1.3.0 (wynntils.com)")
        }
    }

    // Class to manage modrinth mods via the modrinth api

    // Modrinth API Documentation: https://docs.modrinth.com
    // Modrinth API Endpoint: https://api.modrinth.com/{version}/
    // Modrinth API Version: v2

    val modrinthApiUrl = "https://api.modrinth.com/v2"

    suspend fun getLatestVersion(modrinthId: String, mc_version: String): ModrinthVersion? {
        val versions = getModrinthVersions(modrinthId, mc_version) ?: return null
        if (versions.isEmpty()) return null
        return versions[0]
    }

    suspend fun getLatestFile(modrinthId: String, mc_version: String): ModrinthFile? {
        val latest = getLatestVersion(modrinthId, mc_version) ?: return null
        if (latest.files.isEmpty()) return null
        return latest.files[0]
    }

//    suspend fun getRequiredDependencies(modrinthId: String, mc_version: String): List<String> {
//        val modrinthMod = getLatestVersion(modrinthId, mc_version) ?: return emptyList()
//        return modrinthMod.dependencies.filter { it.dependency_type == "required" }.map { it.version_id }
//    }

    suspend fun getModrinthVersions(modrinthId: String, mc_version: String): List<ModrinthVersion>? {
        // Make request using ktor and get json response
        println("Getting modrinth versions for $modrinthId")
        val response: String =  httpClient.get("$modrinthApiUrl/project/$modrinthId/version?game_versions=%5B%22$mc_version%22%5D&loaders=%5B%22fabric%22%5D").body()
        val json = Json { ignoreUnknownKeys = true;  }
        try {
            return json.decodeFromString(response)
        } catch (e: Exception) {
            println("Failed to parse json response: $response")
            e.printStackTrace()
        }
        return null
    }


}

@Serializable
data class ModrinthVersion(
    val version_number: String,
    val files: List<ModrinthFile>,
    val dependencies: List<ModrinthDependency>
)

@Serializable
data class ModrinthFile(
    val url: String
)

@Serializable
data class ModrinthDependency(
    val project_id: String? = null,
    val version_id: String? = null,
    val dependency_type: String
)
