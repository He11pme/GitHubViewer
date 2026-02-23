package ivan.mineev.githubviewer.domain.repository

import dagger.hilt.android.scopes.ActivityRetainedScoped
import ivan.mineev.githubviewer.domain.model.Repo
import ivan.mineev.githubviewer.domain.model.RepoDetails

@ActivityRetainedScoped
interface AppRepository {

    suspend fun signIn(token: String? = null): Result<Unit>
    suspend fun loadRepositories(isUpdate: Boolean = false): Result<List<Repo>>
    suspend fun loadRepo(repo: String): Result<RepoDetails>
    suspend fun loadReadme(repo: String): Result<String>
    fun logout()

}