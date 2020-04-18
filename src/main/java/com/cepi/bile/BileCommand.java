package com.cepi.bile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import net.md_5.bungee.api.ChatColor;

public class BileCommand implements CommandExecutor {

	public String tag;
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equals("biletools")) {
			if (!sender.hasPermission("bile.use")) {
				sender.sendMessage(tag + "You need bile.use or OP.");
				return true;
			}

			if (args.length == 0) {
				sender.sendMessage(tag + "/bile load <plugin>");
				sender.sendMessage(tag + "/bile unload <plugin>");
				sender.sendMessage(tag + "/bile reload <plugin>");
				sender.sendMessage(tag + "/bile install <plugin> [version]");
				sender.sendMessage(tag + "/bile uninstall <plugin>");
				sender.sendMessage(tag + "/bile library [plugin]");
			}

			else {
				if (args[0].equalsIgnoreCase("load")) {
					if (args.length > 1) {
						for (int i = 1; i < args.length; i++) {
							try {
								File s = BileUtils.getPluginFile(args[i]);

								if (s == null) {
									sender.sendMessage(tag + "Couldn't find \"" + args[i] + "\".");
									continue;
								}

								try {
									BileUtils.load(s);
									String n = BileUtils.getPluginByName(args[i]).getName();
									sender.sendMessage(tag + "Loaded " + ChatColor.WHITE + n + ChatColor.GRAY + " from "
											+ ChatColor.WHITE + s.getName());
								}

								catch (Throwable e) {
									sender.sendMessage(tag + "Couldn't load \"" + args[i] + "\".");
									e.printStackTrace();
								}
							}

							catch (Throwable e) {
								sender.sendMessage(tag + "Couldn't load or find \"" + args[i] + "\".");
								e.printStackTrace();
							}
						}
					}

					else {
						sender.sendMessage(tag + "/bile load <PLUGIN>");
					}
				}

				if (args[0].equalsIgnoreCase("uninstall")) {
					if (args.length > 1) {
						for (int i = 1; i < args.length; i++) {
							try {
								File s = BileUtils.getPluginFile(args[i]);

								if (s == null) {
									sender.sendMessage(tag + "Couldn't find \"" + args[i] + "\".");
									continue;
								}

								try {
									String n = BileUtils.getPluginName(s);
									BileUtils.delete(s);

									if (!s.exists()) {
										sender.sendMessage(tag + "Uninstalled " + ChatColor.WHITE + n + ChatColor.GRAY
												+ " from " + ChatColor.WHITE + s.getName());
									}

									else {
										sender.sendMessage(tag + "Uninstalled " + ChatColor.WHITE + n + ChatColor.GRAY
												+ " from " + ChatColor.WHITE + s.getName());
										sender.sendMessage(
												tag + "But it looks like we can't delete it. You may need to delete "
														+ ChatColor.RED + s.getName() + ChatColor.GRAY
														+ " before installing it again.");
									}
								}

								catch (Throwable e) {
									sender.sendMessage(tag + "Couldn't uninstall \"" + args[i] + "\".");
									e.printStackTrace();
								}
							}

							catch (Throwable e) {
								sender.sendMessage(tag + "Couldn't uninstall or find \"" + args[i] + "\".");
								e.printStackTrace();
							}
						}
					}

					else {
						sender.sendMessage(tag + "/bile uninstall <PLUGIN>");
					}
				}

				if (args[0].equalsIgnoreCase("install")) {
					if (args.length > 1) {
						try {
							for (File i : new File(BileTools.getPlugin(BileTools.class).getDataFolder(), "library").listFiles()) {
								if (i.getName().toLowerCase().equals(args[1].toLowerCase())) {
									if (args.length == 2) {
										long highest = -100000;
										File latest = null;

										for (File j : i.listFiles()) {
											String v = j.getName().replace(".jar", "");
											List<Integer> d = new ArrayList<Integer>();

											for (char k : v.toCharArray()) {
												if (Character.isDigit(k)) {
													d.add(Integer.valueOf(k + ""));
												}
											}

											Collections.reverse(d);
											long g = 0;

											for (int k = 0; k < d.size(); k++) {
												g += (Math.pow(d.get(k), (k + 2)));
											}

											if (g > highest) {
												highest = g;
												latest = j;
											}
										}

										if (latest != null) {
											File ff = new File(BileUtils.getPluginsFolder(),
													i.getName() + "-" + latest.getName());
											BileUtils.copy(latest, ff);
											BileUtils.load(ff);
											sender.sendMessage(tag + "Installed " + ChatColor.WHITE + ff.getName()
													+ ChatColor.GRAY + " from library.");
										}
									}

									else {
										for (File j : i.listFiles()) {
											String v = j.getName().replace(".jar", "");

											if (v.equals(args[2])) {
												File ff = new File(BileUtils.getPluginsFolder(), i.getName() + "-" + v);
												BileUtils.copy(j, ff);
												BileUtils.load(ff);
												sender.sendMessage(tag + "Installed " + ChatColor.WHITE + ff.getName()
														+ ChatColor.GRAY + " from library.");
											}
										}
									}
								}
							}
						}

						catch (Throwable e) {
							sender.sendMessage(tag + "Couldn't install or find \"" + args[1] + "\".");
							e.printStackTrace();
						}
					}

					else {
						sender.sendMessage(tag + "/bile install <PLUGIN> [VERSION]");
					}
				}

				if (args[0].equalsIgnoreCase("library")) {
					if (args.length == 1) {
						try {
							for (File i : new File(BileTools.getPlugin(BileTools.class).getDataFolder(), "library").listFiles()) {
								long highest = -100000;
								File latest = null;

								for (File j : i.listFiles()) {
									String v = j.getName().replace(".jar", "");
									List<Integer> d = new ArrayList<Integer>();

									for (char k : v.toCharArray()) {
										if (Character.isDigit(k)) {
											d.add(Integer.valueOf(k + ""));
										}
									}

									Collections.reverse(d);
									long g = 0;

									for (int k = 0; k < d.size(); k++) {
										g += (Math.pow(d.get(k), (k + 2)));
									}

									if (g > highest) {
										highest = g;
										latest = j;
									}
								}

								if (latest != null) {
									boolean inst = false;
									String v = null;

									for (File k : BileUtils.getPluginsFolder().listFiles()) {
										if (BileUtils.isPluginJar(k)
												&& i.getName().equalsIgnoreCase(BileUtils.getPluginName(k))) {
											v = BileUtils.getPluginVersion(k);
											inst = true;
											break;
										}
									}

									if (inst) {
										sender.sendMessage(tag + i.getName() + " " + ChatColor.GREEN + "(" + v
												+ " installed) " + ChatColor.WHITE
												+ latest.getName().replace(".jar", "") + ChatColor.GRAY + " (latest)");
									}

									else {
										sender.sendMessage(tag + i.getName() + " " + ChatColor.WHITE
												+ latest.getName().replace(".jar", "") + ChatColor.GRAY + " (latest)");
									}
								}
							}
						}

						catch (Throwable e) {
							sender.sendMessage(tag + "Couldn't list library.");
							e.printStackTrace();
						}
					}

					else if (args.length > 1) {
						try {
							boolean dx = false;

							for (File i : new File(BileTools.getPlugin(BileTools.class).getDataFolder(), "library").listFiles()) {
								if (!i.getName().equalsIgnoreCase(args[1])) {
									continue;
								}

								dx = true;
								long highest = -100000;
								File latest = null;

								for (File j : i.listFiles()) {
									String v = j.getName().replace(".jar", "");
									List<Integer> d = new ArrayList<Integer>();

									for (char k : v.toCharArray()) {
										if (Character.isDigit(k)) {
											d.add(Integer.valueOf(k + ""));
										}
									}

									Collections.reverse(d);
									long g = 0;

									for (int k = 0; k < d.size(); k++) {
										g += (Math.pow(d.get(k), (k + 2)));
									}

									if (g > highest) {
										highest = g;
										latest = j;
									}
								}

								if (latest != null) {
									for (File j : i.listFiles()) {
										sender.sendMessage(tag + j.getName().replace(".jar", ""));
									}

									sender.sendMessage(tag + i.getName() + " " + ChatColor.WHITE
											+ latest.getName().replace(".jar", "") + ChatColor.GRAY + " (latest)");
								}
							}

							if (!dx) {
								sender.sendMessage(tag + "Couldn't find " + args[1] + " in library.");
							}
						}

						catch (Throwable e) {
							sender.sendMessage(tag + "Couldn't list library.");
							e.printStackTrace();
						}
					}

					else {
						sender.sendMessage(tag + "/bile library [PLUGIN]");
					}
				}

				else if (args[0].equalsIgnoreCase("unload")) {
					if (args.length > 1) {
						for (int i = 1; i < args.length; i++) {
							try {
								Plugin s = BileUtils.getPluginByName(args[i]);

								if (s == null) {
									sender.sendMessage(tag + "Couldn't find \"" + args[i] + "\".");
									continue;
								}

								String sn = s.getName();
								BileUtils.unload(s);
								File n = BileUtils.getPluginFile(args[i]);
								sender.sendMessage(tag + "Unloaded " + ChatColor.WHITE + sn + ChatColor.GRAY + " ("
										+ ChatColor.WHITE + n.getName() + ChatColor.GRAY + ")");
							}

							catch (Throwable e) {
								sender.sendMessage(tag + "Couldn't unload \"" + args[i] + "\".");
								e.printStackTrace();
							}
						}
					}

					else {
						sender.sendMessage(tag + "/bile unload <PLUGIN>");
					}
				}

				else if (args[0].equalsIgnoreCase("reload")) {
					if (args.length > 1) {
						for (int i = 1; i < args.length; i++) {
							try {
								Plugin s = BileUtils.getPluginByName(args[i]);

								if (s == null) {
									sender.sendMessage(tag + "Couldn't find \"" + args[i] + "\".");
									continue;
								}

								try {
									String sn = s.getName();
									BileUtils.reload(s);
									File n = BileUtils.getPluginFile(args[i]);
									sender.sendMessage(tag + "Reloaded " + ChatColor.WHITE + sn + ChatColor.GRAY + " ("
											+ ChatColor.WHITE + n.getName() + ChatColor.GRAY + ")");
								}

								catch (Throwable e) {
									sender.sendMessage(tag + "Couldn't reload \"" + args[i] + "\".");
									e.printStackTrace();
								}
							}

							catch (Throwable e) {
								sender.sendMessage(tag + "Couldn't reload or find \"" + args[i] + "\".");
								e.printStackTrace();
							}
						}
					}

					else {
						sender.sendMessage(tag + "/bile reload <PLUGIN>");
					}
				}
			}

			return true;
		}

		return false;
	}

}
