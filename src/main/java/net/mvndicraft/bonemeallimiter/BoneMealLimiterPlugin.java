package net.mvndicraft.bonemeallimiter;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class BoneMealLimiterPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        new Metrics(this, 25354);
        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
