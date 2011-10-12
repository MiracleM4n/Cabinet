package com.mjmr89.Cabinet;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;

import java.io.File;
import java.io.IOException;

public class CConfigListener {
        Cabinet plugin;
        Boolean hasChanged = false;

        public CConfigListener(Cabinet plugin) {
            this.plugin = plugin;
        }

    protected void loadConfig() {
        YamlConfiguration config = plugin.cConfig;

        plugin.usePermissions = config.getBoolean("use-Permissions", plugin.usePermissions);
    }

    protected void defaultConfig() {
        YamlConfiguration config = plugin.cConfig;
        YamlConfigurationOptions configO = config.options();

        configO.header(
            "Cabinet configuration file"
        );

        config.set("use-Permissions", plugin.usePermissions);

        try {
            config.save(plugin.cConfigF);
        } catch (IOException ignored) {}
    }

    protected void checkConfig() {
        YamlConfiguration config = plugin.cConfig;
        YamlConfigurationOptions configO = config.options();

        if (!(new File(plugin.getDataFolder(), "config.yml")).exists()) {
            defaultConfig();
        }

        checkOption(config, "use-Permissions", plugin.usePermissions);

        if (hasChanged) {
            configO.header(
                    "Cabinet configuration file"
            );

            try {
                config.save(plugin.cConfigF);
            } catch (IOException ignored) {}

            plugin.log("[" + plugin.pdfFile.getName() + "]" + " config.yml has been updated.");
        }
    }

    protected void checkOption(YamlConfiguration config, String option, Object dOption) {
        if (config.get(option) == null) {
            config.set(option, dOption);
            hasChanged = true;
        }
    }
}
