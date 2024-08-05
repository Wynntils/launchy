import de.undercouch.gradle.tasks.download.Download
import org.apache.tools.ant.taskdefs.condition.Os
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose") version "1.5.11"
    id("de.undercouch.download") version "5.6.0"
}

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
    implementation(compose.material)
    implementation(compose.materialIconsExtended)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.kaml)
    implementation("io.ktor:ktor-client-core:1.6.8")
    implementation("io.ktor:ktor-client-cio:1.6.8")

    implementation("org.json:json:20210307")
    implementation("net.fabricmc:fabric-installer:1.0.1")
    implementation("edu.stanford.ejalbert:BrowserLauncher2:1.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs = listOf(
        "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
        "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
        "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
    )
}

val appName = "Wynntils Mod Installer"

compose.desktop {
    application {
        mainClass = "com.wynntils.launchy.MainKt"
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
                menuGroup = appName
                shortcut = true
                upgradeUuid = "b627d78b-947c-4f5c-9f3b-ae02bfa97d08"
                iconFile.set(iconsRoot.resolve("icon.ico"))
                dirChooser = false
                perUserInstall = false
            }
            linux {
                iconFile.set(iconsRoot.resolve("icon.png"))
            }
        }
    }
}

val linuxAppDir = project.file("packaging/appimage/Wynntils.AppDir")
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

tasks.jar {
    manifest.attributes["Main-Class"] = "com.wynntils.launchy.MainKt"
}
