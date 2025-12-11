package ivan.mineev.githubviewer.repository

import android.content.Context
import android.util.Log
import androidx.core.graphics.toColorInt
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageColorRepository @Inject constructor(@ApplicationContext context: Context) {

    private val colors: Map<String, String> = loadLanguageColors(context)

    fun getColorFor(language: String): Int {

        val hex = colors[language] ?: OTHER_LANGUAGE_COLOR
        try {
            return hex.toColorInt()
        } catch (e: IllegalArgumentException) {
            logError("Невалидный HEX: ${e.message}")
            return OTHER_LANGUAGE_COLOR.toColorInt()
        }

    }

    private fun loadLanguageColors(context: Context): Map<String, String> {
        try {
            val input = context.assets.open(FILE_WITH_COLORS)
            val jsonString = input.bufferedReader().use { it.readText() }
            return Json.decodeFromString(jsonString)
        } catch (e: Exception) {

            when(e) {
                is FileNotFoundException -> logError("Не удалось найти файл: ${FILE_WITH_COLORS}. ${e.message}")
                is IOException -> logError("message: ${e.message}")
                is SerializationException -> logError("message: ${e.message}")
                else -> logError("Непредвиденная ошибка: ${e.message}")
            }
            return emptyMap()
        }
    }

    private fun logError(message: String) {
        Log.e(TAG, message)
    }

    companion object {
        private const val TAG = "LANGUAGE_COLOR_REPOSITORY"
        private const val OTHER_LANGUAGE_COLOR = "#808080"
        private const val FILE_WITH_COLORS = "github-lang-colors.json"
    }

}


