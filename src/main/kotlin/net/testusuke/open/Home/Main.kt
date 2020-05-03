package net.testusuke.open.Home

import net.testusuke.open.Home.Command.HomeCommand
import net.testusuke.open.Home.Command.HomeSetCommand
import net.testusuke.open.Home.Data.HomeAdminFunction
import net.testusuke.open.Home.Data.HomeFunction
import net.testusuke.open.Home.Event.EventListener
import net.testusuke.open.Home.Util.VaultManager
import org.bukkit.plugin.java.JavaPlugin

class Main: JavaPlugin() {
    companion object{
        lateinit var plugin: JavaPlugin
        var prefix: String = "§e[§dHome]§f"
        var mode: Boolean = false
        var version = "1.0"
        var author = "testusuke"
        var pluginName = "Home"
        //  Class
        lateinit var homeFunction: HomeFunction
        lateinit var homeAdminFunction: HomeAdminFunction
    }

    var homeCreateVault:Int = 0
    var homeTPVault:Int = 0

    override fun onEnable() {
        plugin = this
        //  Logger
        logger.info("==============================")
        logger.info("Plugin: $pluginName")
        logger.info("Ver: $version Author: $author")
        logger.info("==============================")
        //  Event
        server.pluginManager.registerEvents(EventListener,this)
        //  Command
        getCommand("home").executor = HomeCommand(this)
        getCommand("sethome").executor = HomeSetCommand(this)
        this.saveDefaultConfig()
        loadConfig()
        //  Class
        homeFunction = HomeFunction(this)
        homeAdminFunction = HomeAdminFunction(this)
        VaultManager(this)
    }

    override fun onDisable() {
        //  Clear Data
        clearData()
        //  Status
        saveStatus()
    }

    fun loadConfig(){
        mode = config.getBoolean("mode")
        try {
            prefix = config.getString("prefix").replace("&", "§")
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        try{
            homeCreateVault = this.config.getInt("money.create")
            homeTPVault = this.config.getInt("money.tp")
        }catch (e:NullPointerException){
            logger.info("値の取得に失敗")
            e.printStackTrace()
        }
    }

    fun saveStatus(){
        config.set("mode",mode)
        this.saveConfig()
    }

    private fun clearData(){
        homeFunction.clearData()
        homeAdminFunction.clearData()
    }
}