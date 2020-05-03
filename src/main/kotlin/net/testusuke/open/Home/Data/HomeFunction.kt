package net.testusuke.open.Home.Data

import net.testusuke.open.Home.Main
import org.bukkit.entity.Player

class HomeFunction(private val plugin: Main) {

    private var playerDataMap = HashMap<Player, PlayerData>()

    fun loadPlayerData(player: Player) {
        var pd = PlayerData(player)
        pd.loadConfig()
        pd.loadData()
        playerDataMap[player] = pd
    }

    fun removePlayerData(player: Player) {
        playerDataMap.remove(player)
    }

    fun getPlayerData(player: Player): PlayerData? {
        return playerDataMap[player]
    }

    fun getPlayerDataMap(): HashMap<Player, PlayerData>? {
        return playerDataMap
    }

    fun clearData() {
        playerDataMap.clear()
    }

}