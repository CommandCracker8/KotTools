package com.cepi.bile.subcommands

import com.cepi.bile.BileTools
import com.cepi.bile.BileUtils
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.CommandSender

fun unloadCommand(sender: CommandSender, args: Array<String>) {
    for (i in 1 until args.size) {
        try {
            val s = BileUtils.getPluginByName(args[i])
            if (s == null) {
                sender.sendMessage(BileTools.tag + "Couldn't find \"" + args[i] + "\".")
                continue
            }
            val sn = s.name
            BileUtils.unload(s)
            val n = BileUtils.getPluginFile(args[i])
            sender.sendMessage(BileTools.tag + "Unloaded " + ChatColor.WHITE + sn + ChatColor.GRAY + " ("
                    + ChatColor.WHITE + n!!.name + ChatColor.GRAY + ")")
        } catch (e: Throwable) {
            sender.sendMessage(BileTools.tag + "Couldn't unload \"" + args[i] + "\".")
            e.printStackTrace()
        }
    }
}