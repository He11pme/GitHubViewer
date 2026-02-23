package ivan.mineev.githubviewer.data.mappers

import ivan.mineev.githubviewer.data.model.RepoDetailsDto
import ivan.mineev.githubviewer.domain.model.RepoDetails

fun RepoDetailsDto.toDomain(): RepoDetails {
    return RepoDetails(
        name = name,
        url = url,
        license = license?.name,
        watchers = watchers,
        forks = forks,
        stars = stars
    )
}