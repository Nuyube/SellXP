package xyz.nuyube.minecraft.sellxp;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin; 
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.milkbowl.vault.economy.Economy;

public class XPSellCommand implements CommandExecutor { 
    @Override
    //When our users use the xpsell command
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Check that the sender is a player
        if(sender instanceof Player) {
            //Get the player
            Player p = (Player)sender;
            //Get the player's level
            int TotalEXP = p.getLevel();
            //Calculate the XP worth
            double Worth = Math.pow(TotalEXP, 2);
            //If they're trying to sell their XP
            if(args.length > 0 && args[0].equals("confirm")) {
                //Check if they have permission
                for (PermissionAttachmentInfo pia : p.getEffectivePermissions()) {
                    if(pia.getPermission().startsWith("sellxp.sell") && pia.getValue()) {
                        //If the economy is unreachable,
                        if(SellXP.econ == null) {
                            //Notify the player and shortcut execution.
                            p.sendRawMessage("The Vault Economy Service is not available.");
                            return true;
                        }
                        //Log them selling XP
                        SellXP.PluginLogger.info("[Nuyube's SellXP] Player " + p.getDisplayName() + " selling " + TotalEXP + " levels of EXP for $" + Worth+ ".");
                        //Notify the player
                        p.sendRawMessage("Sold " + TotalEXP + " levels for $" + Worth + ".");
                        //Credit the player
                        SellXP.econ.depositPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()), p.getWorld().getName(), Worth);
                        //Reset the player's XP
                        p.setLevel(0);
                        p.setExp(0);
                        //Return a success.
                        return true;
                    }
                }
                //Notify the player of lacking permissions
                p.sendRawMessage("Sorry, but you do not have permission to perform this command.");
                return true;
            }
            //Notify the player of their XP's worth, and tell them how to sell their XP.
            p.sendRawMessage("Your XP is worth $" + Worth + ". Use /sellxp confirm to sell your XP."); 
        }
        return true;
    }
}