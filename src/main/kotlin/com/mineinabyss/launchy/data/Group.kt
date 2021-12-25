package com.mineinabyss.launchy.data

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val name: String,
    val enabledByDefault: Boolean = false,
    val forceEnabled: Boolean = false,
    val forceDisabled: Boolean = false,
)
