package xyz.nuyube.minecraft.sellxp;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

final class XPSellCommand implements CommandExecutor {

  @Override
  // When our users use the xpsell command
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    // Check that the sender is a player
    if (sender instanceof Player) {
      // Get the player
      Player player = (Player) sender;
      // Get the player's level
      int playerLevel = player.getLevel();
      Configuration config = Configuration.getInstance();
      if (playerLevel < config.getLevelForSell()) {
        player.sendRawMessage("Sorry, but you can't sell your XP right now. You must be at least Level "
            + config.getLevelForSell() + ".");
        return true;
      }

      // Calculate the XP worth
      double xpValue = 0;
      // Implement the level threshold for the constant.
      double constant = config.getConstant();
      if (playerLevel < config.getLevelForConst())
        constant = 0;
      if (config.getFunctionType().equals("EXPONENTIAL"))
        xpValue = (Math.pow(playerLevel, config.getCoefficient())) + constant;
      else if (config.getFunctionType().equals("LINEAR"))
        xpValue = (config.getCoefficient() * playerLevel) + constant;
      // If they're trying to sell their XP
      if (args.length > 0 && args[0].equals("confirm")) {
        // If they do not have permission, tell them and return
        if (!player.hasPermission("sellxp.sell")) {
          player.sendMessage("Sorry, but you do not have permission to sell xp. You need " + ChatColor.COLOR_CHAR
              + "asellxp.sell" + ChatColor.COLOR_CHAR + "f" + ".");
          return true;
        }
        // If the economy isn't available, tell them and return
        if (SellXP.econ == null) {
          player.sendMessage("The economy service is not available.");
          return true;
        }
        
        if (xpValue <= 0) {
          player.sendRawMessage("The worth of your XP is less than or equal to 0, so it's impossible to sell.");
          return true;
        }

        // Log the player's transaction
        SellXP.PluginLogger
            .info("Player " + player.getDisplayName() + " selling " + playerLevel + " levels of EXP for $" + xpValue + ".");

        // Credit the player
        SellXP.econ.depositPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), player.getWorld().getName(), xpValue);
        // Reset the player's XP
        player.setLevel(0);
        player.setExp(0);
        return true;
      }
      // If they're trying to reload the plugin
      if (args.length > 0 && args[0].equals("reload")) {
        // Check if they have permission
        if (!player.hasPermission("sellxp.reload")) {
          player.sendMessage("Sorry, but you can't reload SellXP. You need " + ChatColor.COLOR_CHAR + "asellxp.reload"
              + ChatColor.COLOR_CHAR + "f" + ".");
          return true;
        }
        //Notify player & server of reload
        SellXP.PluginLogger.info("Reloading SellXP...");
        player.sendRawMessage("Reloading SellXP.");

        //Reload the config
        Configuration.reload();
        
        //Notify player & server of completion
        SellXP.PluginLogger.info("Done.");
        player.sendRawMessage("Done.");
        return true;
      }
      // Notify the player of their XP's worth, and tell them how to sell their XP.
      player.sendRawMessage("Your XP is worth $" + xpValue + ". Use /sellxp confirm to sell your XP.");
    }
    return true;
  }
}
