package com.cepi.bile.subcommands

import com.cepi.bile.BileTools.Companion.tag
import com.cepi.bile.BileUtils
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.CommandSender

fun loadCommand(sender: CommandSender, args: Array<String>) {
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
}