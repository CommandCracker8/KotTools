package world.cepi.kotlintools

import world.cepi.kotlintools.KotlinTools.Companion.tag
import world.cepi.kotlintools.subcommands.loadCommand
import world.cepi.kotlintools.subcommands.reloadCommand
import world.cepi.kotlintools.subcommands.uninstallCommand
import world.cepi.kotlintools.subcommands.unloadCommand
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.util.StringUtil
import java.util.*

class KotlinCommand : CommandExecutor, TabCompleter {
    val subCommands = listOf(
            "load" to ::loadCommand,
            "unload" to ::unloadCommand,
            "reload" to ::reloadCommand,
            "uninstall" to ::uninstallCommand
    )

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {

        require(sender.hasPermission("bile.use")) {
            sender.sendMessage(tag + "You need bile.use or OP.")
            return true
        }

        if (args.isEmpty()) {
            subCommands.forEach { sender.sendMessage("$tag/bile ${it.first} <plugin>") }
            return true
        }

        subCommands.forEach {
            if (args[0].equals(it.first, ignoreCase = true)) {
                require(args.size > 1) {
                    sender.sendMessage("$tag/bile ${it.first} <PLUGIN>")
                    return true;
                }

                it.second(sender, args)
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
            StringUtil.copyPartialMatches(args[0], subCommands.map { it.first }, finalList)
            finalList.sort()
        }
        return finalList
    }
}