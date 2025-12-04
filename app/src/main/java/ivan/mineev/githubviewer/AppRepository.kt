package ivan.mineev.githubviewer

import dagger.hilt.android.scopes.ActivityRetainedScoped
import ivan.mineev.githubviewer.network.GitHubApi
import javax.inject.Inject

@ActivityRetainedScoped
class AppRepository @Inject constructor(val keyValueStorage: KeyValueStorage) {

    private var _user: UserInfo? = null
    val user: UserInfo get() = _user ?: throw Exception("User unauthorized")

    suspend fun signIn(token: String? = null): Result<Unit> {
        return (token ?: keyValueStorage.authToken)?.let {
            try {
                saveUser(getUser(it))
                saveToken(it)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } ?: Result.failure(Exception("token unsaved"))
    }
    private suspend fun getUser(token: String): UserInfo {
        // new format token: Bearer $token
        // old format token: token $token
        val prefix = if (token.contains(NEW_TOKEN_INCLUDE)) NEW_TOKEN_PREFIX else OLD_TOKEN_PREFIX
        return GitHubApi.retrofitService.getUser("$prefix $token")
    }

    private fun saveUser(userInfo: UserInfo) {
        _user = userInfo
    }

    private fun saveToken(token: String) {
        keyValueStorage.saveToken(token)
    }

    companion object {
        private const val NEW_TOKEN_PREFIX = "Bearer"
        private const val OLD_TOKEN_PREFIX = "token"

        private const val NEW_TOKEN_INCLUDE = "github_pat"
    }

}