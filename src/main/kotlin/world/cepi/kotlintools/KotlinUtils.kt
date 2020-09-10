package world.cepi.kotlintools

import com.google.common.io.Files
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.PluginCommand
import org.bukkit.command.SimpleCommandMap
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.plugin.*
import java.io.File
import java.io.IOException
import java.net.URLClassLoader
import java.util.*
import java.util.zip.ZipException
import java.util.zip.ZipFile

object KotlinUtils {
    @Throws(IOException::class)
    fun delete(plugin: Plugin) {
        val file = getPluginFile(plugin)
        unload(plugin)
        file!!.delete()
    }

    @Throws(IOException::class, InvalidConfigurationException::class, InvalidDescriptionException::class)
    fun delete(file: File) {
        if (getPlugin(file) != null) {
            getPlugin(file)?.let { delete(it) }
            return
        }
        file.delete()
    }

    @Throws(IOException::class, UnknownDependencyException::class, InvalidPluginException::class, InvalidDescriptionException::class, InvalidConfigurationException::class)
    fun reload(p: Plugin) {
        val file = getPluginFile(p)
        val x = unload(p)
        for (i in x)
            if (i != null)
                load(i)

        if (file != null)
            load(file)
    }

    private fun stp(s: String) {
        Bukkit.getConsoleSender().sendMessage(
                ChatColor.GREEN.toString() + "[" + ChatColor.DARK_GRAY + "Kotlin" + ChatColor.GREEN + "]: " + ChatColor.GRAY + s)
    }

    @Throws(UnknownDependencyException::class, InvalidPluginException::class, InvalidDescriptionException::class, ZipException::class, IOException::class, InvalidConfigurationException::class)
    fun load(file: File) {
        if (getPlugin(file) != null) {
            return
        }
        stp("Loading " + getPluginName(file) + " " + getPluginVersion(file))
        val f = getPluginDescription(file)
        for (i in f.depend) {
            if (Bukkit.getPluginManager().getPlugin(i) == null) {
                stp(getPluginName(file) + " depends on " + i)
                val fx = getPluginFile(i)
                if (fx != null) {
                    load(fx)
                } else {
                    return
                }
            }
        }
        for (i in f.softDepend) {
            if (Bukkit.getPluginManager().getPlugin(i) == null) {
                val fx = getPluginFile(i)
                if (fx != null) {
                    stp(getPluginName(file) + " soft depends on " + i)
                    load(fx)
                }
            }
        }
        val target = Bukkit.getPluginManager().loadPlugin(file)
        target!!.onLoad()
        Bukkit.getPluginManager().enablePlugin(target)
    }

    // TODO im deeply scared
    fun unload(plugin: Plugin): Set<File?> {
        val file = getPluginFile(plugin)
        stp("Unloading " + plugin.name)
        val dependencies: MutableSet<File?> = HashSet()
        for (i in Bukkit.getPluginManager().plugins) {
            if (i == plugin)
                continue

            if (i.description.softDepend.contains(plugin.name)) {
                stp(i.name + " soft depends on " + plugin.name + ". Playing it safe.")
                dependencies.add(getPluginFile(i))
            }

            if (i.description.depend.contains(plugin.name)) {
                stp(i.name + " depends on " + plugin.name + ". Playing it safe.")
                dependencies.add(getPluginFile(i))
            }
        }

        if (plugin.name == "WorldEdit") {
            val fa = Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit")
            if (fa != null) {
                stp(fa.name + " (kind of) depends on " + plugin.name + ". Playing it safe.")
                dependencies.add(getPluginFile(fa))
            }
        }
        for (i in HashSet(dependencies)) {
            if (i != null)
                getPlugin(i)?.let { unload(it) }?.let { dependencies.addAll(it) }
        }
        Bukkit.getScheduler().cancelTasks(plugin)
        HandlerList.unregisterAll(plugin)
        val name = plugin.name
        val pluginManager = Bukkit.getPluginManager()
        val commandMap: SimpleCommandMap?
        val plugins: MutableList<Plugin?>?
        val names: MutableMap<String?, Plugin?>?
        val commands: MutableMap<String?, Command>?
        var listeners: Map<Event?, SortedSet<RegisteredListener>>? = null
        var reloadListeners = true
        pluginManager.disablePlugin(plugin)
        try {
            val pluginsField = Bukkit.getPluginManager().javaClass.getDeclaredField("plugins")
            val lookupNamesField = Bukkit.getPluginManager().javaClass.getDeclaredField("lookupNames")
            pluginsField.isAccessible = true
            plugins = pluginsField[pluginManager] as MutableList<Plugin?>
            lookupNamesField.isAccessible = true
            names = lookupNamesField[pluginManager] as MutableMap<String?, Plugin?>
            try {
                val listenersField = Bukkit.getPluginManager().javaClass.getDeclaredField("listeners")
                listenersField.isAccessible = true
                listeners = listenersField[pluginManager] as Map<Event?, SortedSet<RegisteredListener>>
            } catch (e: Exception) {
                reloadListeners = false
            }
            val commandMapField = Bukkit.getPluginManager().javaClass.getDeclaredField("commandMap")
            val knownCommandsField = SimpleCommandMap::class.java.getDeclaredField("knownCommands")
            commandMapField.isAccessible = true
            commandMap = commandMapField[pluginManager] as SimpleCommandMap
            knownCommandsField.isAccessible = true
            commands = knownCommandsField[commandMap] as MutableMap<String?, Command>
        } catch (e: Throwable) {
            e.printStackTrace()
            return HashSet()
        }
        pluginManager.disablePlugin(plugin)

        if (plugins.contains(plugin))
            plugins.remove(plugin)

        if (names.containsKey(name))
            names.remove(name)

        if (listeners != null && reloadListeners) {
            for (set in listeners.values) {
                val it = set.iterator()
                while (it.hasNext()) {
                    val value = it.next()
                    if (value.plugin === plugin) {
                        it.remove()
                    }
                }
            }
        }
        val it: MutableIterator<Map.Entry<String?, Command>> = commands.entries.iterator()
        while (it.hasNext()) {
            val entry = it.next()
            if (entry.value is PluginCommand) {
                val c = entry.value as PluginCommand
                if (c.plugin === plugin) {
                    c.unregister(commandMap)
                    it.remove()
                }
            }
        }
        val classLoader = plugin.javaClass.classLoader
        if (classLoader is URLClassLoader) {
            try {
                classLoader.close()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
        val randomUUID = UUID.randomUUID().toString()
        val ff = File(File(KotlinTools.kotlin!!.dataFolder, "temp"), randomUUID)
        System.gc()
        try {
            if (file != null)
                copy(file, ff)
            file!!.delete()
            copy(ff, file)
            KotlinTools.kotlin!!.reset(file)
            ff.deleteOnExit()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return dependencies
    }

    @Throws(IOException::class)
    fun copy(a: File, b: File) {
        b.parentFile.mkdirs()
        Files.copy(a, b)
    }

    private fun getPlugin(file: File): Plugin? {
        for (i in Bukkit.getPluginManager().plugins) {
            try {
                if (getPluginFile(i) == file) {
                    return i
                }
            } catch (e: Throwable) {
            }
        }
        return null
    }

    fun getPluginFile(plugin: Plugin): File? {
        for (i in pluginsFolder.listFiles()) {
            if (isPluginJar(i)) {
                try {
                    if (plugin.name == getPluginName(i)) {
                        return i
                    }
                } catch (e: Throwable) {
                }
            }
        }
        return null
    }

    fun getPluginFile(name: String): File? {
        for (plugin in pluginsFolder.listFiles()) {
            if (isPluginJar(plugin) && plugin.isFile && plugin.name.toLowerCase() == name.toLowerCase()) {
                return plugin
            }
        }

        for (plugin in pluginsFolder.listFiles()) {
            try {
                if (isPluginJar(plugin) && plugin.isFile && getPluginName(plugin).toLowerCase() == name.toLowerCase()) {
                    return plugin
                }
            } catch (e: Throwable) {
            }
        }
        return null
    }

    private fun isPluginJar(f: File): Boolean = f.exists() && f.isFile && f.name.toLowerCase().endsWith(".jar")

    private val pluginsFolder: File = KotlinTools.folder

    @Throws(ZipException::class, IOException::class, InvalidConfigurationException::class, InvalidDescriptionException::class)
    fun getPluginVersion(file: File) = getPluginDescription(file).version

    @Throws(ZipException::class, IOException::class, InvalidConfigurationException::class, InvalidDescriptionException::class)
    fun getPluginName(file: File) = getPluginDescription(file).name

    @Throws(ZipException::class, IOException::class, InvalidConfigurationException::class, InvalidDescriptionException::class)
    private fun getPluginDescription(file: File): PluginDescriptionFile {
        val z = ZipFile(file)
        val `is` = z.getInputStream(z.getEntry("plugin.yml"))
        val f = PluginDescriptionFile(`is`)
        z.close()
        return f
    }

    fun getPluginByName(string: String) = Bukkit.getPluginManager().plugins.find {it.name.toLowerCase().contains(string.toLowerCase()) }
}