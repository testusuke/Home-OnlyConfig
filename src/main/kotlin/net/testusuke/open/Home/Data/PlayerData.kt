package net.testusuke.open.Home.Data

import net.testusuke.open.Home.Main.Companion.plugin
import net.testusuke.open.Home.Main.Companion.prefix
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.io.IOException

class PlayerData (private val player: Player) {
    private var config = YamlConfiguration()
    private lateinit var file:File

    private var locationMap = HashMap<String,Location>()
    private var locationNameList = ArrayList<String>()

    fun loadData(){
        //  get function
        var i = 0;
        for(key in config.getConfigurationSection("").getKeys(false)){
            val worldName = config.getString("${key}.world")
            val x:Double = config.getDouble("${key}.x")
            val y:Double = config.getDouble("${key}.y")
            val z:Double = config.getDouble("${key}.z")
            //  world
            val world: World = Bukkit.getServer().getWorld(worldName) ?: continue
            var location = Location(world, x, y, z)
            //  put
            locationMap[key] = location
            i++
        }
        plugin.logger.info("load homes. amount: $i")
        //  Add LocationNameList
        for (name in locationMap.keys){
            locationNameList.add(name)
        }


    }

    fun loadConfig(){
        try {
            var directory: File = plugin.dataFolder
            if (!directory.exists())directory.mkdir()
            //var dataDirectory = File(directory,"/data/")
            //if(dataDirectory.exists())dataDirectory.mkdir()
            file = File(directory,"${player.uniqueId.toString().replace("-","")}.yml")
            if(!file.exists()){
                file.createNewFile();
                config = YamlConfiguration.loadConfiguration(file)
            }else{
                config = YamlConfiguration.loadConfiguration(file)
            }
            plugin.logger.info("Loaded HomeData. player: ${player.name}")
        }catch (e:IOException){
            e.printStackTrace()
            plugin.logger.warning("コンフィグのロードに失敗しました。")
        }
    }

    private fun saveData(){
        try{
            config.save(file)
        }catch (e:IOException){
            e.printStackTrace()
        }
    }

    private fun setHome(name:String,location: Location){
        config.set("${name}.world",location.world.name)
        config.set("${name}.x", location.x)
        config.set("${name}.y", location.y)
        config.set("${name}.z", location.z)
        plugin.logger.info("${player.name} add home. name: $name")
        //  Data
        saveData()
    }

    private fun removeHome(name:String){
        config.set(name,null)
        locationNameList.remove(name)
        //  Data
        saveData()
    }

    fun getPlayer(): Player{return player}

    fun getLocationMap(): HashMap<String,Location>{ return locationMap }

    fun getLocation(name: String):Location?{
        if(!locationMap.containsKey(name))return null
        return locationMap[name]
    }

    fun addLocation(name:String, location: Location){
        if(locationMap.containsKey(name)){
            return
        }
        locationMap[name] = location
        locationNameList.add(name)
        //  Config
        setHome(name, location)
    }

    fun removeLocation(name:String){
        if(!locationMap.containsKey(name)){
            player.sendMessage("${prefix}§cホームが存在しません")
            return
        }
        locationMap.remove(name)
        //  Config
        removeHome(name)
    }

    fun getLocationNameList(): ArrayList<String>{ return locationNameList }

    fun size():Int{
        return locationMap.size
    }
}
