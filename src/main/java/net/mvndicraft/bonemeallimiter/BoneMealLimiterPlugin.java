package net.mvndicraft.bonemeallimiter;

import co.aikar.commands.PaperCommandManager;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bstats.bukkit.Metrics;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public final class BoneMealLimiterPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        new Metrics(this, 25354);

        // Save config in our plugin data folder if it does not exist.
        saveDefaultConfig();

        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new BoneMealLimiterCommand());

        getServer().getPluginManager().registerEvents(new BoneMealListener(), this);

        // reloadConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static BoneMealLimiterPlugin getInstance() { return getPlugin(BoneMealLimiterPlugin.class); }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        getConfig().set("bypassGameModeEnum", getConfigGameMode("bypass_game_mode"));
        getConfig().set("disabledMaterials", getConfigMaterials("disabled"));
        getConfig().set("limitGowthStageMaterials", getConfigMaterialsMap("limit_gowth_stage"));
    }

    private Set<GameMode> getConfigGameMode(String key) {
        return getConfig().getStringList(key).stream().map(gm -> safeMatchGameMode(gm, key)).filter(Objects::nonNull)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(GameMode.class)));
    }

    private Set<Material> getConfigMaterials(String key) {
        return getConfig().getStringList(key).stream().map(name -> safeMatchMaterial(name, key)).filter(Objects::nonNull)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Material.class)));
    }
    private Map<Material, Integer> getConfigMaterialsMap(String key) {
        return getConfig().getConfigurationSection(key).getKeys(false).stream().map(name -> {
            Material mat = safeMatchMaterial(name, key);
            return mat == null ? null : Map.entry(mat, getConfig().getInt(key + "." + name));
        }).filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, () -> new EnumMap<>(Material.class)));
    }

    @Nullable
    private Material safeMatchMaterial(String name, String key) {
        Material mat = Material.matchMaterial(name);
        if (mat == null) {
            getLogger().warning(() -> "Invalid material in config at '" + key + "': " + name);
        }
        return mat;
    }
    @Nullable
    private GameMode safeMatchGameMode(String name, String key) {
        try {
            return GameMode.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            getLogger().warning(() -> "Invalid GameMode in config at '" + key + "': " + name);
            return null;
        }
    }


    // Usual log with debug level
    public static void log(Level level, String message) { getInstance().getLogger().log(level, message); }
    public static void log(Level level, Supplier<String> messageProvider) { getInstance().getLogger().log(level, messageProvider); }
    public static void log(Level level, String message, Throwable e) { getInstance().getLogger().log(level, message, e); }
    public static void debug(String message) {
        if (getInstance().getConfig().getBoolean("debug", false)) {
            log(Level.INFO, message);
        }
    }
    public static void debug(Supplier<String> messageProvider) {
        if (getInstance().getConfig().getBoolean("debug", false)) {
            log(Level.INFO, messageProvider);
        }
    }
    public static void info(String message) { log(Level.INFO, message); }
    public static void info(String message, Throwable e) { log(Level.INFO, message, e); }
    public static void warning(String message) { log(Level.WARNING, message); }
    public static void warning(String message, Throwable e) { log(Level.WARNING, message, e); }
    public static void error(String message) { log(Level.SEVERE, message); }
    public static void error(String message, Throwable e) { log(Level.SEVERE, message, e); }
}
