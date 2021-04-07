package xyz.nuyube.minecraft.sellxp;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin; 
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.milkbowl.vault.economy.Economy;

public class SellXP extends JavaPlugin {
    static Logger PluginLogger = null;
    static RegisteredServiceProvider<Economy> rsp = null;
    static Economy econ = null;
    @Override
    public void onEnable() {
        //Start our plugin logger
        PluginLogger = getLogger();
        //Register our sellxp command and its alias
        this.getCommand("sellxp").setExecutor(new XPSellCommand());
        this.getCommand("xpsell").setExecutor(new XPSellCommand());
        //Get the economy
        rsp = getServer().getServicesManager().getRegistration(Economy.class);
        //If the economy can't be found, log it.
        if(rsp == null) {
            PluginLogger.severe("The economy provider could not be found."); 
        }
        //Else, set the economy.
        else econ = rsp.getProvider();
        //ReadConfiguration();
    } 
    
    @Override
    //We don't actually do anything on disable.
    public void onDisable() {
        econ = null;
        rsp= null;
        PluginLogger = null;
    } 
}
