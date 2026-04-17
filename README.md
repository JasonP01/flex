> [!Warning]
>
> The project is now deprecated. In retrospect, this plugin is not very good and its simpler to implement chat formatting yourself in your own plugin if all you do is changing the prefix of your player names.
>
> As for flex-translator, it's always available on [maven](https://maven.xpdustry.com/#/releases/com/xpdustry/flex-translator), but if i were you, i would copy the code (not very complicated imo) and ask AI to convert it to a blocking API.

# flex

[![Maven](https://maven.xpdustry.com/api/badge/latest/releases/com/xpdustry/flex?color=008080&name=flex&prefix=v)](https://maven.xpdustry.com/#/releases/com/xpdustry/flex)
[![Downloads](https://img.shields.io/github/downloads/xpdustry/flex/total?color=008080)](https://github.com/xpdustry/flex/releases)
[![Mindustry 7.0](https://img.shields.io/badge/Mindustry-7.0-008080)](https://github.com/Anuken/Mindustry/releases)
[![Discord](https://img.shields.io/discord/519293558599974912?color=008080&label=Discord)](https://discord.xpdustry.com)

## Description

A plugin providing powerful text processing to your server and other plugins.
Placeholders, translations, message filtering/processing, chat formatting, name formatting, etc...
You name it, flex has it.

## Links

- [Wiki](https://github.com/xpdustry/flex/wiki)
- [Javadoc](https://maven.xpdustry.com/javadoc/releases/com/xpdustry/flex/latest/)

## Building

- `./gradlew shadowJar`: Compile and create a fat jar that contains the plugin and its dependencies (will be located at builds/libs/flex.jar).

- `./gradlew runMindustryClient`: Run Mindustry in desktop with the plugin.

- `./gradlew runMindustryServer`: Run Mindustry in a server with the plugin.

- `./gradlew spotlessApply`: to apply the code formatting and the licence header.
