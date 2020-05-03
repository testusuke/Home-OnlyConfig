package net.testusuke.open.Home.Event

import net.testusuke.open.Home.Main.Companion.homeFunction
import net.testusuke.open.Home.Main.Companion.mode
import net.testusuke.open.Home.Main.Companion.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object EventListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        var player = event.player
        if (!mode) return
        homeFunction.loadPlayerData(player)
        var amount = homeFunction.getPlayerData(player)?.size()
        player.sendMessage("§aあなたのホームデータを読み込みました。ホーム数: $amount")
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        var player = event.player
        homeFunction.removePlayerData(player)
    }

}