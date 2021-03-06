package com.miraclem4n.cabinet;

import com.miraclem4n.cabinet.config.ConfigUtil;
import com.miraclem4n.cabinet.listeners.PlayerListener;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Cabinet extends JavaPlugin {
    PluginManager pm;
    PluginDescriptionFile pdfFile;

    // Boolean
    public static Boolean usePermissions = true;

    public void onEnable() {
        pm = getServer().getPluginManager();
        pdfFile = getDescription();

        setupConfigs();

        pm.registerEvents(new PlayerListener(this), this);

        PluginDescriptionFile pdfFile = getDescription();

        log("[" + pdfFile.getName() + "]" + " version " + pdfFile.getVersion() + " is enabled!");
    }

    public void onDisable() {
        log("[" + pdfFile.getName() + "]" + " version " + pdfFile.getVersion() + " is disabled!");
    }

    public void log(Object loggedObject) {
        System.out.println(loggedObject);
    }

    public Boolean checkPermissions(CommandSender sender, String node) {
        return !usePermissions || sender.hasPermission(node);
    }

    protected void setupConfigs() {
        ConfigUtil.initialize();
    }
}
