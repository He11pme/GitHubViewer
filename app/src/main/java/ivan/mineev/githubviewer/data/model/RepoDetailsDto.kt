package ivan.mineev.githubviewer.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepoDetailsDto(
    val name: String,
    @SerialName("html_url")
    val url: String,
    val license: LicenseDto?,
    @SerialName("watchers_count")
    val watchers: Int,
    @SerialName("forks_count")
    val forks: Int,
    @SerialName("stargazers_count")
    val stars: Int
)

@Serializable
data class LicenseDto(
    val name: String
)