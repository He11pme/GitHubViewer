package ivan.mineev.githubviewer.managers

object TokenManager {

    private const val NEW_TOKEN_PREFIX = "Bearer"
    private const val OLD_TOKEN_PREFIX = "token"
    const val NEW_TOKEN_INCLUDE = "github_pat"

    const val OLD_TOKEN_INCLUDE = "ghp"

    val fullToken: (String) -> String = { token ->
        // new format token: Bearer $token
        // old format token: token $token
        val prefix =
            if (token.contains(NEW_TOKEN_INCLUDE)) NEW_TOKEN_PREFIX else OLD_TOKEN_PREFIX

        "$prefix $token"
    }

}