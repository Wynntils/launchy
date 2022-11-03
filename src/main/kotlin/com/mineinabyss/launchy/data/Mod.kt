package com.mineinabyss.launchy.data

import kotlinx.serialization.Serializable

@Serializable
data class Mod(
    val name: String,
    val license: String = "Unknown",
    val homepage: String = "",
    val desc: String,
    val url: String,
    val configUrl: String = "",
    val configDesc: String = "",
    val forceConfigDownload: Boolean = false,
    val dependency: Boolean = false,
    val incompatibleWith: List<String> = emptyList(),
    val requires: List<String> = emptyList(),
)
