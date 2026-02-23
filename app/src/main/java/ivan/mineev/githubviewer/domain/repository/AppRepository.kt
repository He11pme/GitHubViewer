package ivan.mineev.githubviewer.domain.repository

import dagger.hilt.android.scopes.ActivityRetainedScoped
import ivan.mineev.githubviewer.domain.model.Repo
import ivan.mineev.githubviewer.domain.model.RepoDetails

@ActivityRetainedScoped
interface AppRepository {

    val repositories: List<Repo>
    suspend fun signIn(token: String? = null): Result<Unit>
    suspend fun loadRepositories(): Result<Unit>
    suspend fun loadRepo(repo: String): Result<RepoDetails>
    suspend fun loadReadme(repo: String): Result<String>
    fun logout()

}