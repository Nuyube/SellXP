package xyz.nuyube.minecraft.sellxp;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import java.io.IOException;

import com.github.nuyube.javayamlreader.JavaYAMLHelper;
import com.github.nuyube.messager.Messages;

import org.bukkit.plugin.java.JavaPlugin;

final class Configuration {
    private double coeff = 2d, constant = 0d;
    private int constLevel = 0;
    private int sellLevel = 1;
    private String worthFunc = "EXPONENTIAL";

    public int getSellLevel() {
        return sellLevel;
    }

    public int getConstLevel() {
        return constLevel;
    }

    public String getFunction() {
        return worthFunc;
    }

    public double getCoefficient() {
        return coeff;
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

    private Messages messages;

    public static void reload() {
        getInstance().init();
    }

    private final void readConfigFile() {
        Logger errorLogger = SellXP.getInstance().getLogger();
        messages.emitConsole("reading-config");
        // Get files
        File DataDirectory = SellXP.getInstance().getDataFolder();
        File ConfigFile = new File(DataDirectory.getPath() + "/config.yml");
        String fileContents;
        try {
            fileContents = Files.readString(Path.of(ConfigFile.getAbsolutePath()));
        } catch (IOException e) {
            messages.emitConsoleSevere("config-not-found");
            return;
        }
        String strConst, strCoeff, strThConst, strThSell;
        strConst = JavaYAMLHelper.getValueFromKey("sellxp-worth-constant", fileContents);
        strCoeff = JavaYAMLHelper.getValueFromKey("sellxp-worth-coefficient", fileContents);
        worthFunc = JavaYAMLHelper.getValueFromKey("sellxp-worth-function", fileContents);
        strThConst = JavaYAMLHelper.getValueFromKey("sellxp-constant-level-threshold", fileContents);
        strThSell = JavaYAMLHelper.getValueFromKey("sellxp-sell-level-threshold", fileContents);
        try {
            constant = Double.parseDouble(strConst);
            coeff = Double.parseDouble(strCoeff);
            constLevel = Integer.parseInt(strThConst);
            sellLevel = Integer.parseInt(strThSell);
        } catch (NumberFormatException nfe) {
            errorLogger.severe("Error reading config.yml: one of the numerical fields was formatted incorrectly!");
            // rethrow to disable the plugin instead of using zeroed values.
            throw nfe;
        }
        errorLogger.info("Function type: " + worthFunc + " Coefficient: " + coeff + " Constant: " + constant);
    }

    private final void init() {
        messages = SellXP.getInstance().getMessages();
        // Read our configuration
        messages.emitConsole("initializing-config");
        File DataDirectory;
        File ConfigFile;

        DataDirectory = SellXP.getInstance().getDataFolder();
        ConfigFile = new File(DataDirectory + "/config.yml");

        // If our file or directory doesn't exist,
        if (!DataDirectory.exists() || !ConfigFile.exists()) {
            messages.emitConsole("config-not-exist-writing");
            // Write it
            writeNewConfigFile();
        }
        // Read the file (which might be new)
        readConfigFile();
    }

    private final void writeNewConfigFile() {
        // Save the bundled config.yml resource
        SellXP.getInstance().saveResource("config.yml", true);
    }
}
