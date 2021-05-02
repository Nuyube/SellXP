package xyz.nuyube.minecraft.sellxp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner; 
import org.bukkit.plugin.java.JavaPlugin;

final class Configuration {
 

  private static final String KEY_FUNCTION =
    "sellxp-worth-function", KEY_COEFFICIENT =
    "sellxp-worth-coefficient", KEY_CONSTANT =
    "sellxp-worth-constant", KEY_CONSTANT_THRESHOLD =
    "sellxp-constant-level-threshold", KEY_SELL_THRESHOLD =
    "sellxp-sell-level-threshold";

  private double coefficient = 2d, constant = 0d;
  private int LevelThresholdForConstant = 0;
  private int LevelThresholdForSell = 1;
  private String worthFunctionType = "EXPONENTIAL";

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

  private Messages messages = Messages.getInstance();

  public static void reload() {
    getInstance().init();
  }

  private final void readConfigFile() {
    messages.emitConsole("reading-config");
    //Get files
    File DataDirectory = JavaPlugin.getPlugin(SellXP.class).getDataFolder();
    File ConfigFile = new File(DataDirectory.getPath() + "/config.yml");
    Scanner fr = null;
    try {
      //Open our file
      fr = new Scanner(ConfigFile);
    } catch (FileNotFoundException e) {
      messages.emitConsoleSevere("config-not-found");
      return;
    }
    //Get keys that we're looking for
    ConfigKeyList Keys = new ConfigKeyList();
    Keys.addKey(new ConfigKey(KEY_FUNCTION, null));
    Keys.addKey(new ConfigKey(KEY_COEFFICIENT, null));
    Keys.addKey(new ConfigKey(KEY_CONSTANT, null));
    Keys.addKey(new ConfigKey(KEY_CONSTANT_THRESHOLD, null));
    Keys.addKey(new ConfigKey(KEY_SELL_THRESHOLD, null));
    //Read the file line by line
    while (fr.hasNextLine()) {
      String line = fr.nextLine();
      //If our key is found, fill it.
      for (ConfigKey x : Keys) {
        if (line.startsWith(x.name)) {
          line = line.substring((x.name + ": ").length()).trim();
          x.value = line;
        }
      } 
    }
    fr.close();
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
    messages.emitConsole("initializing-config");
    File DataDirectory ;
    File ConfigFile;

    DataDirectory = JavaPlugin.getPlugin(SellXP.class).getDataFolder();
    ConfigFile  = new File(DataDirectory + "/config.yml");
    
    
    //If our file or directory doesn't exist,
    if (!DataDirectory.exists() || !ConfigFile.exists()) {
      messages.emitConsole("config-not-exist-writing");
      //Write it
      writeNewConfigFile();
     }
     //Read the file (which might be new)
     readConfigFile();
  }

  private final void writeNewConfigFile() {
    //Save the bundled config.yml resource
    JavaPlugin.getPlugin(SellXP.class).saveResource("config.yml", true);
  }
}
