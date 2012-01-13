package com.mjmr89.Cabinet;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class Cabinet extends JavaPlugin {
    PluginManager pm;
    PluginDescriptionFile pdfFile;

    // Listeners
    CabinetPlayerListener pListener;
    CConfigListener cListener;

    // Permissions
    public PermissionHandler permissions;
    Boolean permissionsB = false;

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

        pListener = new CabinetPlayerListener(this);
        cListener = new CConfigListener(this);

        setupPermissions();

        setupConfigs();

        pm.registerEvent(Event.Type.PLAYER_INTERACT, this.pListener, Event.Priority.Monitor, this);

        PluginDescriptionFile pdfFile = getDescription();

        log("[" + pdfFile.getName() + "]" + " version " + pdfFile.getVersion() + " is enabled!");
    }

    public void onDisable() {
        log("[" + pdfFile.getName() + "]" + " version " + pdfFile.getVersion() + " is disabled!");
    }

    void setupPermissions() {
        Plugin permTest = pm.getPlugin("Permissions");

        if(permTest != null) {
            permissions = ((Permissions) permTest).getHandler();
            permissionsB = true;
            log("[" + pdfFile.getName() + "] Permissions " + (permTest.getDescription().getVersion()) + " found hooking in.");
        } else {
            permissionsB = false;
        }
    }

    public void log(Object loggedObject) {
        try {
            getServer().getConsoleSender().sendMessage(loggedObject.toString());
        } catch (IncompatibleClassChangeError ignored) {
            System.out.println(loggedObject);
        }
    }

    @SuppressWarnings("unused")
    public Boolean checkPermissions(Player player, String node, Boolean useOp) {
        if (!usePermissions)
            return true;

        if (permissionsB)
            if (permissions.has(player, node))
                return true;

        if (useOp)
            return player.isOp();

        return player.hasPermission(node);

    }

    protected void setupConfigs() {
        cListener.checkConfig();
        cListener.loadConfig();
    }
}
