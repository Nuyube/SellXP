package xyz.nuyube.minecraft.sellxp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

final class Configuration {

  private static final int LOGGING_LEVEL = 4;
  private static final Logger logger = JavaPlugin
    .getPlugin(SellXP.class)
    .getLogger();

  private final void log(String message, int logLevel) {
    if (LOGGING_LEVEL >= logLevel) {
      logger.info(message);
    }
  }

  private static final String KEY_FUNCTION =
    "sellxp-worth-function", KEY_COEFFICIENT =
    "sellxp-worth-coefficient", KEY_CONSTANT =
    "sellxp-worth-constant", KEY_CONSTANT_THRESHOLD =
    "sellxp-constant-level-threshold", KEY_SELL_THRESHOLD =
    "sellxp-sell-level-threshold";

  private double coefficient, constant;
  private int LevelThresholdForConstant;
  private int LevelThresholdForSell;
  private String worthFunctionType;

  public int getLevelForSell() {
    return LevelThresholdForSell;
  }

  public int getLevelForConst() {
    return LevelThresholdForConstant;
  }

  public String getFunctionType() {
    return worthFunctionType;
  }

  public double getCoefficient() {
    return coefficient;
  }

  public double getConstant() {
    return constant;
  }

  private static Configuration instance = null;

  public static Configuration getInstance() {
    if (instance == null) {
      instance = new Configuration();
    }
    return instance;
  }

  private Configuration() {
    init();
  }

  public static void reload() {
    instance.init();
  }

  private final void readConfigFile() {
    log("Reading configuration file", 3);
    File DataDirectory = JavaPlugin.getPlugin(SellXP.class).getDataFolder();
    File ConfigFile = new File(DataDirectory.getPath() + "/config.yml");
    Scanner fr = null;
    try {
      fr = new Scanner(ConfigFile);
    } catch (FileNotFoundException e) {
      log("Failed to read config file: Not found", 0);
      return;
    }
    ConfigKeyList Keys = new ConfigKeyList();
    Keys.addKey(new ConfigKey(KEY_FUNCTION, null));
    Keys.addKey(new ConfigKey(KEY_COEFFICIENT, null));
    Keys.addKey(new ConfigKey(KEY_CONSTANT, null));
    Keys.addKey(new ConfigKey(KEY_CONSTANT_THRESHOLD, null));
    Keys.addKey(new ConfigKey(KEY_SELL_THRESHOLD, null));
    // = {"sellxp-worth-function", "sellxp-worth-coefficient", "sellxp-worth-constant", "sellxp-constant-level-threshold", "sellxp-sell-level-threshold"};
    while (fr.hasNextLine()) {
      String line = fr.nextLine();
      for (ConfigKey x : Keys) {
        if (line.startsWith(x.name)) {
          line = line.substring((x.name + ": ").length()).trim();
          x.value = line;
        }
      } 
      if (!Keys.getKeyByName(KEY_CONSTANT).isNull()) {
        constant = Keys.getKeyByName(KEY_CONSTANT).asDouble();
      }
      if (!Keys.getKeyByName(KEY_COEFFICIENT).isNull()) {
        coefficient = Keys.getKeyByName(KEY_COEFFICIENT).asDouble();
      }
      if (!Keys.getKeyByName(KEY_FUNCTION).isNull()) {
        worthFunctionType = Keys.getKeyByName(KEY_FUNCTION).asString();
      }
      if (!Keys.getKeyByName(KEY_CONSTANT_THRESHOLD).isNull()) {
        LevelThresholdForConstant =
          Keys.getKeyByName(KEY_CONSTANT_THRESHOLD).asInt();
      }
      if (!Keys.getKeyByName(KEY_SELL_THRESHOLD).isNull()) {
        LevelThresholdForSell = Keys.getKeyByName(KEY_SELL_THRESHOLD).asInt();
      }
    }
    fr.close();
    SellXP.PluginLogger.info(
      "Function type: " +
      worthFunctionType +
      " Coefficient: " +
      coefficient +
      " Constant: " +
      constant
    );
  }

  private final void init() {
    //Read our configuration
    log("Initializing configuration...", 1);
    File DataDirectory = JavaPlugin.getPlugin(SellXP.class).getDataFolder();
    File ConfigFile = new File(DataDirectory + "/config.yml");
    log("Data Directory is " + DataDirectory.getAbsolutePath(), 3);
    log("Config File is " + ConfigFile.getAbsolutePath(), 3);
    if (!DataDirectory.exists() || !ConfigFile.exists()) {
      log("Config does not exist... Writing.", 3);
      writeNewConfigFile();
    } else {
      log("Config file exists... Reading", 3);
      readConfigFile();
    }
  }

  private final void writeNewConfigFile() {
    JavaPlugin.getPlugin(SellXP.class).saveResource("config.yml", true);
  }
}
