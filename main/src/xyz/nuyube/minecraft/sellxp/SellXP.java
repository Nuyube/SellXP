package xyz.nuyube.minecraft.sellxp;

import java.io.FileNotFoundException;
import java.util.logging.Logger;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import de.jeff_media.updatechecker.UpdateChecker;
import net.milkbowl.vault.economy.Economy;
import com.github.nuyube.messager.Messages;

public final class SellXP extends JavaPlugin {

    Logger logger = null;
    static RegisteredServiceProvider<Economy> rsp = null;
    static Economy econ = null;
    private Messages messages;

    private static SellXP instance = null;

    public Messages getMessages() {
        return messages;
    }

    public static SellXP getInstance() {
        if (instance == null) {
            instance = new SellXP();
        }
        return instance;
    }

    public SellXP() {
        logger = getLogger();
        instance = this;
    }

    @Override
    public void onEnable() {
        // Start our plugin logger
        messages = Messages.getInstance();

        try {
            logger.info("Messages dir: " + getDataFolder().getAbsolutePath() + "/messages.yml");
            messages.init(getLogger(), getDataFolder().getAbsolutePath() + "/messages.yml");
        } catch (FileNotFoundException fnfe) {
            logger.severe("Could not initialize messages... Trying to write the file. "+
            "If this is the first time you're running your server with SellXP, "+
            "feel free to ignore this message."); 
            saveResource("messages.yml", true);
            try {
                messages.init(getLogger(), getDataFolder().getAbsolutePath() + "/messages.yml");
            } catch (FileNotFoundException fnfe2) {
                logger.severe("Failed to initialize messages again.");
            }
        }
        messages.emitConsole("enabling");

        // Check for updates
        UpdateChecker.init(this, 91031).checkNow();

        // Register our sellxp command and its alias
        this.getCommand("sellxp").setExecutor(new XPSellCommand());
        // Get the economy
        rsp = getServer().getServicesManager().getRegistration(Economy.class);
        // If the economy can't be found, log it.
        if (rsp == null) {
            messages.emitConsoleSevere("economy-unavailable");
            // Throw an exception to disable the plugin.
            rsp.getProvider();
        }
        // Else, set the economy.
        else
            econ = rsp.getProvider();

        messages.emitConsole("enabled");
    }

    @Override
    // We don't actually do anything on disable.
    public void onDisable() {
        messages.emitConsole("disabling");

        econ = null;
        rsp = null;
        messages.emitConsole("disabled");
        logger = null;
    }

}
