<div align="center">

# Wynntils Mod Installer

[![Release](https://img.shields.io/github/v/release/Wynntils/launchy?label=Download&style=for-the-badge)](https://github.com/Wynntils/launchy/releases/latest)
[![Forked](https://img.shields.io/badge/Fork%20Of-MineInAbyss%2Flaunchy-green?style=for-the-badge&logo=github)](https://github.com/MineInAbyss/launchy)

</div>

Our custom installer which makes it easier to set up optional mods and in the future, resourcepack options, and more!

## Screenshots
<div align="center">
  <img src="https://user-images.githubusercontent.com/15234414/199800781-c9bf92d4-143a-46a3-a3b0-0188c8d7db39.png" width="846px">
  <img src="https://user-images.githubusercontent.com/15234414/199800326-027d1824-b8b3-4c36-9a54-b93886795f0e.png" width="846px">
</div>


## Instructions

Install it from our [releases](https://github.com/Wynntils/launchy/releases/latest), set up your preferences then just launch through Minecraft (you will need to reopen the installer to download any mod updates we provide.)

## Features
- Installs fabric into a 'Wynntils' profile
- Allows you to choose recommended mods by group or individually
- Installs to the default mods folder and doesn't touch other present mods
- Looks for updates in our mod recommendations on startup

# FAQ

### My game uses my default mods folder
- On Linux, Flatpak users should make sure the Minecraft launcher has access to `~/.wynntils` (you may need to allow access to the entire home directory.)

### [MacOS] "Wynntils Mod Installer" is damaged and can't be opened
![img.png](docs/img.png)

On MacOS, you will see this error message due to our file not being signed by an apple developer key, which is $80/year

To fix this, use the following command to download the file to your computer:
```shell
curl -L "https://github.com/Wynntils/launchy/releases/download/v1.3.0/Wynntils.Mod.Installer-1.3.0.dmg" -o Installer.dmg
open Installer.dmg
```
