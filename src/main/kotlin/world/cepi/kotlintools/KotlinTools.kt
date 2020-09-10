package world.cepi.kotlintools

import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*
import java.util.logging.Level

class KotlinTools : JavaPlugin() {
    private val successSound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP

    override fun onEnable() {
        config.options().copyDefaults(true)
        saveConfig()
        kotlin = this
        getCommand("bile")!!.setExecutor(KotlinCommand())
        getCommand("bile")!!.tabCompleter = KotlinCommand()

        if (config.getBoolean("doAutoUpdate"))
            server.scheduler.scheduleSyncRepeatingTask(this, { onTick() }, 20, 20)
    }

    fun reset(f: File?) {
        modification[f] = f!!.length()
        lastModified[f] = f.lastModified()
    }

    private fun onTick() {

        val files = folder.listFiles()

        require(files != null) // If this throws an error, I don't know what witchery the user committed, but ok.

        for (i in files) {
            if (i.isFile && i.name.toLowerCase().endsWith(".jar")) {
                if (!modification.containsKey(i)) {
                    logger.log(Level.INFO, "Now Tracking: " + i.name)
                    modification[i] = i.length()
                    lastModified[i] = i.lastModified()
                    try {
                        KotlinUtils.load(i)
                        for (player in Bukkit.getOnlinePlayers()) {
                            if (player.hasPermission("bile.use")) {
                                player.sendMessage(tag + "Hot Dropped " + ChatColor.WHITE + i.name)
                                player.playSound(player.location, successSound, 1f, 1.9f)
                            }
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        Bukkit.getOnlinePlayers()
                                .filter { it.hasPermission("bile.use") }
                                .forEach { it.sendMessage(tag + "Failed to hot drop " + ChatColor.RED + i.name) }
                    }
                }
                if (modification[i] != i.length() || lastModified[i] != i.lastModified()) {
                    modification[i] = i.length()
                    lastModified[i] = i.lastModified()
                    for (j in Bukkit.getServer().pluginManager.plugins) {
                        if (KotlinUtils.getPluginFile(j) != null
                                && KotlinUtils.getPluginFile(j)!!.name == i.name) {
                            logger.info("Plugin Reloading: " + j.name + " <-> " + i.name)
                            try {
                                KotlinUtils.reload(j)
                                Bukkit.getOnlinePlayers()
                                        .filter { it.hasPermission("bile.use") }
                                        .forEach {
                                    it.sendMessage(tag + "Reloaded " + ChatColor.WHITE + j.name)
                                    it.playSound(it.location, successSound, 1f, 1.9f)
                                }
                            } catch (e: Throwable) {
                                Bukkit.getOnlinePlayers()
                                        .filter { it.hasPermission("bile.use") }
                                        .forEach { it.sendMessage(tag + "Failed to Reload " + ChatColor.RED + j.name) }
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
        private val lastModified = HashMap<File?, Long>()
        val folder = File("plugins")
        var kotlin: KotlinTools? = null
    }
}