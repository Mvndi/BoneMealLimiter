package net.mvndicraft.bonemeallimiter;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

@CommandAlias("bonemeallimiter|bml")
public class BoneMealLimiterCommand extends BaseCommand {
    private static final String ADMIN_PERMISSION = "bonemeallimiter.admin";
    @Default
    @Description("Lists the version of the plugin")
    public static void onBml(CommandSender commandSender) {
        commandSender.sendMessage(Component.text(BoneMealLimiterPlugin.getInstance().toString()));
    }

    @Subcommand("reload")
    @Description("Reloads the plugin config and data file")
    @CommandPermission(ADMIN_PERMISSION)
    public static void onReload(CommandSender commandSender) {
        BoneMealLimiterPlugin.getInstance().reloadConfig();
        commandSender.sendMessage(Component.text("BoneMealLimiter reloaded"));
    }
}
