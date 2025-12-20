package ivan.mineev.githubviewer.fragments.repos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ivan.mineev.githubviewer.R
import ivan.mineev.githubviewer.model.Repo
import ivan.mineev.githubviewer.repository.AppRepository
import ivan.mineev.githubviewer.utils.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class RepositoriesViewModel @Inject constructor(
    val appRepository: AppRepository,
    val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> get() = _state

    private val _actions = MutableSharedFlow<Action>()
    val actions: Flow<Action> get() = _actions

    fun reloadRepositories() {
        _state.value = State.Reloading
        tryLoadRepositories()
    }

    fun loadRepositories() {
        _state.value = State.Loading
        tryLoadRepositories()
    }

    private fun tryLoadRepositories() {
        viewModelScope.launch {
            appRepository.loadRepositories().apply {
                onSuccess { handleSuccess() }
                onFailure { e -> handleError(e) }
            }
        }
    }

    private fun handleSuccess() {
        if (appRepository.repositories.isEmpty()) _state.value = State.Empty
        else _state.value = State.Loaded(appRepository.repositories)
    }

    private suspend fun handleError(e: Throwable) {
        when (e) {
            is IOException -> handleIOException()
            is HttpException -> handleHttpException(e)
            else -> handleUnexpectedError()
        }
        Log.e(TAG, e.message.toString())
    }

    private fun handleIOException() {
        _state.value = State.Error(R.string.network_error)
    }

    private suspend fun handleHttpException(e: HttpException) {
        when (e.code()) {
            401 -> {
                _actions.emit(Action.ForceLogout(R.string.unauthorized_error))
                forceLogout()
            }

            403 -> _state.value = State.Error(R.string.forbidden_error)

            else -> _state.value = State.Error(R.string.server_error)
        }
    }

    private suspend fun forceLogout() {
        sessionManager.logout()
    }

    private fun handleUnexpectedError() {
        _state.value = State.Error(R.string.unexpected_error)
    }

    sealed interface State {

        object Reloading: State
        object Loading : State
        data class Loaded(val repos: List<Repo>) : State
        data class Error(val error: Int) : State
        object Empty : State
    }

    sealed interface Action {
        data class ForceLogout(val message: Int) : Action

    }

    companion object {
        private const val TAG = "REPOSITORIES"
    }

}