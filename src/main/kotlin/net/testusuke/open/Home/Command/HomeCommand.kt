package net.testusuke.open.Home.Command

import net.testusuke.open.Home.Data.HomeAdminFunction
import net.testusuke.open.Home.Data.OfflinePlayerData
import net.testusuke.open.Home.Main
import net.testusuke.open.Home.Main.Companion.homeFunction
import net.testusuke.open.Home.Main.Companion.mode
import net.testusuke.open.Home.Main.Companion.prefix
import net.testusuke.open.Home.Main.Companion.version
import net.testusuke.open.Home.Data.PlayerData
import net.testusuke.open.Home.Main.Companion.homeAdminFunction
import net.testusuke.open.Home.Util.VaultManager
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

                //  Pex
                if (!hasAdmin(player)) {
                    return false
                }
                var name = args[1]
                var opd: OfflinePlayerData? = homeAdminFunction.getOfflinePlayerData(name)
                if (opd == null) {
                    player.sendMessage("${prefix}§6データがロードされていないので、データをロードします")
                    opd = homeAdminFunction.getOfflinePlayerData(name)
                    if (opd == null) {
                        player.sendMessage("${prefix}§cエラーです。")
                        return false
                    }
                }
                var list = opd.getLocationNameList()
                player.sendMessage("${prefix}§a${name}のホームリストを表示します。")
                for (home in list) {
                    player.sendMessage("- $home")
                }
            }
            "remove" -> {
                if (!mode) {
                    sendDisable(player)
                    return false
                }

                //  Admin
                if (args.size >= 3) {
                    //  Pex
                    if (!hasAdmin(player)) {
                        sendNotHasPex(player)
                        return false
                    }
                    var name = args[1]
                    var home = args[2]
                    var opd: OfflinePlayerData? = homeAdminFunction.getOfflinePlayerData(name)
                    if (opd == null) {
                        player.sendMessage("${prefix}§6データがロードされていないので、データをロードします")
                        opd = homeAdminFunction.getOfflinePlayerData(name)
                        if (opd == null) {
                            player.sendMessage("${prefix}§cエラーです。")
                            return false
                        }
                    }
                    var list = opd.getLocationNameList()
                    if (list.contains(home)) {
                        opd.removeLocation(home)
                        player.sendMessage("${prefix}§6ホームを削除します。")
                        return true
                    } else {
                        player.sendMessage("${prefix}§cホームが存在しません。")
                    }
                    return true
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
                if (list.contains(arg1)) {
                    pd.removeLocation(arg1)
                    player.sendMessage("${prefix}§6ホームを削除します。")
                    return true
                } else {
                    player.sendMessage("${prefix}§cホームが存在しません。")
                    return false
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

            "load" -> {
                if (!mode) {
                    sendDisable(player)
                    return false
                }
                if (!hasAdmin(player)) {
                    sendNotHasPex(player)
                    return false
                }
                if (args.size != 2) {
                    player.sendMessage("${prefix}§c使用方法が誤っています。/home help")
                    return false
                }
                var arg1 = args[1]
                var opd: OfflinePlayerData? = homeAdminFunction.getOfflinePlayerData(arg1)
                if (opd == null) {
                    homeAdminFunction.loadPlayerData(arg1, player)
                } else {
                    homeAdminFunction.removeOfflinePlayerData(arg1)
                    homeAdminFunction.loadPlayerData(arg1, player)
                }
            }

            "tp" -> {
                if (!mode) {
                    sendDisable(player)
                    return false
                }
                if (!hasAdmin(player)) {
                    sendNotHasPex(player)
                    return false
                }
                if (args.size != 3) {
                    player.sendMessage("${prefix}§c使用方法が誤っています。/home help")
                    return false
                }
                var name = args[1]
                var home = args[2]
                var opd: OfflinePlayerData? = homeAdminFunction.getOfflinePlayerData(name)
                if (opd == null) {
                    player.sendMessage("${prefix}§6データがロードされていないので、データをロードします")
                    opd = homeAdminFunction.getOfflinePlayerData(name)
                    if (opd == null) {
                        player.sendMessage("${prefix}§cエラーです。")
                        return false
                    }
                }
                if (!opd.getLocationNameList().contains(home)) {
                    player.sendMessage("${prefix}§cホームが存在しません。/home list $name")
                    return false
                }
                var location: Location? = opd.getLocation(home)
                player.teleport(location)
                player.sendMessage("${prefix}§aテレポートします。")
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
        //  Vault
        var vault = VaultManager.economy.getBalance(player).toInt()
        if (plugin.homeTPVault != 0) {
            if (plugin.homeTPVault > vault) {
                player.sendMessage("${prefix}§cお金が不足しています。必要な金額: ${plugin.homeTPVault}")
                return
            }
            VaultManager.economy.withdrawPlayer(player, plugin.homeTPVault.toDouble())
        }
        player.teleport(location)
        player.sendMessage("${prefix}§aテレポートします。")
        return
    }
}
