package com.cepi.bile;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class BileTools extends JavaPlugin {

	public final static String tag = ChatColor.GREEN + "[" + ChatColor.DARK_GRAY + "Bile" + ChatColor.GREEN + "]: "
			+ ChatColor.GRAY;

	private static HashMap<File, Long> modification = new HashMap<File, Long>();
	private static HashMap<File, Long> las = new HashMap<File, Long>();

	public static BileTools bile;
	private File folder = new File("plugins");
	private Sound successSound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;

	@Override
	public void onEnable() {

		bile = this;
		getCommand("bile").setExecutor(new BileCommand());
		getCommand("bile").setTabCompleter(new BileCommand());

		getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> onTick(), 20, 20);
	}

	public void reset(File f) {
		modification.put(f, f.length());
		las.put(f, f.lastModified());
	}

	public void onTick() {
		for (File i : folder.listFiles()) {
			if (i.isFile() && i.getName().toLowerCase().endsWith(".jar")) {
				if (!modification.containsKey(i)) {
					getLogger().log(Level.INFO, "Now Tracking: " + i.getName());
					modification.put(i, i.length());
					las.put(i, i.lastModified());

					try {
						BileUtils.load(i);

						for (Player player : Bukkit.getOnlinePlayers()) {
							if (player.hasPermission("bile.use")) {
								player.sendMessage(tag + "Hot Dropped " + ChatColor.WHITE + i.getName());
								player.playSound(player.getLocation(), successSound, 1f, 1.9f);
							}
						}
					}

					catch (Throwable e) {
						e.printStackTrace();
						for (Player k : Bukkit.getOnlinePlayers()) {
							if (k.hasPermission("bile.use")) {
								k.sendMessage(tag + "Failed to hot drop " + ChatColor.RED + i.getName());
							}
						}
					}
				}

				if (modification.get(i) != i.length() || las.get(i) != i.lastModified()) {
					modification.put(i, i.length());
					las.put(i, i.lastModified());
					for (Plugin j : Bukkit.getServer().getPluginManager().getPlugins()) {
						if (BileUtils.getPluginFile(j) != null
								&& BileUtils.getPluginFile(j).getName().equals(i.getName())) {
							getLogger().log(Level.INFO, "Plugin Reloading: " + j.getName() + " <-> " + i.getName());

							try {

								BileUtils.reload(j);

								for (Player k : Bukkit.getOnlinePlayers()) {
									k.sendMessage(tag + "Reloaded " + ChatColor.WHITE + j.getName());
									k.playSound(k.getLocation(), successSound, 1f, 1.9f);
								}
							}

							catch (Throwable e) {
								for (Player k : Bukkit.getOnlinePlayers()) {
									if (k.hasPermission("bile.use")) {
										k.sendMessage(tag + "Failed to Reload " + ChatColor.RED + j.getName());
									}
								}

								e.printStackTrace();
							}

							break;
						}
					}
				}
			}
		}
	}
}
