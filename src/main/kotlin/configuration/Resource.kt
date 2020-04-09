package com.github.namiuni.eventreward.configuration

import com.charleskorn.kaml.Yaml
import com.github.namiuni.eventreward.configuration.configdata.ContestantData
import configuration.configdata.AwardData
import kotlinx.serialization.builtins.set
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.FileReader
import java.nio.file.Files
import java.nio.file.StandardOpenOption

class Resource(val plugin: Plugin) {
    private val json = Json(JsonConfiguration(prettyPrint = true))

    //報酬リスト
    private val configFile = File(plugin.dataFolder,"award.yml")
    lateinit var awardData :AwardData

    //参加者リスト
    private var contestantFile = File(plugin.dataFolder, "contestant.json")
    lateinit var contestantDataList: MutableSet<ContestantData>


    fun load() {
        val configText = loadText(configFile)
        val contestantText = loadText(contestantFile)
        contestantDataList = json.parse(ContestantData.serializer().set,contestantText).toMutableSet()
        awardData = Yaml.default.parse(AwardData.serializer(),configText)

    }
    private fun loadText(file: File) :String {
        if (!file.exists()) plugin.saveResource(file.name, false)
        return FileReader(file).readText()
    }


    fun saveContestant(playerName: String) {
        contestantDataList.find { it.name == playerName }?.received = true
        contestantFile.delete()
        val saveJson = json.stringify(ContestantData.serializer().set,contestantDataList)
        Files.write(contestantFile.toPath(), saveJson.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)
    }


    fun contestantData(playerName: String) :ContestantData? {
        return contestantDataList.find { it.name == playerName }
    }
}