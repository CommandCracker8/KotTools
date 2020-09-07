package com.cepi.bile

import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.util.StringUtil
import java.util.*

class BileCommand : CommandExecutor, TabCompleter {
    private var tag = BileTools.tag

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {

        require(sender.hasPermission("bile.use")) {
            sender.sendMessage(tag + "You need bile.use or OP.")
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage("$tag/bile load <plugin>")
            sender.sendMessage("$tag/bile unload <plugin>")
            sender.sendMessage("$tag/bile reload <plugin>")
            sender.sendMessage("$tag/bile uninstall <plugin>")
        } else {
            if (args[0].equals("load", ignoreCase = true)) {
                if (args.size > 1) {
                    for (i in 1 until args.size) {
                        try {
                            val s = BileUtils.getPluginFile(args[i])
                            if (s == null) {
                                sender.sendMessage(tag + "Couldn't find \"" + args[i] + "\".")
                                continue
                            }
                            try {
                                BileUtils.load(s)
                                val n = BileUtils.getPluginByName(args[i])!!.name
                                sender.sendMessage(tag + "Loaded " + ChatColor.WHITE + n + ChatColor.GRAY + " from "
                                        + ChatColor.WHITE + s.name)
                            } catch (e: Throwable) {
                                sender.sendMessage(tag + "Couldn't load \"" + args[i] + "\".")
                                e.printStackTrace()
                            }
                        } catch (e: Throwable) {
                            sender.sendMessage(tag + "Couldn't load or find \"" + args[i] + "\".")
                            e.printStackTrace()
                        }
                    }
                } else {
                    sender.sendMessage("$tag/bile load <PLUGIN>")
                }
            }

            if (args[0].equals("uninstall", ignoreCase = true)) {
                if (args.size > 1) {
                    for (i in 1 until args.size) {
                        try {
                            val s = BileUtils.getPluginFile(args[i])
                            if (s == null) {
                                sender.sendMessage(tag + "Couldn't find \"" + args[i] + "\".")
                                continue
                            }
                            try {
                                val n = BileUtils.getPluginName(s)
                                BileUtils.delete(s)
                                if (!s.exists()) {
                                    sender.sendMessage(tag + "Uninstalled " + ChatColor.WHITE + n + ChatColor.GRAY
                                            + " from " + ChatColor.WHITE + s.name)
                                } else {
                                    sender.sendMessage(tag + "Uninstalled " + ChatColor.WHITE + n + ChatColor.GRAY
                                            + " from " + ChatColor.WHITE + s.name)
                                    sender.sendMessage(
                                            tag + "But it looks like we can't delete it. You may need to delete "
                                                    + ChatColor.RED + s.name + ChatColor.GRAY
                                                    + " before installing it again.")
                                }
                            } catch (e: Throwable) {
                                sender.sendMessage(tag + "Couldn't uninstall \"" + args[i] + "\".")
                                e.printStackTrace()
                            }
                        } catch (e: Throwable) {
                            sender.sendMessage(tag + "Couldn't uninstall or find \"" + args[i] + "\".")
                            e.printStackTrace()
                        }
                    }
                } else {
                    sender.sendMessage("$tag/bile uninstall <PLUGIN>")
                }
            } else if (args[0].equals("unload", ignoreCase = true)) {
                if (args.size > 1) {
                    for (i in 1 until args.size) {
                        try {
                            val s = BileUtils.getPluginByName(args[i])
                            if (s == null) {
                                sender.sendMessage(tag + "Couldn't find \"" + args[i] + "\".")
                                continue
                            }
                            val sn = s.name
                            BileUtils.unload(s)
                            val n = BileUtils.getPluginFile(args[i])
                            sender.sendMessage(tag + "Unloaded " + ChatColor.WHITE + sn + ChatColor.GRAY + " ("
                                    + ChatColor.WHITE + n!!.name + ChatColor.GRAY + ")")
                        } catch (e: Throwable) {
                            sender.sendMessage(tag + "Couldn't unload \"" + args[i] + "\".")
                            e.printStackTrace()
                        }
                    }
                } else {
                    sender.sendMessage("$tag/bile unload <PLUGIN>")
                }
            } else if (args[0].equals("reload", ignoreCase = true)) {
                if (args.size > 1) {
                    for (i in 1 until args.size) {
                        try {
                            val s = BileUtils.getPluginByName(args[i])
                            if (s == null) {
                                sender.sendMessage(tag + "Couldn't find \"" + args[i] + "\".")
                                continue
                            }
                            try {
                                val sn = s.name
                                BileUtils.reload(s)
                                val n = BileUtils.getPluginFile(args[i])
                                sender.sendMessage(tag + "Reloaded " + ChatColor.WHITE + sn + ChatColor.GRAY + " ("
                                        + ChatColor.WHITE + n!!.name + ChatColor.GRAY + ")")
                            } catch (e: Throwable) {
                                sender.sendMessage(tag + "Couldn't reload \"" + args[i] + "\".")
                                e.printStackTrace()
                            }
                        } catch (e: Throwable) {
                            sender.sendMessage(tag + "Couldn't reload or find \"" + args[i] + "\".")
                            e.printStackTrace()
                        }
                    }
                } else {
                    sender.sendMessage("$tag/bile reload <PLUGIN>")
                }
            }
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> {
        val finalList: MutableList<String> = ArrayList()
        if (args.size > 1) {
            val list = Bukkit.getPluginManager().plugins.map { it.name }.toList()
            StringUtil.copyPartialMatches(args[1], list, finalList)
            finalList.sort()
        } else if (args.isNotEmpty()) {
            val list: List<String> = listOf("load", "unload", "reload", "uninstall")
            StringUtil.copyPartialMatches(args[0], list, finalList)
            finalList.sort()
        }
        return finalList
    }
}