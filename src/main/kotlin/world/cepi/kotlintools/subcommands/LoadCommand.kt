package world.cepi.kotlintools.subcommands

import world.cepi.kotlintools.KotlinTools.Companion.tag
import world.cepi.kotlintools.KotlinUtils
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.CommandSender

fun loadCommand(sender: CommandSender, args: Array<String>) {
    for (i in 1 until args.size) {
        try {
            val s = KotlinUtils.getPluginFile(args[i])
            if (s == null) {
                sender.sendMessage(tag + "Couldn't find \"" + args[i] + "\".")
                continue
            }
            try {
                KotlinUtils.load(s)
                val n = KotlinUtils.getPluginByName(args[i])!!.name
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