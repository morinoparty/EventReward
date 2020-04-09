package com.github.namiuni.eventreward.listener

import com.github.namiuni.eventreward.configuration.Resource
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class MainListener(private val resource: Resource) :Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        val contestantData = resource.contestantDataList.find { it.name == player.name }
        if (contestantData != null && !contestantData.received) {
            consoleCommand("""tellraw ${player.name} ["",{"text":"[イベント]","color":"dark_aqua"},{"text":" エンドラ討伐イベントの報酬が受け取れます","color":"green"},{"text":" "},{"text":"[受取]","color":"light_purple","clickEvent":{"action":"run_command","value":"/event reward"}}]""")
        }
    }

    private fun consoleCommand(string: String) {
        Bukkit.getServer().dispatchCommand(
            Bukkit.getConsoleSender(), string)
    }
}