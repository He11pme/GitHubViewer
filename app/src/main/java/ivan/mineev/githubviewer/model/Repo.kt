package ivan.mineev.githubviewer.model

import kotlinx.serialization.Serializable

@Serializable
data class Repo(
    val id: Int,
    val name: String,
    val description: String = "",
    val language: String
)
