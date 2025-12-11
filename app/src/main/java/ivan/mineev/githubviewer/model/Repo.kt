package ivan.mineev.githubviewer.model

import androidx.core.graphics.toColorInt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Repo(
    val id: Int,
    val name: String,
    val description: String = "",
    val language: String,
    @SerialName("private")
    val isPrivate: Boolean
) {
    var color = "#808080".toColorInt()
}
