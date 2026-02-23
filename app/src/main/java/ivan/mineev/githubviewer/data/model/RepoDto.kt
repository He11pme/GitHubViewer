package ivan.mineev.githubviewer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepoDto(
    val id: Int,
    val name: String,
    val description: String = "",
    val language: String?,
    @SerialName("private")
    val isPrivate: Boolean
)
//{
//    var color = "#808080".toColorInt()
//}
