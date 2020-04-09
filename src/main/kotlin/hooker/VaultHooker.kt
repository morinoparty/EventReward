package com.github.namiuni.eventreward.hooker

import net.milkbowl.vault.economy.Economy
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginManager

class VaultHooker {
    val pluginName = "Vault"
    var hasHooked = false
    lateinit var economy: Economy

    fun canHook(pluginManager: PluginManager) = pluginManager.isPluginEnabled(pluginName)

    fun hook(plugin: Plugin) {
        checkEnabled(this, plugin.server.pluginManager)

        val registration = plugin.server.servicesManager.getRegistration(Economy::class.java)
        if (registration != null) {
            economy = registration.provider
        }
        hasHooked = true
    }

    fun hookIfEnabled(plugin: Plugin) {
        if (canHook(plugin.server.pluginManager)) {
            hook(plugin)
        }
    }

    fun hasEconomy(): Boolean {
        return ::economy.isInitialized
    }

    companion object {
        fun checkEnabled(hooker: VaultHooker, pluginManager: PluginManager) {
            check(hooker.canHook(pluginManager)) { "${hooker.pluginName} を有効にする必要があります" }
        }

        fun checkHooked(hooker: VaultHooker) {
            check(hooker.hasHooked) { "${hooker.pluginName} と接続する必要があります" }
        }
    }
}