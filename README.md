[**Discord**](https://discord.gg/RPNbtRSFqG) | [**Hangar**](https://hangar.papermc.io/Hydrolien/BoneMealLimiter) | [**GitHub**](https://github.com/Mvndi/BoneMealLimiter)

# BoneMealLimiter

A small plugin that control 100% of the bone meal behavior.
**Paper** and **Folia** are supported. Any Paper fork should work.

## Usage

Download the latest version from [the releases](https://github.com/Mvndi/BoneMealLimiter/releases). Start your server. Edit the config to match your expected limitation. Then reload ingame with `/bml reload`.

Each time you change the configuration, reload or restart your server.

The config can be set to limit the grow of some plants to a maximum stage. For example wheat can be configure to grow to the maximum stage - 1. Players will need to wait at least for the latest stage to be done naturaly but bone meal does not become useless.

## Build
`./gradlew assemble`
The plugin jar file will be in build/libs/


## TODO

<!-- 1. Create the reload command. -->
2. Add a listener that react to bone meal use according to the config limitation.
3. Test bone meal limitation with MockBukkit is possible.
4. Test ingame.