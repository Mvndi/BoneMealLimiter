package net.mvndicraft.bonemeallimiter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Listener for bone meal use on blocks or plants.
 */
public class BoneMealListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onBoneMealUse(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getType() == Material.BONE_MEAL
                && event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
            BoneMealLimiterPlugin.debug(() -> "Player " + event.getPlayer().getName() + " used bone meal.");
            Player player = event.getPlayer();
            if (BoneMealLimiter.isAffected(player)) {
                BoneMealLimiterPlugin.debug(() -> "Player is affected by bone meal limiter.");


                if (BoneMealLimiterPlugin.getInstance().getConfig().getBoolean("disable_all")
                        || BoneMealLimiter.isBoneMealDisabledOn(event.getClickedBlock().getType())) {
                    BoneMealLimiterPlugin.debug(() -> "Bone meal is disabled on this block.");

                    event.setCancelled(true);
                    soundOrMessageNo(event.getClickedBlock(), player);
                } else {
                    BoneMealLimiterPlugin.debug(() -> "Bone meal is not disabled on this block.");
                    int maxStage = BoneMealLimiter.getMaxStage(event.getClickedBlock().getType());
                    Block block = event.getClickedBlock();
                    if (block.getState() instanceof BlockState state) {
                        int age = getAgeOrStage(state);
                        if (age != -1) {
                            if (age >= maxStage) {
                                BoneMealLimiterPlugin.debug(
                                        () -> "Bone meal is disabled on this block when max stage = " + maxStage);
                                event.setCancelled(true);
                                soundOrMessageNo(block, player);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Extra test for block that can grow more than one stage at a time.
     */
    @EventHandler(ignoreCancelled = true)
    public void onCropFertilize(BlockFertilizeEvent event) {
        if (BoneMealLimiter.isAffected(event.getPlayer())) {

            Block block = event.getBlock();
            BlockState newState = event.getBlocks().getFirst();
            BoneMealLimiterPlugin.debug(() -> "BlockFertilizeEvent: Player " + event.getPlayer().getName()
                    + " used bone meal on block " + block.getType() + " with new state " + newState);
            if (block.getBlockData() instanceof Ageable && newState != null
                    && newState.getBlockData() instanceof Ageable newAgeable) {
                int maxStage = BoneMealLimiter.getMaxStage(block.getType());
                int newAge = newAgeable.getAge();
                if (newAge > maxStage) {
                    BoneMealLimiterPlugin
                            .debug(() -> "BlockFertilizeEvent: Bone meal is capped on this block when max stage = "
                                    + maxStage);
                    newAgeable.setAge(maxStage);
                    newState.setBlockData(newAgeable);
                }
            }
        }
    }


    private int getAgeOrStage(BlockState state) {
        if (state.getBlockData() instanceof Ageable ageable) {
            return ageable.getAge();
        } else if (state.getBlockData() instanceof Sapling sapling) {
            return sapling.getStage();
        } else {
            return -1;
        }
    }
    public void setAgeOrStage(BlockState state, int age) {
        if (state.getBlockData() instanceof Ageable ageable) {
            ageable.setAge(age);
        } else if (state.getBlockData() instanceof Sapling sapling) {
            sapling.setStage(age);
        }
    }

    private void soundOrMessageNo(Block block, Player player) {
        if (BoneMealLimiterPlugin.getInstance().getConfig().getBoolean("messages.enable")) {
            String message = BoneMealLimiterPlugin.getInstance().getConfig().getString("messages.bone_meal_disabled",
                    "");
            if (!message.isEmpty()) {
                String color = BoneMealLimiterPlugin.getInstance().getConfig()
                        .getString("messages.bone_meal_disabled_color", "");
                player.sendMessage(Component.text(message, TextColor.fromHexString(color)));
            }
        }

        if (BoneMealLimiterPlugin.getInstance().getConfig().getBoolean("sounds.enable")) {
            String sound = BoneMealLimiterPlugin.getInstance().getConfig().getString("sounds.bone_meal_disabled", "");
            if (!sound.isEmpty()) {
                block.getWorld().playSound(block.getLocation(), sound, 1.0f, 0.0f);
            }
        }
    }
}
