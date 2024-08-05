pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.mineinabyss.com/releases")
        google()
        maven("https://maven.hq.hydraulic.software")
        mavenLocal()
    }

    plugins {
        val kotlinVersion: String by settings
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
    }
}

dependencyResolutionManagement {
    val idofrontVersion: String by settings

    repositories {
        maven("https://repo.mineinabyss.com/releases")
    }

    versionCatalogs {
        create("libs").from("com.mineinabyss:catalog:$idofrontVersion")
    }
}

rootProject.name = "launchy"

