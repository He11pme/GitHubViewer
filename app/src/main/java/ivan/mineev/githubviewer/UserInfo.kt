package ivan.mineev.githubviewer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@JsonIgnoreUnknownKeys
@Serializable
data class UserInfo(
    val name: String,
    val login: String,
    val id: Int,
    @SerialName("avatar_url")
    val avatarUrl: String,
    @SerialName("html_url")
    val url: String,
    @SerialName("repos_url")
    val reposUrl: String
)
