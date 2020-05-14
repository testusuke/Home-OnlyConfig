package net.testusuke.open.Home.Data

import net.testusuke.open.Home.Main
import net.testusuke.open.Home.Main.Companion.plugin
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.World
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import java.io.File
import java.io.IOException
import java.util.*

data class HomeData(private val uuid: UUID) {
    val player: Player?
        get() = Bukkit.getServer().getPlayer(uuid)
    val offlinePlayer: OfflinePlayer?
        get() = Bukkit.getServer().getOfflinePlayer(uuid)
    //  Config
    private lateinit var file: File
    private val config:YamlConfiguration by lazy {
        var c = YamlConfiguration()
        try {
            val directory: File = Main.plugin.dataFolder
            if (!directory.exists()) directory.mkdir()
            val dataDirectory = File(directory,"/data/")
            if(!dataDirectory.exists())dataDirectory.mkdir()
            file = File(directory, "${uuid}.yml")
            if (!file.exists()) {
                file.createNewFile()
            }
            c = YamlConfiguration.loadConfiguration(file)
            Main.plugin.logger.info("Loaded HomeData. uuid: $uuid")
        } catch (e: IOException) {
            e.printStackTrace()
            Main.plugin.logger.warning("コンフィグのロードに失敗しました。")
        }
        c
    }

    private val homeList:MutableMap<String,HomeLocation> by lazy{
        config.getConfigurationSection("")?.getKeys(false)?.mapNotNull { key ->
            try {
                val worldName = config.getString("${key}.world")
                        ?: return@mapNotNull null
                val x = config.getDouble("${key}.x")
                val y = config.getDouble("${key}.y")
                val z = config.getDouble("${key}.z")
                val world = Bukkit.getServer().getWorld(worldName)
                        ?: return@mapNotNull null
                val location = Location(world, x, y, z)
                key to HomeLocation(key,location)
            } catch(ex: Exception){
                null
            }
        }?.toMap()?.toMutableMap() ?: mutableMapOf()
    }

    fun get(name:String):Location?{
        return homeList[name]?.getLocation()
    }
    //Task
    private var saveConfigTask:BukkitTask? = null
    fun set(name: String,location: Location){
        if(homeList[name]?.getLocation() == location) return
        homeList[name]?.setLocation(location)
        saveConfigTask?.cancel()
        saveConfigTask = plugin.server.scheduler.runTaskLater(plugin, Runnable {
            save(name,location)
        }, 3 * 60 * 20)
    }

    fun save(name:String,location:Location){
        config.set("${name}.world", location.world.name)
        config.set("${name}.x", location.x)
        config.set("${name}.y", location.y)
        config.set("${name}.z", location.z)
        plugin.logger.info("$uuid add home. name: $name")
        saveFile()
    }
    private fun saveFile() {
        try {
            config.save(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    
    companion object{
        private val playerToHomeData = mutableMapOf<UUID,HomeData>()
        //  OfflinePlayer
        val OfflinePlayer.homeData
            get() = playerToHomeData.getOrPut(uniqueId){ HomeData(uniqueId) }
        fun saveAll(){
            for(homeData in playerToHomeData.values){
                homeData.saveConfigTask?.cancel()
                for(homeLoc in homeData.homeList.values){
                    /*
                    if(!homeLoc.isSaved()){
                        homeData.save(homeLoc.getName(),homeLoc.getLocation())
                    }*/
                }
            }
        }
    }
}

