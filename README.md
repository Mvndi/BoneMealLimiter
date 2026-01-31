[download]: https://img.shields.io/github/downloads/Mvndi/BoneMealLimiter/total
[downloadLink]: https://github.com/Mvndi/BoneMealLimiter/releases
[discord-shield]: https://img.shields.io/discord/728592434577014825?label=discord
[discord-invite]: https://discord.gg/RPNbtRSFqG

[ ![download][] ][downloadLink]
[ ![discord-shield][] ][discord-invite]

[**Discord**](https://discord.gg/RPNbtRSFqG) | [**Hangar**](https://hangar.papermc.io/Hydrolien/BoneMealLimiter) | [**Modrinth**](https://modrinth.com/plugin/bonemeallimiter) | [**GitHub**](https://github.com/Mvndi/BoneMealLimiter)

# BoneMealLimiter

A small plugin that control 100% of the bone meal behavior.
**Paper** and **Folia** are supported. Any Paper fork should work.

## Usage

Download the latest version from [the releases](https://github.com/Mvndi/BoneMealLimiter/releases). Start your server. Edit the config to match your expected limitation. Then reload ingame with `/bml reload`.

Each time you change the configuration, reload or restart your server.

The config can be set to limit the grow of some plants to a maximum stage. For example wheat can be configure to grow to the maximum stage - 1. Players will need to wait at least for the latest stage to be done naturaly but bone meal does not become useless.
Config can also be set to disable the bone meal on some plants or blocks.

You can give `bonemeallimiter.admin` perm to your non OP admins.

## Statistics
[![bStats Graph Data](https://bstats.org/signatures/bukkit/roadspeedmounts.svg)](https://bstats.org/plugin/bukkit/BoneMealLimiter/25354)

## Build
`./gradlew assemble`
The plugin jar file will be in build/libs/
