import Com_mineinabyss_conventions_platform_gradle.Deps
import de.undercouch.gradle.tasks.download.Download
import org.codehaus.plexus.util.Os
import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("org.jetbrains.compose") version "1.1.1"
    kotlin("plugin.serialization")
//    id("com.github.johnrengelman.shadow") version "7.1.1"
//    id("proguard") version "7.1.0"

}

/*buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.1.0")
    }
}*/

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://maven.fabricmc.net")
}

dependencies {
    implementation(kotlin("reflect"))
    implementation(files("deps/BrowserLauncher2-all-1_3.jar"))
    implementation(compose.desktop.currentOs) {
        exclude(group = "org.jetbrains.compose.material", module = "material")
    }
    @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation(Deps.kotlinx.serialization.json)
    implementation(Deps.kotlinx.serialization.kaml)
    implementation("io.ktor:ktor-client-core:1.6.8")
    implementation("io.ktor:ktor-client-cio:1.6.8")

    implementation("org.json:json:20210307")
    implementation("net.fabricmc:fabric-installer:0.9.0")
    implementation("edu.stanford.ejalbert:BrowserLauncher2:1.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs = listOf("-opt-in=androidx.compose.material3.ExperimentalMaterial3Api")
}

val appName = "MineInAbyss_Launcher-" + when {
    Os.isFamily(Os.FAMILY_MAC) -> "macOS"
    Os.isFamily(Os.FAMILY_WINDOWS) -> "windows"
    else -> "linux"
}

compose.desktop {
    application {
        mainClass = "com.mineinabyss.launchy.MainKt"
        nativeDistributions {
            when {
                Os.isFamily(Os.FAMILY_MAC) -> targetFormats(TargetFormat.Dmg)
                Os.isFamily(Os.FAMILY_WINDOWS) -> targetFormats(TargetFormat.Exe)
                else -> targetFormats(TargetFormat.AppImage)
            }

            modules("java.instrument", "jdk.unsupported")
            packageName = appName
            packageVersion = "${project.version}"
            val iconsRoot = project.file("packaging/icons")
            macOS {
                iconFile.set(iconsRoot.resolve("icon.icns"))
            }
            windows {
                menu = true
                upgradeUuid = "b627d78b-947c-4f5c-9f3b-ae02bfa97d08"
                iconFile.set(iconsRoot.resolve("icon.ico"))
            }
            linux {
                iconFile.set(iconsRoot.resolve("icon.png"))
            }
        }
    }
}

val linuxAppDir = project.file("packaging/appimage/Mine in Abyss.AppDir")
val appImageTool = project.file("deps/appimagetool.AppImage")
val composePackageDir = "$buildDir/compose/binaries/main/${
    when {
        Os.isFamily(Os.FAMILY_MAC) -> "dmg"
        Os.isFamily(Os.FAMILY_WINDOWS) -> "exe"
        else -> "app"
    }
}"

tasks {
    val downloadAppImageBuilder by registering(Download::class) {
        src("https://github.com/AppImage/AppImageKit/releases/download/13/appimagetool-x86_64.AppImage")
        dest(appImageTool)
        doLast {
            exec {
                commandLine("chmod", "+x", "deps/appimagetool.AppImage")
            }
        }
    }

    val deleteOldAppDirFiles by registering(Delete::class) {
        delete("$linuxAppDir/usr/bin", "$linuxAppDir/usr/lib")
    }

    val copyBuildToPackaging by registering(Copy::class) {
        dependsOn("package")
        dependsOn(deleteOldAppDirFiles)
        from("$buildDir/compose/binaries/main/app/$appName")
        into("$linuxAppDir/usr")
    }

    val executeAppImageBuilder by registering(Exec::class) {
        dependsOn(downloadAppImageBuilder)
        dependsOn(copyBuildToPackaging)
        environment("ARCH", "x86_64")
        commandLine(appImageTool, linuxAppDir, "releases/$appName-${project.version}.AppImage")
    }

    val exeRelease by registering(Copy::class) {
        dependsOn("package")
        from(composePackageDir)
        include("*.exe")
        into("releases")
    }

    val dmgRelease by registering(Copy::class) {
        dependsOn("package")
        from(composePackageDir)
        include("*.dmg")
        into("releases")
    }

    val packageForRelease by registering {
        mkdir(project.file("releases"))
        when {
            Os.isFamily(Os.FAMILY_WINDOWS) -> dependsOn(exeRelease)
            Os.isFamily(Os.FAMILY_MAC) -> dependsOn(dmgRelease)
            else -> dependsOn(executeAppImageBuilder)
        }
    }
}

/*
tasks {
    shadowJar {
        mergeServiceFiles()
        minimize {
            exclude(dependency("org.jetbrains.compose.desktop:desktop-jvm.*:.*"))
            exclude(dependency("io.ktor:ktor-client.*:.*"))
            exclude(dependency(("org.jetbrains.compose.material:material-icons.*:.*")))
            exclude("androidx/compose/material/icons/filled/**")
            exclude("androidx/compose/material/icons/outlined/**")
            exclude("androidx/compose/material/icons/sharp/**")
            exclude("androidx/compose/material/icons/twotone/**")
        }

        manifest {
            attributes(mapOf("Main-Class" to "com.mineinabyss.launchy.MainKt"))
        }
    }

    build {
        dependsOn(shadowJar)
    }
}
