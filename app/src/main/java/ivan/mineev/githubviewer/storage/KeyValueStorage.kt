package ivan.mineev.githubviewer.storage

import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class KeyValueStorage @Inject constructor(private val sharedPreferences: SharedPreferences) {

    var authToken = sharedPreferences.getString(KEY_TOKEN, null)
        private set(value) {
            if (field != value) {
                sharedPreferences.edit { putString(KEY_TOKEN, value) }
                field = value
            }
        }

    fun saveToken(token: String?) {
        authToken = token
    }

    companion object {
        private const val KEY_TOKEN = "saved_token"

    }

}