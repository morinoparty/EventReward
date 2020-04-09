package com.github.namiuni.eventreward

import co.aikar.commands.PaperCommandManager
import com.github.namiuni.eventreward.command.MainCommand
import com.github.namiuni.eventreward.configuration.Resource
import com.github.namiuni.eventreward.hooker.VaultHooker
import com.github.namiuni.eventreward.listener.MainListener
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class EventReward: JavaPlugin() {

    private val resource = Resource(this)
    private val listener = MainListener(resource)
    private val vault = VaultHooker()

    override fun onEnable() {

        //必要なやつ読み込む
        resource.load()
        vault.hookIfEnabled(this)

        //プラグインマネージャー
        Bukkit.getPluginManager().registerEvents(listener,this)

        //コマンドマネージャー
        val manager = PaperCommandManager(this)
        val mainCommand = MainCommand(resource,vault)
        manager.registerCommand(mainCommand)
    }

    override fun onDisable() {}

    companion object {
        private lateinit var INSTANCE: EventReward
        val instance: EventReward
            get() = INSTANCE
    }
}