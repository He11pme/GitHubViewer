package ivan.mineev.githubviewer.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepoDetails(
    val name: String,
    @SerialName("html_url")
    val url: String,
    val license: License?,
    @SerialName("watchers_count")
    val watchers: Int,
    @SerialName("forks_count")
    val forks: Int,
    @SerialName("stargazers_count")
    val stars: Int
)

@Serializable
data class License(
    val name: String
)