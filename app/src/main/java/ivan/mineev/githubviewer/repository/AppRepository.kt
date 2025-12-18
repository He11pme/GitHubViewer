package ivan.mineev.githubviewer.repository

import dagger.hilt.android.scopes.ActivityRetainedScoped
import ivan.mineev.githubviewer.model.Repo
import ivan.mineev.githubviewer.model.RepoDetails
import ivan.mineev.githubviewer.model.UserInfo
import ivan.mineev.githubviewer.network.GitHubApi
import ivan.mineev.githubviewer.storage.KeyValueStorage
import javax.inject.Inject

@ActivityRetainedScoped
class AppRepository @Inject constructor(
    val keyValueStorage: KeyValueStorage,
    private val colorRepository: LanguageColorRepository
) {
    private var _user: UserInfo? = null
    val user: UserInfo get() = _user ?: throw Exception("User unauthorized")

    private var _repositories: List<Repo>? = null
    val repositories: List<Repo> get() = _repositories ?: throw Exception("Repositories unloaded")


    suspend fun signIn(token: String? = null): Result<Unit> {
        return (token ?: keyValueStorage.authToken)?.let {
            try {
                saveUser(getUser(it))
                saveToken(it)
                createAuthApi(it)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } ?: Result.failure(Exception("token unsaved"))
    }

    private suspend fun getUser(token: String): UserInfo {
        return GitHubApi.unauthorized.getUser(fullToken(token))
    }

    private fun saveUser(userInfo: UserInfo) {
        _user = userInfo
    }

    private fun saveToken(token: String) {
        keyValueStorage.saveToken(token)
    }

    private fun createAuthApi(token: String) {
        GitHubApi.createAuthorizedService(fullToken(token))
    }

    suspend fun loadRepositories(): Result<Unit> {
        return try {
            _repositories = getRepositories()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getRepositories(): List<Repo> {
        return GitHubApi.authorized.getRepositories().setColor(colorRepository)
    }

    suspend fun loadRepo(repo: String): Result<RepoDetails> {
        return try {
            Result.success(getRepo(repo))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getRepo(repo: String): RepoDetails {
        return GitHubApi.authorized.getRepository(user.login, repo)
    }

    suspend fun loadReadme(repo: String): Result<String> {
        return try {
            Result.success(getReadme(repo))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getReadme(repo: String): String {
        return GitHubApi.authorized.getReadme(user.login, repo)
    }

    fun logout() {
        keyValueStorage.deleteToken()
        _user = null
        _repositories = null
    }

    companion object {
        private const val NEW_TOKEN_PREFIX = "Bearer"
        private const val OLD_TOKEN_PREFIX = "token"
        private const val NEW_TOKEN_INCLUDE = "github_pat"

        private val fullToken: (String) -> String = { token ->
            // new format token: Bearer $token
            // old format token: token $token
            val prefix =
                if (token.contains(NEW_TOKEN_INCLUDE)) NEW_TOKEN_PREFIX else OLD_TOKEN_PREFIX

            "$prefix $token"
        }
    }


}

private suspend fun List<Repo>.setColor(colorRepository: LanguageColorRepository): List<Repo> {
    forEach { it.color = colorRepository.getColorFor(it.language) }
    return this
}