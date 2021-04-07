package xyz.nuyube.minecraft.sellxp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class SellXP extends JavaPlugin {
 
  static Logger PluginLogger = null;
  static RegisteredServiceProvider<Economy> rsp = null;
  static Economy econ = null;


  @Override
  public void onEnable() {
    //Start our plugin logger
    PluginLogger = getLogger();
    //Check for updates
    new UpdateChecker(this, 91031)
      .getVersion(
          version -> {
            if (
              this.getDescription().getVersion().equalsIgnoreCase(version)
            ) {} else {
              Bukkit
                .getConsoleSender()
                .sendMessage(
                  ChatColor.GREEN +
                  "[Nuyube's SellXP] There is a new update available!"
                );
            }
          }
        );
      Configuration.Init();

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
