package ivan.mineev.githubviewer.domain.model

data class Repo(
    val id: Int,
    val name: String,
    val description: String,
    val language: String,
    val isPrivate: Boolean,
    var color: Int
)
