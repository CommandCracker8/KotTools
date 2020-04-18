package com.cepi.bile;

import java.io.File;
import java.io.IOException;
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
	
	private int cd = 10;
	
	private static HashMap<File, Long> mod = new HashMap<File, Long>();
	private static HashMap<File, Long> las = new HashMap<File, Long>();
	
	public static BileTools bile;
	private File folder;
	private File backoff;
	private Sound sx;

	public static void l(String s) {
		System.out.println("[Bile]: " + s);
	}

	public static void main(String[] a) {
		l("Init Standalone");
		File ff = new File("plugins");
		File fb = new File(ff, "BileTools");
		fb.mkdirs();
		l("===========================================");
		l("Plugins Folder: " + ff.getAbsolutePath());
		l("Bile Folder: " + fb.getAbsolutePath());
		File cf = new File(fb, "config.yml");
		l("Init Ghost Plugin");
		l("Load config: " + cf.getAbsolutePath());
		l("Start Pool");
		l("Service Start");
		l("===========================================");
		mod = new HashMap<File, Long>();
		las = new HashMap<File, Long>();

		for (File i : ff.listFiles()) {
			if (i.isFile() && i.getName().endsWith(".jar")) {
				mod.put(i, i.lastModified());
				las.put(i, i.length());
				l("Now tracking: " + i.getName());
			}
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!Thread.interrupted()) {
					try {
						try {
							for (File i : ff.listFiles()) {
								if (i.isDirectory() || !i.getName().endsWith(".jar")) {
									continue;
								}

								if (!mod.containsKey(i)) {
									mod.put(i, i.lastModified());
									las.put(i, i.length());
								}

								else if (mod.get(i) != i.lastModified() || las.get(i) != i.length()) {
									mod.put(i, i.lastModified());
									las.put(i, i.length());
								}

							}
						}

						catch (Throwable e) {
							e.printStackTrace();
						}

						Thread.sleep(500);
					}

					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}, "File Tracker").start();
		l("File Threads Started: 1t/1000ms");
		l("===========================================");
	}

	@Override
	public void onEnable() {

		bile = this;
		mod = new HashMap<File, Long>();
		las = new HashMap<File, Long>();
		folder = getDataFolder().getParentFile();
		backoff = new File(getDataFolder(), "backoff");
		backoff.mkdirs();
		
		getCommand("bile").setExecutor(new BileCommand());
		getCommand("bile").setTabCompleter(new BileCommand());

		for (Sound f : Sound.values()) {
			if (f.name().contains("ORB")) {
				sx = f;
			}
		}

		getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> onTick(), 10, 0);
	}

	public void reset(File f) {
		mod.put(f, f.length());
		las.put(f, f.lastModified());
	}

	@SuppressWarnings("deprecation")
	public void onTick() {
		if (cd > 0) {
			cd--;
		}

		for (File i : folder.listFiles()) {
			if (i.getName().toLowerCase().endsWith(".jar") && i.isFile()) {
				if (!mod.containsKey(i)) {
					getLogger().log(Level.INFO, "Now Tracking: " + i.getName());

					Bukkit.getScheduler().scheduleAsyncDelayedTask(bile, new Runnable() {
						@Override
						public void run() {
							Plugin plugin = BileUtils.getPlugin(i);

							if (plugin != null) {
								try {
									BileUtils.backup(plugin);
								}

								catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					});

					mod.put(i, i.length());
					las.put(i, i.lastModified());

					if (cd == 0) {
						Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
							@Override
							public void run() {
								try {
									BileUtils.load(i);

									for (Player k : Bukkit.getOnlinePlayers()) {
										if (k.hasPermission("bile.use")) {
											k.sendMessage(tag + "Hot Dropped " + ChatColor.WHITE + i.getName());
											k.playSound(k.getLocation(), sx, 1f, 1.9f);
										}
									}
								}

								catch (Throwable e) {
									for (Player k : Bukkit.getOnlinePlayers()) {
										if (k.hasPermission("bile.use")) {
											k.sendMessage(tag + "Failed to hot drop " + ChatColor.RED + i.getName());
										}
									}
								}
							}
						}, 5);
					}
				}

				if (mod.get(i) != i.length() || las.get(i) != i.lastModified()) {
					mod.put(i, i.length());
					las.put(i, i.lastModified());

					for (Plugin j : Bukkit.getServer().getPluginManager().getPlugins()) {
						if (BileUtils.getPluginFile(j).getName().equals(i.getName())) {
							getLogger().log(Level.INFO, "File change detected: " + i.getName());
							getLogger().log(Level.INFO, "Identified Plugin: " + j.getName() + " <-> " + i.getName());
							getLogger().log(Level.INFO, "Reloading: " + j.getName());

							try {

								BileUtils.reload(j);

								for (Player k : Bukkit.getOnlinePlayers()) {
									if (k.hasPermission("bile.use")) {
										k.sendMessage(tag + "Reloaded " + ChatColor.WHITE + j.getName());
										k.playSound(k.getLocation(), sx, 1f, 1.9f);
									}
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
