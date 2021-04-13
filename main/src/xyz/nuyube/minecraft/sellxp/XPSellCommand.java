package xyz.nuyube.minecraft.sellxp;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player; 

final class XPSellCommand implements CommandExecutor {

  @Override
  //When our users use the xpsell command
  public boolean onCommand(
    CommandSender sender,
    Command command,
    String label,
    String[] args
  ) {
    //Check that the sender is a player
    if (sender instanceof Player) {
      //Get the player
      Player p = (Player) sender;
      //Get the player's level
      int TotalEXP = p.getLevel();
      Configuration config = Configuration.getInstance();
      if (TotalEXP < config.getLevelForSell()) {
        p.sendRawMessage(
          "Sorry, but you can't sell your XP right now. You must be at least Level " +
          config.getLevelForSell()
        );
        return true;
      }

      //Calculate the XP worth
      double Worth = 0;
      //Implement the level threshold for the constant.
      double ConstantCopy = config.getConstant();
      if (TotalEXP < config.getLevelForConst()) ConstantCopy = 0;
      if (config.getFunctionType().equals("EXPONENTIAL")) {
        Worth = (Math.pow(TotalEXP, config.getCoefficient())) + ConstantCopy;
      } else if (config.getFunctionType().equals("LINEAR")) {
        Worth = (config.getCoefficient() * TotalEXP) + ConstantCopy;
      }
      if (Worth < 0) {
        p.sendRawMessage(
          "The worth of your XP is less than 0, so it's impossible to sell."
        );
        return true;
      }
      //If they're trying to sell their XP
      if (args.length > 0 && args[0].equals("confirm")) {
        //Check if they have permission
        if (p.hasPermission("sellxp.sell")) {
          //If the economy is unreachable,
          if (SellXP.econ == null) {
            //Notify the player and shortcut execution.
            p.sendRawMessage("The Vault Economy Service is not available.");
            return true;
          }
          //Log them selling XP
          SellXP.PluginLogger.info(
            "[Nuyube's SellXP] Player " +
            p.getDisplayName() +
            " selling " +
            TotalEXP +
            " levels of EXP for $" +
            Worth +
            "."
          );
          //Notify the player
          p.sendRawMessage("Sold " + TotalEXP + " levels for $" + Worth + ".");
          //Credit the player
          SellXP.econ.depositPlayer(
            Bukkit.getOfflinePlayer(p.getUniqueId()),
            p.getWorld().getName(),
            Worth
          );
          //Reset the player's XP
          p.setLevel(0);
          p.setExp(0);
          //Return a success.
          return true;
        }
        //Notify the player of lacking permissions
        p.sendRawMessage(
          "Sorry, but you do not have permission to perform this command."
        );
        return true;
      }
      //If they're trying to sell their XP
      if (args.length > 0 && args[0].equals("reload")) {
        //Check if they have permission
        if (p.hasPermission("sellxp.reload")) {
          SellXP.PluginLogger.info("Reloading SellXP...");
          p.sendRawMessage("Reloading SellXP.");
          Configuration.reload();
          SellXP.PluginLogger.info("Done.");
          p.sendRawMessage("Done.");
          return true;
        }
        //Notify the player of lacking permissions
        p.sendRawMessage(
          "Sorry, but you do not have permission to reload SellXP."
        );
        return true;
      }
      //Notify the player of their XP's worth, and tell them how to sell their XP.
      p.sendRawMessage(
        "Your XP is worth $" + Worth + ". Use /sellxp confirm to sell your XP."
      );
    }
    return true;
  }
}
