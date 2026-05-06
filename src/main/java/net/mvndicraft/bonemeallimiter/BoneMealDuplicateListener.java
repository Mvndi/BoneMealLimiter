package net.mvndicraft.bonemeallimiter;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class BoneMealDuplicateListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        Material blockMaterial = block.getType();
        if (!BoneMealLimiter.isBoneMealDuplicateItemOn(blockMaterial)) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.BONE_MEAL) {
            return;
        }

        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            item.subtract();
        }

        block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(blockMaterial));

        event.getPlayer().swingMainHand();

        if (BoneMealLimiterPlugin.getInstance().getConfig().getBoolean("particles.duplicate_item_enable", false)) {
            block.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, block.getLocation().add(0.5, 0.5, 0.5), 8, 0.3, 0.3, 0.3, 0);
        }
        if (BoneMealLimiterPlugin.getInstance().getConfig().getBoolean("sounds.duplicate_item_enable", false)) {
            block.getWorld().playSound(block.getLocation(), Sound.ITEM_BONE_MEAL_USE, 1.0f, 1.0f);
        }

        event.setCancelled(true);
    }
}
