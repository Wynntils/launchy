/*
 * File licensed under GNU Lesser General Public License v3.0
 *
 * Original can be found at:
 * https://github.com/IrisShaders/Iris-Installer/blob/master/src/main/java/net/hypercubemc/iris_installer/VanillaLauncherIntegration.java
 */
package com.mineinabyss.launchy.logic

import mjson.Json
import mjson.Json.read
import net.fabricmc.installer.client.ProfileInstaller
import net.fabricmc.installer.util.Reference
import net.fabricmc.installer.util.Utils
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.stream.Collectors
import javax.swing.JOptionPane

object FabricInstaller {
    fun isProfileInstalled(mcDir: Path, name: String): Boolean {
        val launcherProfiles: Path = mcDir.resolve(getLauncherType(mcDir).profileJsonName)
        val jsonObject = JSONObject(Utils.readString(launcherProfiles))
        val profiles: JSONObject = jsonObject.getJSONObject("profiles")
        return profiles.has(name)
    }

    fun installToLauncher(
        vanillaGameDir: Path,
        instanceDir: Path,
        profileName: String,
        gameVersion: String,
        loaderName: String,
        loaderVersion: String,
    ): Boolean {
        val versionId = String.format("%s-%s-%s", loaderName, loaderVersion, gameVersion)
        val launcherType: ProfileInstaller.LauncherType = (if (System.getProperty("os.name")
                .contains("Windows")
        ) getLauncherType(vanillaGameDir) else  /* Return standalone if we aren't on Windows.*/ ProfileInstaller.LauncherType.WIN32)
        installVersion(vanillaGameDir, gameVersion, loaderName, loaderVersion, launcherType)
        installProfile(vanillaGameDir, instanceDir, profileName, versionId, launcherType)
        return true
    }

    fun installVersion(
        mcDir: Path,
        gameVersion: String,
        loaderName: String,
        loaderVersion: String,
        launcherType: ProfileInstaller.LauncherType
    ) {
        println("Installing $gameVersion with fabric $loaderVersion to launcher $launcherType")
        val versionId = "$loaderName-$loaderVersion-$gameVersion"
        val versionsDir = mcDir.resolve("versions")
        val profileDir = versionsDir.resolve(versionId)
        val profileJsonPath = profileDir.resolve("$versionId.json")
        if (!Files.exists(profileDir)) {
            Files.createDirectories(profileDir)
        }
        val dummyJar = profileDir.resolve("$versionId.jar")
        Files.deleteIfExists(dummyJar)
        Files.createFile(dummyJar)
        val profileUrl = URL(
            Reference.getMetaServerEndpoint("v2/versions/loader/$gameVersion/$loaderVersion/profile/json")
        )
        val profileJson: Json = read(profileUrl)
        Utils.writeToFile(profileJsonPath, profileJson.toString())
    }

    private fun installProfile(
        mcDir: Path,
        instanceDir: Path,
        profileName: String,
        versionId: String,
        launcherType: ProfileInstaller.LauncherType
    ) {
        val launcherProfiles: Path = mcDir.resolve(launcherType.profileJsonName)
        if (!Files.exists(launcherProfiles)) {
            println("Could not find launcher_profiles")
            return
        }
        println("Creating profile")
        val jsonObject = JSONObject(Utils.readString(launcherProfiles))
        val profiles: JSONObject = jsonObject.getJSONObject("profiles")
        var foundProfileName: String? = profileName
        val it: Iterator<String> = profiles.keys()
        while (it.hasNext()) {
            val key = it.next()
            val foundProfile: JSONObject = profiles.getJSONObject(key)
            if (foundProfile.has("lastVersionId") && foundProfile.getString("lastVersionId")
                    .equals(versionId) && foundProfile.has("gameDir") && foundProfile.getString("gameDir")
                    .equals(instanceDir.toString())
            ) {
                foundProfileName = key
            }
        }

        // If the profile already exists, use it instead of making a new one so that user's settings are kept (e.g icon)
        val profile: JSONObject =
            if (profiles.has(foundProfileName)) profiles.getJSONObject(foundProfileName) else createProfile(
                profileName,
                instanceDir,
                versionId,
            )
        profile.put("name", profileName)
        profile.put("lastUsed", Utils.ISO_8601.format(Date())) // Update timestamp to bring to top of profile list
        profile.put("lastVersionId", versionId)
        profiles.put(foundProfileName, profile)
        jsonObject.put("profiles", profiles)
        Utils.writeToFile(launcherProfiles, jsonObject.toString())
    }

    private fun createProfile(name: String, instanceDir: Path, versionId: String): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("name", name)
        jsonObject.put("type", "custom")
        jsonObject.put("created", Utils.ISO_8601.format(Date()))
        jsonObject.put("gameDir", instanceDir.toString())
        jsonObject.put("lastUsed", Utils.ISO_8601.format(Date()))
        jsonObject.put("lastVersionId", versionId)
        jsonObject.put("icon", getProfileIcon())
        return jsonObject
    }

    private fun getProfileIcon(): String {
        return try {
            val input: InputStream = Utils::class.java.classLoader.getResourceAsStream("mia_profile_icon.png")!!
            val var4: String
            try {
                var ret = ByteArray(4096)
                var offset = 0
                var len: Int
                while (input.read(ret, offset, ret.size - offset).also { len = it } != -1) {
                    offset += len
                    if (offset == ret.size) {
                        ret = Arrays.copyOf(ret, ret.size * 2)
                    }
                }
                var4 = "data:image/png;base64," + Base64.getEncoder().encodeToString(ret.copyOf(offset))
            } catch (e: Throwable) {
                try {
                    input.close()
                } catch (var5: Throwable) {
                    e.addSuppressed(var5)
                }
                throw e
            }
            input.close()
            var4
        } catch (e: IOException) {
            e.printStackTrace()
            "TNT"
        }
    }

    private fun showLauncherTypeSelection(): ProfileInstaller.LauncherType? {
        val options = arrayOf<String>(
            Utils.BUNDLE.getString("prompt.launcher.type.xbox"),
            Utils.BUNDLE.getString("prompt.launcher.type.win32")
        )
        val result = JOptionPane.showOptionDialog(
            null,
            Utils.BUNDLE.getString("prompt.launcher.type.body"),
            Utils.BUNDLE.getString("installer.title"),
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        )
        return if (result == JOptionPane.CLOSED_OPTION) {
            null
        } else {
            if (result == JOptionPane.YES_OPTION) ProfileInstaller.LauncherType.MICROSOFT_STORE else ProfileInstaller.LauncherType.WIN32
        }
    }

    fun getLauncherType(vanillaGameDir: Path): ProfileInstaller.LauncherType {
        var launcherType: ProfileInstaller.LauncherType?
        val types: List<ProfileInstaller.LauncherType> = getInstalledLauncherTypes(vanillaGameDir)
        if (types.size == 0) {
            // Default to WIN32, since nothing will happen anyway
            println("No launchers found, profile installation will not take place!")
            launcherType = ProfileInstaller.LauncherType.WIN32
        } else if (types.size == 1) {
            println("Found only one launcher (" + types[0] + "), will proceed with that!")
            launcherType = types[0]
        } else {
            println("Multiple launchers found, showing selection screen!")
            launcherType = showLauncherTypeSelection()
            if (launcherType == null) {
                System.out.println(Utils.BUNDLE.getString("prompt.ready.install"))
                launcherType = ProfileInstaller.LauncherType.WIN32
            }
        }
        return launcherType
    }

    fun getInstalledLauncherTypes(mcDir: Path): List<ProfileInstaller.LauncherType> {
        return Arrays.stream(ProfileInstaller.LauncherType.values()).filter { launcherType ->
            Files.exists(
                mcDir.resolve(
                    launcherType.profileJsonName
                )
            )
        }.collect(Collectors.toList())
    }
}
