package com.mjmr89.Cabinet;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;

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

    // GroupManager
    public AnjoPermissionsHandler gmPermissions;
    Boolean gmPermissionsB = false;

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

    protected void setupPermissions() {
        Plugin permTest = pm.getPlugin("Permissions");

        if(permTest != null) {
            permissions = ((Permissions) permTest).getHandler();
            permissionsB = true;
            log("[" + pdfFile.getName() + "] Permissions " + (permTest.getDescription().getVersion()) + " found hooking in.");
        } else {
            permissionsB = false;
            setupGroupManager();
        }
    }

    protected void setupGroupManager() {
        Plugin permTest = pm.getPlugin("GroupManager");

        if (permTest != null) {
            gmPermissionsB = true;
            log("[" + pdfFile.getName() + "] GroupManager " + (permTest.getDescription().getVersion()) + " found hooking in.");
        } else {
            gmPermissionsB = false;
            log("[" + pdfFile.getName() + "] No Legacy Permissions plugins were found defaulting to SuperPerms.");
        }
    }

    public void log(Object loggedObject) {
        getServer().getConsoleSender().sendMessage(loggedObject.toString());
    }

    @SuppressWarnings("unused")
    public Boolean checkPermissions(Player player, String node, Boolean useOp) {
        if (!usePermissions)
            return true;

        if (permissionsB)
            if (permissions.has(player, node))
                return true;

        if (gmPermissionsB)
            if (gmPermissions.has(player, node))
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
