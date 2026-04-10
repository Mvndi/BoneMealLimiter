package net.mvndicraft.bonemeallimiter;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class BoneMealFlowers implements Listener {

    private final Random random = new Random();

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null) return;


        if (!BoneMealLimiterPlugin.getInstance().getConfig()
                .getStringList("duplicate_item_enabled")
                .contains(block.getType().name())) return;

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.BONE_MEAL) return;

        org.bukkit.entity.Item dropped = block.getWorld().dropItem(
                block.getLocation().add(
                        random.nextFloat() * 0.5 + 0.25,
                        random.nextFloat() * 0.5 + 0.25,
                        random.nextFloat() * 0.5 + 0.25
                ),
                new ItemStack(block.getType()));
        dropped.setVelocity(new org.bukkit.util.Vector(
                (random.nextDouble() - 0.5) * 0.2,
                0.15,
                (random.nextDouble() - 0.5) * 0.2
        ));

        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            item.subtract();
        }

        event.getPlayer().swingMainHand();

        block.getWorld().spawnParticle(org.bukkit.Particle.HAPPY_VILLAGER,
                block.getLocation().add(0.5, 0.5, 0.5), 8, 0.3, 0.3, 0.3, 0);
        if (BoneMealLimiterPlugin.getInstance().getConfig().getBoolean("sounds.duplicate_item_enable", false)) {
            block.getWorld().playSound(block.getLocation(),
                    org.bukkit.Sound.ITEM_BONE_MEAL_USE, 1.0f, 1.0f);


            }
        }
    }
