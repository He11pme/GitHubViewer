package ivan.mineev.githubviewer.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import ivan.mineev.githubviewer.UserInfo
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Header

private const val BASE_URL = "https://api.github.com/"
private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

interface GitHubApiService {
    @GET("user")
    suspend fun getUser(
        @Header("Authorization") token: String
    ): UserInfo
}

object GitHubApi {
    val retrofitService : GitHubApiService by lazy {
        retrofit.create(GitHubApiService::class.java)
    }
}