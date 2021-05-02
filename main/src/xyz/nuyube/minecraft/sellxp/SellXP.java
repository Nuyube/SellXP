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
private Messages messages;

  @Override
  public void onEnable()  {
    //Start our plugin logger
    PluginLogger = getLogger();
    messages = Messages.getInstance();

    messages.init();

    messages.emitConsole("enabling");

    //Check for updates
    UpdateChecker.init(this, 91031).checkNow();  
    
    //Register our sellxp command and its alias
    this.getCommand("sellxp").setExecutor(new XPSellCommand()); 
    //Get the economy
    rsp = getServer().getServicesManager().getRegistration(Economy.class);
    //If the economy can't be found, log it.
    if (rsp == null) {
      messages.emitConsoleSevere("economy-unavailable");
        //Throw an exception to disable the plugin.
       rsp.getProvider();
    }
    //Else, set the economy.
    else econ = rsp.getProvider(); 
    
    messages.emitConsole("enabled");
  }

  @Override
  //We don't actually do anything on disable.
  public void onDisable() {
    messages.emitConsole("disabling");
    
    econ = null;
    rsp = null;
    messages.emitConsole("disabled");
    PluginLogger = null;
  }

}
