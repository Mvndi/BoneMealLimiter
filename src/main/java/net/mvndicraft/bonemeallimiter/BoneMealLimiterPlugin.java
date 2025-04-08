package net.mvndicraft.bonemeallimiter;

import co.aikar.commands.PaperCommandManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class BoneMealLimiterPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        new Metrics(this, 25354);

        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new BoneMealLimiterCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static BoneMealLimiterPlugin getInstance() { return getPlugin(BoneMealLimiterPlugin.class); }
}
