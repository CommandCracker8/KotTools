package com.cepi.bile.subcommands

import com.cepi.bile.BileTools
import com.cepi.bile.BileUtils
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.CommandSender

fun uninstallCommand(sender: CommandSender, args: Array<String>) {
    for (i in 1 until args.size) {
        try {
            val s = BileUtils.getPluginFile(args[i])
            if (s == null) {
                sender.sendMessage(BileTools.tag + "Couldn't find \"" + args[i] + "\".")
                continue
            }
            try {
                val n = BileUtils.getPluginName(s)
                BileUtils.delete(s)
                sender.sendMessage(BileTools.tag + "Uninstalled " + ChatColor.WHITE + n + ChatColor.GRAY
                        + " from " + ChatColor.WHITE + s.name)
                if (s.exists()) {
                    sender.sendMessage(
                            BileTools.tag + "But it looks like we can't delete it. You may need to delete "
                                    + ChatColor.RED + s.name + ChatColor.GRAY
                                    + " before installing it again.")
                }
            } catch (e: Throwable) {
                sender.sendMessage(BileTools.tag + "Couldn't uninstall \"" + args[i] + "\".")
                e.printStackTrace()
            }
        } catch (e: Throwable) {
            sender.sendMessage(BileTools.tag + "Couldn't uninstall or find \"" + args[i] + "\".")
            e.printStackTrace()
        }
    }
}