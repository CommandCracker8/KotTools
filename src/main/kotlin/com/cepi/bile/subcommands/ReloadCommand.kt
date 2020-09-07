package com.cepi.bile.subcommands

import com.cepi.bile.BileTools
import com.cepi.bile.BileUtils
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.CommandSender

fun reloadCommand(sender: CommandSender, args: Array<String>) {
    for (i in 1 until args.size) {
        try {
            val s = BileUtils.getPluginByName(args[i])
            if (s == null) {
                sender.sendMessage(BileTools.tag + "Couldn't find \"" + args[i] + "\".")
                continue
            }
            try {
                val sn = s.name
                BileUtils.reload(s)
                val n = BileUtils.getPluginFile(args[i])
                sender.sendMessage(BileTools.tag + "Reloaded " + ChatColor.WHITE + sn + ChatColor.GRAY + " ("
                        + ChatColor.WHITE + n!!.name + ChatColor.GRAY + ")")
            } catch (e: Throwable) {
                sender.sendMessage(BileTools.tag + "Couldn't reload \"" + args[i] + "\".")
                e.printStackTrace()
            }
        } catch (e: Throwable) {
            sender.sendMessage(BileTools.tag + "Couldn't reload or find \"" + args[i] + "\".")
            e.printStackTrace()
        }
    }
}