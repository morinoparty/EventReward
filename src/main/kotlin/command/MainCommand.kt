package com.github.namiuni.eventreward.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Subcommand
import com.github.namiuni.eventreward.configuration.Resource
import com.github.namiuni.eventreward.hooker.VaultHooker
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.TNT
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import sun.security.krb5.EncryptedData

@CommandAlias("event")
class MainCommand(
    private val resource: Resource,
    private val vault: VaultHooker
): BaseCommand() {
    private val economy: Economy get() {
        check(vault.hasHooked) { "エコノミーの機能を使用するには、Vaultと接続する必要があります" }
        check(vault.hasEconomy()) { "Vaultにはエコノミープラグインがありません" }
        check(vault.economy.isEnabled) { "エコノミーを有効にする必要があります" }
        return vault.economy
    }

    private val prefix = "${ChatColor.DARK_AQUA}[イベント]${ChatColor.GREEN}"

    @Default
    @Subcommand("reward")
    fun reward(sender: CommandSender) {
        // 対象者チェック
        if (sender !is Player) return sender.sendMessage("$prefix お前はコンソールじゃ！")

        val player = sender as Player
        val contestantData = resource.contestantData(player.name)
        val emptySlotCount = player.inventory.storageContents.count { it == null }

        if (contestantData == null) return player.sendMessage("$prefix イベント報酬の対象者ではありません")
        if (contestantData.received) return player.sendMessage("$prefix 既に受取を完了しています")
        if (emptySlotCount < 2) return player.sendMessage("$prefix インベントリに空きスロットが２つ以上ありません")


        // 報酬チェック
        val awardNumber = contestantData.award
        val awardValue = resource.awardData.awardlist[awardNumber]
            ?: return player.sendMessage("$prefix エラー：報酬番号が一致しません。うにたろうくんへお知らせ下さい")

        val awardEconomy = awardValue.economy.toDouble()
        val awardDragonEgg =
            if (awardValue.dragonEgg) ItemStack(Material.DRAGON_EGG)
            else ItemStack(Material.EGG)
        val awardItem = Material.matchMaterial(awardValue.item)?.let { ItemStack(it,awardValue.amount) }
            ?: return player.sendMessage("$prefix エラー：報酬アイテムが一致しません。うにたろうくんにキレながらお問い合わせ下さい")


        // 報酬プレゼント
        economy.depositPlayer(player,awardEconomy)
        player.inventory.addItem(awardDragonEgg)
        player.inventory.addItem(awardItem)

        player.sendMessage("""
            ${ChatColor.GOLD}${awardEconomy.toInt()} 円 ${ChatColor.GREEN}を手に入れた！
            ${ChatColor.GOLD}${awardDragonEgg.i18NDisplayName} ${ChatColor.GREEN}を ${awardDragonEgg.amount}つ 手に入れた！
            ${ChatColor.GOLD}${awardItem.i18NDisplayName} ${ChatColor.GREEN}を ${awardItem.amount}つ 手に入れた！
        """.trimIndent())
        resource.saveContestant(player.name)
    }

    @Subcommand("contestant")
    @CommandPermission("moripa.mod")
    fun contestant(sender: CommandSender) {
        sender.sendMessage("$prefix イベント報酬の該当者リストを表示します")

        val incomplete = resource.contestantDataList.filter { !it.received }
        val contestantNameList :MutableSet<String> = mutableSetOf()
        val onlinePlayerList = Bukkit.getOnlinePlayers()

        for (contestant in incomplete) {
            for (onlinePlayer in onlinePlayerList) {
                if (contestant.name == onlinePlayer.name) {
                    contestantNameList.add("${ChatColor.GOLD}${contestant.name}")
                } else {
                    contestantNameList.add("${ChatColor.RESET}${contestant.name}")
                }
            }
        }
        sender.sendMessage(contestantNameList.toString())
    }
}