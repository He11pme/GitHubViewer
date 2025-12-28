package ivan.mineev.githubviewer.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import ivan.mineev.githubviewer.model.Repo
import ivan.mineev.githubviewer.model.RepoDetails
import ivan.mineev.githubviewer.model.UserInfo
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://api.github.com/"
private val json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

private val basicRetrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

interface GitHubApiService {
    @GET("user")
    suspend fun getUser(
        @Header("Authorization") token: String
    ): UserInfo

    @GET("user/repos")
    suspend fun getRepositories(
        @Query("per_page") perPage: Int = 10,
        @Query("page") page: Int = 1
    ): List<Repo>

    @GET("repos/{owner}/{repo}")
    suspend fun getRepository(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): RepoDetails

    @GET("repos/{owner}/{repo}/readme")
    @Headers("Accept: application/vnd.github.raw")
    suspend fun getReadme(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): String

}

object GitHubApi {
    val unauthorized: GitHubApiService by lazy {
        basicRetrofit.create(GitHubApiService::class.java)
    }

    private var _authorized: GitHubApiService? = null
    val authorized
        get() = _authorized ?: throw RuntimeException("Authorized retrofit service not created")

    fun createAuthorizedService(token: String) {
        val client = createClient(token)

        val retrofitAuth = basicRetrofit.newBuilder()
            .client(client)
            .build()

        _authorized = retrofitAuth.create(GitHubApiService::class.java)
    }

    private fun createClient(token: String): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor { chain ->

            val original = chain.request()
            val builder = original.newBuilder()

            builder.addHeader("Authorization", token)
            if (original.header("Accept") == null) {
                builder.addHeader("Accept", "application/vnd.github+json")
            }

            chain.proceed(builder.build())
        }.build()
    }

}