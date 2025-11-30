package ivan.mineev.githubviewer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class KeyValueStorage @Inject constructor() {

    private val _authToken = MutableLiveData<String?>(null)
    val authToken: LiveData<String?> get() = _authToken

    fun saveToken(token: String?) {
        _authToken.value = token
    }

}