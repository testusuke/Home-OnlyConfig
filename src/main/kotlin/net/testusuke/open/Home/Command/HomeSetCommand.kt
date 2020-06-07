package net.testusuke.open.Home.Command

import net.testusuke.open.Home.Main
import net.testusuke.open.Home.Main.Companion.homeFunction
import net.testusuke.open.Home.Main.Companion.mode
import net.testusuke.open.Home.Main.Companion.prefix
import net.testusuke.open.Home.Data.PlayerData
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.regex.Pattern

class HomeSetCommand(private var plugin: Main) : CommandExecutor {
    private var permission = "home.general"

    override fun onCommand(sender: CommandSender?, command: Command?, label: String?, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender!!.sendMessage("${prefix}You can't use console")
            return false
        }
        var player: Player = sender
        if (!sender.hasPermission(permission)) {
            player.sendMessage("${prefix}§cあなたには権限がありません")
            return false
        }
        if (!mode) {
            sendDisable(player)
            return false
        }

        if (args == null || args.isEmpty()) {
            var pd: PlayerData? = homeFunction.getPlayerData(player)
            if (pd == null) {
                homeFunction.loadPlayerData(player)
                pd = homeFunction.getPlayerData(player)
            }
            if(!player.inventory.itemInMainHand.isSimilar(plugin.registerItemStack)){
                player.sendMessage("${prefix}§c登録用アイテムを持っていません。")
                return false
            }
            player.inventory.itemInMainHand.amount--
            var location = player.location
            pd?.addLocation("default", location)
            player.sendMessage("${prefix}§aホームを設定します。")
            return true
        }

        var arg = args[0]
        if (args.size == 1) {
            var pd: PlayerData? = homeFunction.getPlayerData(player)
            if (pd == null) {
                homeFunction.loadPlayerData(player)
                pd = homeFunction.getPlayerData(player)
            }
            //  HomeName 英数字のみ
            if (!checkString(arg)) {
                player.sendMessage("${prefix}§c半角英数字のみを使用してください。")
                return false
            }
            if(!player.inventory.itemInMainHand.isSimilar(plugin.registerItemStack)){
                player.sendMessage("${prefix}§c登録用アイテムを持っていません。")
                return false
            }
            player.inventory.itemInMainHand.amount--
            var location = player.location
            pd?.addLocation(arg, location)
            player.sendMessage("${prefix}§aホームを設定します。")
            return true
        }

        player.sendMessage("${prefix}§c使用方法が誤っています。/home help")
        return false
    }

    private fun sendDisable(player: Player) {
        player.sendMessage("${prefix}§c現在利用できません。")
    }

    private fun checkString(str: String): Boolean {
        return Pattern.matches("^[0-9a-zA-Z]+$", str)
    }
}