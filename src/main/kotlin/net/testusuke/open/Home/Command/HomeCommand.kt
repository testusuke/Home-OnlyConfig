package net.testusuke.open.Home.Command

import net.testusuke.open.Home.Data.OfflinePlayerData
import net.testusuke.open.Home.Main
import net.testusuke.open.Home.Main.Companion.homeFunction
import net.testusuke.open.Home.Main.Companion.mode
import net.testusuke.open.Home.Main.Companion.prefix
import net.testusuke.open.Home.Main.Companion.version
import net.testusuke.open.Home.Data.PlayerData
import net.testusuke.open.Home.Main.Companion.homeAdminFunction
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class HomeCommand(private val plugin: Main) : CommandExecutor {

    private var permission = "home.general"
    private var adminPermission = "home.admin"

    override fun onCommand(sender: CommandSender?, command: Command?, label: String?, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender!!.sendMessage("you can't use console!")
            return false
        }

        var player: Player = sender
        if (!sender.hasPermission(permission)) {
            player.sendMessage("${prefix}§cあなたには権限がありません")
            return false
        }

        if (args == null || args.isEmpty()) {
            teleportHome(player, "default")
            return true
        }
        var arg = args[0]
        when (arg) {
            "help" -> {
                sendHelp(player)
            }

            "list" -> {
                if (!mode) {
                    sendDisable(player)
                    return false
                }
                if (args.size == 1) {
                    sendHomeList(player)
                    return true
                }
            }
            "remove" -> {
                if (!mode) {
                    sendDisable(player)
                    return false
                }
                //  General
                if (args.size < 2) {
                    player.sendMessage("${prefix}§c使用方法が誤っています。")
                    return false
                }
                var arg1 = args[1]
                var pd: PlayerData? = homeFunction.getPlayerData(player)
                if (pd == null) {
                    homeFunction.loadPlayerData(player)
                    pd = homeFunction.getPlayerData(player)
                    if (pd == null) {
                        player.sendMessage("${prefix}§cエラーです。")
                        return false
                    }
                }
                var list: ArrayList<String> = pd.getLocationNameList()
                return if (list.contains(arg1)) {
                    pd.removeLocation(arg1)
                    player.sendMessage("${prefix}§6ホームを削除します。")
                    true
                } else {
                    player.sendMessage("${prefix}§cホームが存在しません。")
                    false
                }
            }
            "reload" -> {
                if (!hasAdmin(player)) {
                    sendNotHasPex(player)
                    return false
                }
                plugin.saveConfig()
                plugin.reloadConfig()
                plugin.saveStatus()
                plugin.loadConfig()
                //  Player
                player.sendMessage("${prefix}§aコンフィグを再読み込みしました。")
            }
            "on" -> {
                if (!hasAdmin(player)) {
                    sendNotHasPex(player)
                    return false
                }
                if (mode) {
                    player.sendMessage("${prefix}§cすでに有効です。")
                    return true
                }
                mode = true
                player.sendMessage("${prefix}§a有効になりました。")
                return true
            }
            "off" -> {
                if (!hasAdmin(player)) {
                    sendNotHasPex(player)
                    return false
                }
                if (!mode) {
                    player.sendMessage("${prefix}§cすでに無効です。")
                    return true
                }
                mode = false
                player.sendMessage("${prefix}§a無効になりました。")
                return true
            }

            "get" -> {
                if (!hasAdmin(player)) {
                    sendNotHasPex(player)
                    return false
                }
                player.sendMessage("${prefix}§a登録用アイテムを付与します")
                player.inventory.addItem(plugin.registerItemStack)

            }

            else -> {
                teleportHome(player, arg)
                return true
            }

        }
        return false
    }

    private fun sendHelp(player: Player) {
        player.sendMessage("§e==============================")
        player.sendMessage("§6/home [name] <- ホームに移動します。")
        player.sendMessage("§6/sethome [name] <- ホームを設定します。§a半角英数字のみ使用できます。")
        player.sendMessage("§6/home help <- ヘルプを表示します。")
        player.sendMessage("§6/home list <- ホームのリストを表示します。")
        if (player.hasPermission(adminPermission)) {
            player.sendMessage("§c/home reload <- 設定用コンフィグを再読み込みします。")
            player.sendMessage("§c/home on <- プラグインを有効にします。")
            player.sendMessage("§c/home off <- プラグインを無効にします。")
            player.sendMessage("§c§l※サーバーがインターネットに接続されていないと使用できません。以下の処理は多少時間がかかる可能性があります。")
            player.sendMessage("§c/home load <id> <- 指定されたプレイヤーのホームデータをロードします。")
            player.sendMessage("§c/home list <id> <- 指定されたプレイヤーのホームリストを表示します。")
            player.sendMessage("§c/home tp <id> <name> <- 指定されたプレイヤーのホームに移動します。")
            player.sendMessage("§c/home remove <id> <name> <- 指定されたプレイヤーのホームを削除します。")
            //player.sendMessage("§c/home clear <id> <- 指定されたプレイヤーのホームデータを削除します。(復元できません。)")
        }
        player.sendMessage("§d§lCreated by testusuke Version: $version")
        player.sendMessage("§e==============================")
    }

    private fun sendHomeList(player: Player) {
        var pd: PlayerData? = homeFunction.getPlayerData(player)
        if (pd == null) {
            homeFunction.loadPlayerData(player)
            pd = homeFunction.getPlayerData(player)
        }
        var list: ArrayList<String>? = pd?.getLocationNameList()
        if (list == null || list.isEmpty()) {
            player.sendMessage("${prefix}§cホームが存在しません。")
            return
        }
        player.sendMessage("${prefix}§aホームリストを表示します。")
        for (name in list) {
            player.sendMessage("- $name")
        }
    }

    private fun sendDisable(player: Player) {
        player.sendMessage("${prefix}§c現在利用できません。")
    }

    private fun sendNotHasPex(player: Player) {
        player.sendMessage("${prefix}§cあなたには権限がありません。")
    }

    private fun hasAdmin(player: Player): Boolean {
        return player.hasPermission(adminPermission)
    }

    private fun teleportHome(player: Player, name: String) {
        if (!mode) {
            sendDisable(player)
            return
        }
        var pd: PlayerData? = homeFunction.getPlayerData(player)
        if (pd == null) {
            homeFunction.loadPlayerData(player)
            pd = homeFunction.getPlayerData(player)
        }
        var location: Location? = pd?.getLocation(name)
        if (location == null) {
            player.sendMessage("${prefix}§cホームが存在しません。/home help")
            return
        }
        player.teleport(location)
        player.sendMessage("${prefix}§aテレポートします。")
        return
    }
}
