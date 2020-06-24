package com.cepi.bile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;

import net.md_5.bungee.api.ChatColor;

public class BileCommand implements CommandExecutor, TabCompleter {

	public String tag = ChatColor.GREEN + "[" + ChatColor.DARK_GRAY + "Bile" + ChatColor.GREEN + "]: " + ChatColor.GRAY;

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("bile.use")) {
			sender.sendMessage(tag + "You need bile.use or OP.");
			return true;
		}

		if (args.length == 0) {
			sender.sendMessage(tag + "/bile load <plugin>");
			sender.sendMessage(tag + "/bile unload <plugin>");
			sender.sendMessage(tag + "/bile reload <plugin>");
			sender.sendMessage(tag + "/bile uninstall <plugin>");
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

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> finalList = new ArrayList<>();
		if (args.length > 1) {

			List<String> list = new ArrayList<>();

			for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
				list.add(plugin.getName());
			}

			StringUtil.copyPartialMatches(args[1], list, finalList);
			Collections.sort(finalList);

		} else if (args.length > 0) {
			List<String> list = new ArrayList<>();

			list.add("load");
			list.add("unload");
			list.add("reload");
			list.add("uninstall");

			StringUtil.copyPartialMatches(args[0], list, finalList);
			Collections.sort(finalList);
		}
		return finalList;
	}

}
