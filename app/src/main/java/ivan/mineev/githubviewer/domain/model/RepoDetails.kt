package ivan.mineev.githubviewer.domain.model

data class RepoDetails(
    val name: String,
    val url: String,
    val license: String?,
    val watchers: Int,
    val forks: Int,
    val stars: Int
)