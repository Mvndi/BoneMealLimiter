package net.mvndicraft.bonemeallimiter;

import javax.annotation.Nullable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
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

                doBoneMealLimitation(event.getClickedBlock(), player, event);
            }
        }
    }

    /**
     * Extra test for block that can grow more than one stage at a time.
     */
    @EventHandler(ignoreCancelled = true)
    public void onCropFertilize(BlockFertilizeEvent event) {
        if (BoneMealLimiter.isAffected(event.getPlayer()) || event.getPlayer() == null) {
            String source = event.getPlayer() == null ? "Dispenser " : "Player " + event.getPlayer().getName();
            Block block = event.getBlock();
            BlockState newState = event.getBlocks().getFirst();
            BoneMealLimiterPlugin.debug(() -> "BlockFertilizeEvent: " + source + " used bone meal on block "
                    + block.getType() + " with new state " + newState);
            // Age might have been increase over the max allowed value.
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

    @EventHandler(ignoreCancelled = true)
    public void onDispense(BlockDispenseEvent event) {
        if (event.getItem().getType() == Material.BONE_MEAL) {
            BoneMealLimiterPlugin.debug(() -> "Dispensing bone meal from " + event.getBlock());
            Block dispenser = event.getBlock();
            if (dispenser.getBlockData() instanceof Directional directional) {
                BlockFace facing = directional.getFacing();
                Block target = dispenser.getRelative(facing);
                BoneMealLimiterPlugin.debug(() -> "Dispensing bone meal on block " + target.getType());

                doBoneMealLimitation(target, null, event);
            }
        }
    }


    private void doBoneMealLimitation(Block block, @Nullable Player player, Cancellable event) {
        boolean cancelled = false;
        // disabled
        if (BoneMealLimiterPlugin.getInstance().getConfig().getBoolean("disable_all")
                || BoneMealLimiter.isBoneMealDisabledOn(block.getType())) {
            BoneMealLimiterPlugin.debug(() -> "Bone meal is disabled on this block.");

            cancelled = true;
            // limited
        } else {
            BoneMealLimiterPlugin.debug(() -> "Bone meal is not disabled on this block.");
            int maxStage = BoneMealLimiter.getMaxStage(block.getType());
            if (block.getState() instanceof BlockState state) {
                int age = getAgeOrStage(state);
                if (age != -1 && age >= maxStage) {
                    BoneMealLimiterPlugin
                            .debug(() -> "Bone meal is disabled on this block when max stage = " + maxStage);
                    cancelled = true;
                }
            }
        }
        if (cancelled) {
            event.setCancelled(true);
            soundOrMessageNo(block, player);
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

    private void soundOrMessageNo(Block block, @Nullable Player player) {
        if (BoneMealLimiterPlugin.getInstance().getConfig().getBoolean("messages.enable")) {
            String message = BoneMealLimiterPlugin.getInstance().getConfig().getString("messages.bone_meal_disabled",
                    "");
            if (!message.isEmpty() && player != null) {
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
