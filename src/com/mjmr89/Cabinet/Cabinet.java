package com.mjmr89.Cabinet;

import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Cabinet extends JavaPlugin
{
  static final String pluginName = "Cabinet";

  final Logger log = Logger.getLogger("Minecraft");
  final Server server = getServer();

  private final CabinetBlockListener blockListener = new CabinetBlockListener(this);

  public void onDisable()
  {
  }

  public void onEnable()
  {
    PluginManager pm = getServer().getPluginManager();

    pm.registerEvent(Event.Type.PLAYER_INTERACT, this.blockListener, Event.Priority.Normal, this);

    PluginDescriptionFile pdfFile = getDescription();
    System.out.println(pdfFile.getName() + " version " + 
      pdfFile.getVersion() + " is enabled!");
  }

  public void onLoad()
  {
  }
}