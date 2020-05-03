package net.testusuke.open.Home.Data

import net.testusuke.open.Home.Main
import net.testusuke.open.Home.Main.Companion.plugin
import org.apache.commons.io.IOUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.configuration.file.YamlConfiguration
import org.json.simple.JSONObject
import org.json.simple.JSONValue
import java.io.File
import java.io.IOException;
import java.lang.Exception
import java.net.URL;


class OfflinePlayerData(private val name:String) {

    private var config = YamlConfiguration()
    private lateinit var file: File

    private var locationMap = HashMap<String, Location>()
    private var locationNameList = ArrayList<String>()
    private var uuid:String = ""

    fun loadOfflineData(): Boolean {
        //  Get UUID from WEB API
        var uuid = getUUIDWithAPI(name)
        if(uuid == "")return false
        plugin.logger.info("Successfully get uuid with web api. name: $name uuid: $uuid")
        //  LoadConfig
        var b = loadConfig()
        if(!b)return false
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
        plugin.logger.info("load homes. name: $name amount: $i")
        //  Add LocationNameList
        for (name in locationMap.keys){
            locationNameList.add(name)
        }
        return true
    }

    private fun getUUIDWithAPI(name: String):String{
        try {
            var url = "https://api.mojang.com/users/profiles/minecraft/$name"
            var uuidJson = IOUtils.toString(URL(url))
            if(uuidJson.isEmpty())return ""
            var jsonObject:JSONObject = JSONValue.parseWithException(uuidJson) as JSONObject
            return jsonObject["id"].toString()
        }catch (e: Exception){
            return ""
        }
    }

    private fun loadConfig(): Boolean{
        try {
            var directory: File = Main.plugin.dataFolder
            if (!directory.exists())directory.mkdir()
            //var dataDirectory = File(directory,"data")
            //if(dataDirectory.exists())dataDirectory.mkdir()
            file = File(directory,"${uuid}.yml")
            if(!file.exists()){
                return false
            }else{
                config = YamlConfiguration.loadConfiguration(file)
            }
            plugin.logger.info("Loaded OfflinePlayerData. player: $name")
            return true
        }catch (e: IOException){
            e.printStackTrace()
            plugin.logger.warning("コンフィグのロードに失敗しました。")
            return false
        }
    }

    private fun saveData(){
        try{
            config.save(file)
        }catch (e: IOException){
            e.printStackTrace()
        }
    }

    private fun removeHome(name:String){
        config.set(name,null)
        locationNameList.remove(name)
        //  Data
        saveData()
    }

    fun getName(): String {return name}
    fun getUUID(): String {return uuid}

    fun getLocationMap(): HashMap<String, Location>{ return locationMap }

    fun removeLocation(name:String){
        locationMap.remove(name)
        //  Config
        removeHome(name)
    }

    fun getLocationNameList(): ArrayList<String>{ return locationNameList }

    fun size():Int{
        return locationMap.size
    }

    fun getLocation(name:String):Location?{
        return locationMap[name]
    }
}