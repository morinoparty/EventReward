package configuration.configdata


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AwardData(
    val awardlist: Map<Int,AwardValue>
) {
    @Serializable
    data class AwardValue(
        val economy: Int,
        @SerialName("dragonegg")
        val dragonEgg: Boolean,
        val item: String,
        val amount: Int
    )
}