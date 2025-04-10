package net.mvndicraft.bonemeallimiter;

import java.util.EnumMap;
import java.util.EnumSet;
import javax.annotation.Nullable;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Tool functions
 */
public class BoneMealLimiter {
    private BoneMealLimiter() {}

    public static boolean isAffected(@Nullable Player player) {
        return player != null && !BoneMealLimiterPlugin.getInstance().getConfig()
                .getObject("bypassGameModeEnum", EnumSet.class).contains(player.getGameMode());
    }

    public static boolean isBoneMealDisabledOn(Material material) {
        return BoneMealLimiterPlugin.getInstance().getConfig().getObject("disabledMaterials", EnumSet.class)
                .contains(material);
    }

    public static int getMaxStage(Material material) {
        Object o = BoneMealLimiterPlugin.getInstance().getConfig().getObject("limitGowthStageMaterials", EnumMap.class)
                .get(material);
        return (o != null && o instanceof Integer i) ? i : Integer.MAX_VALUE;
    }
}
