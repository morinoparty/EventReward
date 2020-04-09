package com.github.namiuni.eventreward.configuration.configdata

import kotlinx.serialization.Serializable

@Serializable
data class ContestantData(
    val name: String,
    val award: Int,
    var received: Boolean
)