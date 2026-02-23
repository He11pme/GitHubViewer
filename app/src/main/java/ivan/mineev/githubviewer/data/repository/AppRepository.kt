package ivan.mineev.githubviewer.data.repository

import dagger.hilt.android.scopes.ActivityRetainedScoped
import ivan.mineev.githubviewer.data.local.storage.KeyValueStorage
import ivan.mineev.githubviewer.data.mappers.toDomain
import ivan.mineev.githubviewer.data.model.UserInfoDto
import ivan.mineev.githubviewer.data.network.GitHubApi
import ivan.mineev.githubviewer.domain.model.Repo
import ivan.mineev.githubviewer.domain.model.RepoDetails
import ivan.mineev.githubviewer.managers.TokenManager
import javax.inject.Inject

@ActivityRetainedScoped
class AppRepository @Inject constructor(
    val keyValueStorage: KeyValueStorage,
    private val colorRepository: LanguageColorRepository
) {
    private var _user: UserInfoDto? = null
    private val user: UserInfoDto get() = _user ?: throw RuntimeException("User unauthorized")

    private var _repositories: List<Repo>? = null
    val repositories: List<Repo>
        get() = _repositories ?: throw RuntimeException("Repositories unloaded")


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
        } ?: Result.failure(RuntimeException("token unsaved"))
    }

    private suspend fun getUser(token: String): UserInfoDto {
        return GitHubApi.unauthorized.getUser(TokenManager.fullToken(token))
    }

    private fun saveUser(userInfo: UserInfoDto) {
        _user = userInfo
    }

    private fun saveToken(token: String) {
        keyValueStorage.saveToken(token)
    }

    private fun createAuthApi(token: String) {
        GitHubApi.createAuthorizedService(TokenManager.fullToken(token))
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
        return GitHubApi.authorized.getRepositories().map {
            it.toDomain(colorRepository.getColorFor(it.language ?: ""))
        }
    }

    suspend fun loadRepo(repo: String): Result<RepoDetails> {
        return try {
            Result.success(getRepo(repo))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getRepo(repo: String): RepoDetails {
        return GitHubApi.authorized.getRepository(user.login, repo).toDomain()
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

}

//private suspend fun List<RepoDto>.setColor(colorRepository: LanguageColorRepository): List<RepoDto> {
//    forEach {
//        it.language?.let {language ->
//            it.color = colorRepository.getColorFor(language)
//        }
//
//    }
//    return this
//}