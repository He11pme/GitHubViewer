package ivan.mineev.githubviewer.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://raw.githubusercontent.com/github/linguist/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface LinguistService {

    @GET("master/lib/linguist/languages.yml")
    suspend fun getLanguagesYml(): String

}

object LinguistApi {
    val linguistService: LinguistService by lazy {
        retrofit.create(LinguistService::class.java)
    }
}