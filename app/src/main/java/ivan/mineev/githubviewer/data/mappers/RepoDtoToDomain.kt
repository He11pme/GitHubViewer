package ivan.mineev.githubviewer.data.mappers

import ivan.mineev.githubviewer.data.model.RepoDto
import ivan.mineev.githubviewer.domain.model.Repo

fun RepoDto.toDomain(color: Int): Repo {
    return Repo(
        id = id,
        name = name,
        description = description,
        language = language ?: "",
        isPrivate = isPrivate,
        color = color
    )
}