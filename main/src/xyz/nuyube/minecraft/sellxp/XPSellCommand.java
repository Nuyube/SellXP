package xyz.nuyube.minecraft.sellxp;

import java.util.HashMap;

import com.github.nuyube.messager.Messages;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.milkbowl.vault.economy.EconomyResponse;

final class XPSellCommand implements CommandExecutor {

    @Override
    // When our users use the xpsell command
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Messages messages;
        messages = SellXP.getInstance().getMessages();
        Configuration config;

        config = Configuration.getInstance();

        // Check that the sender is a player
        if (sender instanceof Player) {
            // Get the player
            Player player;
            int playerLevel;

            player = (Player) sender;
            playerLevel = player.getLevel();

            double constant;
            double xpValue;

            xpValue = 0;
            constant = config.getConstant();

            if (playerLevel < config.getConstLevel())
                constant = 0;

            if (config.getFunction().equals("EXPONENTIAL"))
                xpValue = (Math.pow(playerLevel, config.getCoefficient())) + constant;

            else if (config.getFunction().equals("LINEAR"))
                xpValue = (config.getCoefficient() * playerLevel) + constant;

            HashMap<String, String> replacements;

            replacements = new HashMap<String, String>();

            replacements.put("%PLAYER%", player.getDisplayName());
            replacements.put("%LEVEL%", String.valueOf(playerLevel));
            replacements.put("%WORTH%", String.valueOf(xpValue));
            replacements.put("%LEVELREQ%", String.valueOf(config.getSellLevel()));

            // If they're trying to sell their XP
            if (args.length > 0 && args[0].equals("confirm")) {

                // If they do not have permission, tell them and return
                if (!player.hasPermission("sellxp.sell")) {
                    player.sendMessage(messages.get("sell-no-permission"));
                    return true;
                }
                // If the economy isn't available, tell them and return
                if (SellXP.econ == null) {
                    player.sendMessage(messages.get("economy-unavailable"));
                    return true;
                }

                if (xpValue <= 0) {
                    player.sendMessage(messages.getKeyWithReplacements("xp-worthless", replacements));
                    return true;
                }

                if (playerLevel < config.getSellLevel()) {
                    player.sendMessage(messages.getKeyWithReplacements("sell-no-level", replacements));
                    return true;
                }
                messages.emitConsole("player-selling", replacements);

                // Credit the player
                EconomyResponse er = SellXP.econ.depositPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()),
                        player.getWorld().getName(), xpValue);
                if (er.transactionSuccess()) {
                    // Notify the player
                    player.sendMessage(messages.getKeyWithReplacements("sale-finished", replacements));

                    // Reset the player's XP
                    player.setLevel(0);
                    player.setExp(0);
                } else {
                    replacements.put("%ERROR%", er.errorMessage);
                    messages.emitConsoleSevere("unspecified-error", replacements);
                }
                return true;
            }
            // If they're trying to reload the plugin
            if (args.length > 0 && args[0].equals("reload")) {
                // Check if they have permission
                if (!player.hasPermission("sellxp.reload")) {
                    messages.emitConsole("reload-no-permission");
                    return true;
                }
                // Notify player & server of reload
                messages.emitConsole("reloading-sellxp");
                player.sendMessage(messages.get("reloading-sellxp"));

                // Reload the config
                Configuration.reload();
                // Notify player & server of completion
                messages.emitConsole("generic-done");
                player.sendMessage(messages.get("generic-done"));
                return true;
            }
            player.sendMessage(messages.getKeyWithReplacements("xp-worth", replacements));
            player.sendMessage(messages.get("use-confirm"));
        }
        return true;
    }
}
