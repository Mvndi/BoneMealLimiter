package net.mvndicraft.bonemeallimiter;

import co.aikar.commands.PaperCommandManager;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bstats.bukkit.Metrics;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public final class BoneMealLimiterPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        new Metrics(this, 25354);

        // Save config in our plugin data folder if it does not exist.
        saveDefaultConfig();

        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new BoneMealLimiterCommand());

        getServer().getPluginManager().registerEvents(new BoneMealListener(), this);

        reloadConfig();
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

    private EnumSet<GameMode> getConfigGameMode(String key) {
        return getConfig().getStringList(key).stream().map(String::toUpperCase).map(GameMode::valueOf)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(GameMode.class)));
    }

    private EnumSet<Material> getConfigMaterials(String key) {
        return getConfig().getStringList(key).stream().map(Material::matchMaterial)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Material.class)));
    }
    private EnumMap<Material, Integer> getConfigMaterialsMap(String key) {
        return getConfig().getConfigurationSection(key).getKeys(false).stream().collect(Collectors.toMap(Material::matchMaterial,
                k -> getConfig().getInt(key + "." + k), (a, b) -> a, () -> new EnumMap<>(Material.class)));
    }

    // Usual log with debug level
    public static void log(Level level, String message) { getInstance().getLogger().log(level, message); }
    public static void log(Level level, String message, Throwable e) { getInstance().getLogger().log(level, message, e); }
    public static void debug(String message) {
        if (getInstance().getConfig().getBoolean("debug", false)) {
            log(Level.INFO, message);
        }
    }
    public static void debug(Supplier<String> messageProvider) {
        if (getInstance().getConfig().getBoolean("debug", false)) {
            log(Level.INFO, messageProvider.get());
        }
    }
    public static void info(String message) { log(Level.INFO, message); }
    public static void info(String message, Throwable e) { log(Level.INFO, message, e); }
    public static void warning(String message) { log(Level.WARNING, message); }
    public static void warning(String message, Throwable e) { log(Level.WARNING, message, e); }
    public static void error(String message) { log(Level.SEVERE, message); }
    public static void error(String message, Throwable e) { log(Level.SEVERE, message, e); }
}
