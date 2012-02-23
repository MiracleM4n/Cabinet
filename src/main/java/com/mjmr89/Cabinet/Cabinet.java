package com.mjmr89.Cabinet;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Cabinet extends JavaPlugin {
    PluginManager pm;
    PluginDescriptionFile pdfFile;

    // Configuration
    YamlConfiguration cConfig = null;
    File cConfigF = null;

    // Boolean
    Boolean usePermissions = true;

    public void onEnable() {
        pm = getServer().getPluginManager();
        pdfFile = getDescription();

        cConfigF = new File(getDataFolder(), "config.yml");
        cConfig = YamlConfiguration.loadConfiguration(cConfigF);

        setupConfigs();

        pm.registerEvents(new CabinetPlayerListener(this), this);

        PluginDescriptionFile pdfFile = getDescription();

        log("[" + pdfFile.getName() + "]" + " version " + pdfFile.getVersion() + " is enabled!");
    }

    public void onDisable() {
        log("[" + pdfFile.getName() + "]" + " version " + pdfFile.getVersion() + " is disabled!");
    }

    public void log(Object loggedObject) {
        System.out.println(loggedObject);
    }

    public Boolean checkPermissions(Player player, String node, Boolean useOp) {
        if (!usePermissions)
            return true;

        if (useOp)
            return player.isOp();

        return player.hasPermission(node);

    }

    protected void setupConfigs() {
        new CConfigListener(this).checkConfig();
        new CConfigListener(this).loadConfig();
    }
}
