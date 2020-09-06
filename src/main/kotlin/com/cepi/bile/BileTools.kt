package com.cepi.bile

import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*
import java.util.logging.Level

class BileTools : JavaPlugin() {
    private val folder = File("plugins")
    private val successSound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP
    override fun onEnable() {
        config.options().copyDefaults(true)
        saveConfig()
        bile = this
        getCommand("bile")!!.setExecutor(BileCommand())
        getCommand("bile")!!.tabCompleter = BileCommand()
        if (config.getBoolean("doAutoUpdate")) server.scheduler.scheduleSyncRepeatingTask(this, { onTick() }, 20, 20)
    }

    fun reset(f: File?) {
        modification[f] = f!!.length()
        las[f] = f.lastModified()
    }

    private fun onTick() {
        for (i in folder.listFiles()) {
            if (i.isFile && i.name.toLowerCase().endsWith(".jar")) {
                if (!modification.containsKey(i)) {
                    logger.log(Level.INFO, "Now Tracking: " + i.name)
                    modification[i] = i.length()
                    las[i] = i.lastModified()
                    try {
                        BileUtils.load(i)
                        for (player in Bukkit.getOnlinePlayers()) {
                            if (player.hasPermission("bile.use")) {
                                player.sendMessage(tag + "Hot Dropped " + ChatColor.WHITE + i.name)
                                player.playSound(player.location, successSound, 1f, 1.9f)
                            }
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        for (k in Bukkit.getOnlinePlayers()) {
                            if (k.hasPermission("bile.use")) {
                                k.sendMessage(tag + "Failed to hot drop " + ChatColor.RED + i.name)
                            }
                        }
                    }
                }
                if (modification[i] != i.length() || las[i] != i.lastModified()) {
                    modification[i] = i.length()
                    las[i] = i.lastModified()
                    for (j in Bukkit.getServer().pluginManager.plugins) {
                        if (BileUtils.getPluginFile(j) != null
                                && BileUtils.getPluginFile(j)!!.name == i.name) {
                            logger.log(Level.INFO, "Plugin Reloading: " + j.name + " <-> " + i.name)
                            try {
                                BileUtils.reload(j)
                                for (k in Bukkit.getOnlinePlayers()) {
                                    k.sendMessage(tag + "Reloaded " + ChatColor.WHITE + j.name)
                                    k.playSound(k.location, successSound, 1f, 1.9f)
                                }
                            } catch (e: Throwable) {
                                for (k in Bukkit.getOnlinePlayers()) {
                                    if (k.hasPermission("bile.use")) {
                                        k.sendMessage(tag + "Failed to Reload " + ChatColor.RED + j.name)
                                    }
                                }
                                e.printStackTrace()
                            }
                            break
                        }
                    }
                }
            }
        }
    }

    companion object {
        val tag = (ChatColor.GREEN.toString() + "[" + ChatColor.DARK_GRAY + "Bile" + ChatColor.GREEN + "]: "
                + ChatColor.GRAY)
        private val modification = HashMap<File?, Long>()
        private val las = HashMap<File?, Long>()
        var bile: BileTools? = null
    }
}