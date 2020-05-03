package net.testusuke.open.Home.Data

import net.testusuke.open.Home.Main
import net.testusuke.open.Home.Main.Companion.prefix
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class HomeAdminFunction(private var plugin: Main) {

    private var map = HashMap<String, OfflinePlayerData>()
    private var uuidMap = HashMap<String,String>()

    fun loadPlayerData(name: String,admin: Player){
        object:BukkitRunnable(){
            override fun run(){
                var offlinePlayerData = OfflinePlayerData(name)
                var b = offlinePlayerData.loadOfflineData()
                if(!b){
                    admin.sendMessage("${prefix}§cデータを読み込めませんでした。")
                    cancel()
                    return
                }else{
                    map[name] = offlinePlayerData
                    addUUID(name,offlinePlayerData)
                    admin.sendMessage("${prefix}§aデータを読み込みました。")
                }
            }
        }.runTaskAsynchronously(plugin)
    }

    private fun addUUID(name: String,offlinePlayerData: OfflinePlayerData){
        uuidMap[name] = offlinePlayerData.getUUID()
    }

    fun getOfflinePlayerData(name:String): OfflinePlayerData? {
        if(!map.containsKey(name))return null
        return map[name]
    }

    fun setOfflinePlayerData(name:String,data: OfflinePlayerData){
        map[name] = data
    }

    fun removeOfflinePlayerData(name: String){
        map.remove(name)
    }

    fun clearData(){
        map.clear()
    }

}