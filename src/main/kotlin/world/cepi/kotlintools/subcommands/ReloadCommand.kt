package world.cepi.kotlintools.subcommands

import world.cepi.kotlintools.KotlinTools
import world.cepi.kotlintools.KotlinUtils
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.CommandSender

fun reloadCommand(sender: CommandSender, args: Array<String>) {
    for (i in 1 until args.size) {
        try {
            val s = KotlinUtils.getPluginByName(args[i])
            if (s == null) {
                sender.sendMessage(KotlinTools.tag + "Couldn't find \"" + args[i] + "\".")
                continue
            }
            try {
                val sn = s.name
                KotlinUtils.reload(s)
                val n = KotlinUtils.getPluginFile(args[i])
                sender.sendMessage(KotlinTools.tag + "Reloaded " + ChatColor.WHITE + sn + ChatColor.GRAY + " ("
                        + ChatColor.WHITE + n!!.name + ChatColor.GRAY + ")")
            } catch (e: Throwable) {
                sender.sendMessage(KotlinTools.tag + "Couldn't reload \"" + args[i] + "\".")
                e.printStackTrace()
            }
        } catch (e: Throwable) {
            sender.sendMessage(KotlinTools.tag + "Couldn't reload or find \"" + args[i] + "\".")
            e.printStackTrace()
        }
    }
}