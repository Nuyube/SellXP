package xyz.nuyube.minecraft.sellxp;

import java.util.logging.Logger;
 
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import de.jeff_media.updatechecker.UpdateChecker;
import net.milkbowl.vault.economy.Economy;

public final class SellXP extends JavaPlugin {
 
  static Logger PluginLogger = null;
  static RegisteredServiceProvider<Economy> rsp = null;
  static Economy econ = null;


  @Override
  public void onEnable() {
    //Start our plugin logger
    PluginLogger = getLogger();
    //Check for updates
    UpdateChecker.init(this, 91031).checkNow(); 

    //Register our sellxp command and its alias
    this.getCommand("sellxp").setExecutor(new XPSellCommand());
    this.getCommand("xpsell").setExecutor(new XPSellCommand());
    //Get the economy
    rsp = getServer().getServicesManager().getRegistration(Economy.class);
    //If the economy can't be found, log it.
    if (rsp == null) {
      PluginLogger.severe(
        "[Nuyube's SellXP] The economy provider could not be found."
      );
    }
    //Else, set the economy.
    else econ = rsp.getProvider();
    //ReadConfiguration();
  }

  @Override
  //We don't actually do anything on disable.
  public void onDisable() {
    PluginLogger.info("[Nuyube's SellXP] Disabled.");
    econ = null;
    rsp = null;
    PluginLogger = null;
  }

}
