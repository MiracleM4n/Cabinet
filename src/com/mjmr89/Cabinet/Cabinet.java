package com.mjmr89.Cabinet;

import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.plugin.Plugin;


public class Cabinet extends JavaPlugin
{
	static final String pluginName = "Cabinet";

  	final Logger log = Logger.getLogger("Minecraft");
  	final Server server = getServer();

  	private final CabinetBlockListener blockListener = new CabinetBlockListener(this);
	public static PermissionHandler Permissions;

	public void onDisable()
	{
		PluginDescriptionFile pdfFile = getDescription();
		System.out.println(pdfFile.getName() + " version " + 
				pdfFile.getVersion() + " is disabled!");
	}

	public void onEnable()
	{
		PluginManager pm = getServer().getPluginManager();

		pm.registerEvent(Event.Type.PLAYER_INTERACT, this.blockListener, Event.Priority.Normal, this);
		setupPermissions();
		PluginDescriptionFile pdfFile = getDescription();
		System.out.println(pdfFile.getName() + " version " + 
				pdfFile.getVersion() + " is enabled!");
	}

	public void onLoad()
	{
	}

	private void setupPermissions() 
	{
		Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");

		if (Cabinet.Permissions == null) 
		{
			if (test != null) 
			{
				Cabinet.Permissions = ((Permissions)test).getHandler();
				System.out.println("[Cabinet] Permissions found hooking in.");
			} 
			
			else 
			{
				System.out.println("[Cabinet] Permissions plugin not found, defaulting to ops.txt.");
			}
		}
	}
}