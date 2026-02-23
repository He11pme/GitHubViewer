package ivan.mineev.githubviewer.data.repository

import android.util.Log
import androidx.core.graphics.toColorInt
import ivan.mineev.githubviewer.data.network.LinguistApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.serialization.SerializationException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageColorRepository @Inject constructor() {

    private val colorsDeferred = CoroutineScope(Dispatchers.IO).async {
        loadLanguageColors()
    }

    suspend fun getColorFor(language: String): Int {
        val colors = colorsDeferred.await()
        val hex = colors[language] ?: OTHER_LANGUAGE_COLOR
        try {
            return hex.toColorInt()
        } catch (e: IllegalArgumentException) {
            logError("Unvalid HEX: ${e.message}")
            return OTHER_LANGUAGE_COLOR.toColorInt()
        }

    }

    private suspend fun loadLanguageColors(): Map<String, String> {
        try {
            val yamlString = LinguistApi.linguistService.getLanguagesYml()
            return parseColorsFromYaml(yamlString)
        } catch (e: Exception) {

            when (e) {
                is IOException -> logError("IOException: message: ${e.message}")
                is SerializationException -> logError("SerializationException: message: ${e.message}")
                else -> logError("Unexpected error: ${e.message}")
            }
            return emptyMap()
        }
    }

    private fun parseColorsFromYaml(yaml: String): Map<String, String> {
        val regex = Regex(
            """^(\w[\w\s+#-]*)\s*:\s*\n(?:.*\n)*?\s+color:\s+"(#[0-9a-fA-F]{6})"""",
            RegexOption.MULTILINE
        )
        return regex.findAll(yaml).map { matchResult ->
            val (language, color) = matchResult.destructured
            language to color
        }.toMap()
    }

    private fun logError(message: String) {
        Log.e(TAG, message)
    }

    companion object {
        private const val TAG = "LANGUAGE_COLOR_REPOSITORY"
        private const val OTHER_LANGUAGE_COLOR = "#808080"
    }

}


