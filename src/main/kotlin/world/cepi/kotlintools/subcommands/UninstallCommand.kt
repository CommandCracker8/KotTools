package world.cepi.kotlintools.subcommands

import world.cepi.kotlintools.KotlinTools
import world.cepi.kotlintools.KotlinUtils
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.CommandSender

fun uninstallCommand(sender: CommandSender, args: Array<String>) {
    for (i in 1 until args.size) {
        try {
            val s = KotlinUtils.getPluginFile(args[i])
            if (s == null) {
                sender.sendMessage(KotlinTools.tag + "Couldn't find \"" + args[i] + "\".")
                continue
            }
            try {
                val n = KotlinUtils.getPluginName(s)
                KotlinUtils.delete(s)
                sender.sendMessage(KotlinTools.tag + "Uninstalled " + ChatColor.WHITE + n + ChatColor.GRAY
                        + " from " + ChatColor.WHITE + s.name)
                if (s.exists()) {
                    sender.sendMessage(
                            KotlinTools.tag + "But it looks like we can't delete it. You may need to delete "
                                    + ChatColor.RED + s.name + ChatColor.GRAY
                                    + " before installing it again.")
                }
            } catch (e: Throwable) {
                sender.sendMessage(KotlinTools.tag + "Couldn't uninstall \"" + args[i] + "\".")
                e.printStackTrace()
            }
        } catch (e: Throwable) {
            sender.sendMessage(KotlinTools.tag + "Couldn't uninstall or find \"" + args[i] + "\".")
            e.printStackTrace()
        }
    }
}