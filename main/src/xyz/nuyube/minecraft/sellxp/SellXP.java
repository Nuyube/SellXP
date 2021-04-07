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

  public static double Coefficient, Constant;
  public static int LevelThresholdForConstant;
  public static int LevelThresholdForSell;
  public static String WorthFunctionType;
  static Logger PluginLogger = null;
  static RegisteredServiceProvider<Economy> rsp = null;
  static Economy econ = null;

  private void WriteNewConfigFile() {
    File DataDirectory = getDataFolder();
    File To = new File(DataDirectory.getAbsolutePath() + "/config.yml");
    DataDirectory.mkdir();
    try {
      To.createNewFile();
      PrintWriter pw = new PrintWriter(To);
      pw.write(
        "#DO NOT change this value - it's used to figure out how to read this config!\nsellxp-config-version: 1\n\n#Type of worth function\n# LINEAR: Worth = (coefficient)(level) + (constant)\n# EXPONENTIAL: Worth = level^(coefficient) + (constant)\nsellxp-worth-function: EXPONENTIAL\n\nsellxp-worth-coefficient: 2\nsellxp-worth-constant: 0\n\n#A user must be at least this level to get the Constant part of the formula (setting to 0 will allow users to spam /sellxp confirm for infinite money)\nsellxp-constant-level-threshold: 5\n\n#A user must be at least this level to sell their XP\nsellxp-sell-level-threshold: 1"
      );
      pw.close();
    } catch (IOException e) {
      PluginLogger.severe("The config file could not be written");
    }
  }

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
    //Read our configuration
    File DataDirectory = getDataFolder();
    File ConfigFile = new File(DataDirectory.getPath() + "/config.yml");
    if (!DataDirectory.exists() || !ConfigFile.exists()) {
      WriteNewConfigFile();
    } 
    else {
      Scanner fr = null;
      try {
        fr = new Scanner(ConfigFile);
      } catch (FileNotFoundException e) {
        PluginLogger.severe(e.getMessage());
        return;
      }
      boolean readFunction = false, readCoeff = false, readConst =
        false, readSellThreshold = false, readConstantThreshold = false;
      while (fr.hasNextLine()) {
        String line = fr.nextLine(); 
        if (line.startsWith("sellxp-worth-function")) {
          line = line.substring("sellxp-worth-function: ".length()).trim();
          WorthFunctionType = line.toUpperCase();
          readFunction = true;
        } else if (line.startsWith("sellxp-worth-coefficient")) {
          line = line.substring("sellxp-worth-coefficient: ".length()).trim();
          Coefficient = Double.parseDouble(line);
          readCoeff = true;
        } else if (line.startsWith("sellxp-worth-constant")) {
          line = line.substring("sellxp-worth-constant: ".length()).trim();
          Constant = Double.parseDouble(line);
          readConst = true;
        } else if (line.startsWith("sellxp-constant-level-threshold")) {
          line = line.substring("sellxp-worth-constant: ".length()).trim();
          LevelThresholdForConstant = Integer.parseInt(line);
          readConstantThreshold = true;
        } else if (line.startsWith("sellxp-sell-level-threshold")) {
          line = line.substring("sellxp-worth-constant: ".length()).trim();
          LevelThresholdForSell = Integer.parseInt(line);
          readSellThreshold = true;
        }
      }
      fr.close();
      if (
        !readSellThreshold ||
        !readConst ||
        !readCoeff ||
        !readFunction ||
        !readConstantThreshold
      ) {
        WriteDifferentialConfigFile(
          ConfigFile,
          readConst,
          readCoeff,
          readFunction,
          readSellThreshold,
          readConstantThreshold
        );
      }
      PluginLogger.info(
        "Function type: " +
        WorthFunctionType +
        " Coefficient: " +
        Coefficient +
        " Constant: " +
        Constant
      );
    }

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

  public void WriteDifferentialConfigFile(
    File ConfigFile,
    boolean readConst,
    boolean readCoeff,
    boolean readFunction,
    boolean readSellThreshold,
    boolean readConstantThreshold
  ) {
    try {
      PrintWriter pw = new PrintWriter(ConfigFile);
      if (!readConst) {
        pw.println(
          "\n#This amount will be added to the worth command if the user meets the level requirement."
        );
        pw.println("sellxp-worth-constant: 0");
        Constant = 0;
      }
      if (!readCoeff) {
        pw.println(
          "\n#This amount will be used in the multiplicative / exponential part of the worth function."
        );
        pw.println("sellxp-worth-coefficient: 2");
        Coefficient = 2;
      }
      if (!readFunction) {
        pw.println(
          "\n#This determines the function to use when calculating worth"
        );
        pw.println("sellxp-worth-function: EXPONENTIAL");
        WorthFunctionType = "EXPONENTIAL";
      }
      if (!readSellThreshold) {
        pw.println("\n#Users must be at least this level to sell their XP.");
        pw.println("sellxp-sell-level-threshold: 1");
        LevelThresholdForSell = 1;
      }
      if (!readConstantThreshold) {
        pw.println(
          "\n#Users must be at least this level to recieve the constant part of worth."
        );
        pw.println("sellxp-constant-level-threshold: 5");
        LevelThresholdForConstant = 5;
      }
      pw.close();
    } catch (FileNotFoundException e) {
      PluginLogger.severe("The config file could not be appended.");
    }
  }
}
