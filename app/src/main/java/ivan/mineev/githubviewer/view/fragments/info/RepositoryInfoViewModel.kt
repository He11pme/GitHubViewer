package ivan.mineev.githubviewer.view.fragments.info

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ivan.mineev.githubviewer.R
import ivan.mineev.githubviewer.view.managers.AppBarManager
import ivan.mineev.githubviewer.view.managers.SessionManager
import ivan.mineev.githubviewer.data.model.RepoDetails
import ivan.mineev.githubviewer.data.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class RepositoryInfoViewModel @Inject constructor(
    val appRepository: AppRepository,
    val sessionManager: SessionManager,
    val appBarManager: AppBarManager
) : ViewModel() {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> get() = _state

    private val _actions = MutableSharedFlow<Action>()

    val actions: Flow<Action> get() = _actions

    private lateinit var repository: RepoDetails
    private var readmeState: ReadmeState? = null

    fun setTitleAppBar(nameRepo: String) {
        viewModelScope.launch {
            appBarManager.setTitleAppBar(nameRepo)
        }
    }

    fun loadRepo(nameRepo: String) {
        _state.value = State.Loading
        viewModelScope.launch {
            tryLoadRepository(nameRepo).apply {
                onSuccess { handleSuccessLoadRepo(it) }
                onFailure { handleErrorLoadRepo(it) }
            }
            tryLoadReadme(nameRepo).apply {
                onSuccess { handleSuccessLoadReadme(it) }
                onFailure { e -> handleErrorLoadReadme(e) }
                if (state.value is State.Loaded) _state.value =
                    State.Loaded(repository, readmeState)
            }
        }
    }

    private suspend fun tryLoadRepository(nameRepo: String): Result<RepoDetails> {
        _state.value = State.Loading
        return appRepository.loadRepo(nameRepo)
    }

    private suspend fun tryLoadReadme(nameRepo: String): Result<String> {
        readmeState = ReadmeState.Loading
        return appRepository.loadReadme(nameRepo)
    }

    private fun handleSuccessLoadRepo(details: RepoDetails) {
        repository = details
        _state.value = State.Loaded(repository, readmeState)
    }

    private suspend fun handleErrorLoadRepo(e: Throwable) {
        when (e) {
            is IOException -> handleIOExceptionLoadRepo()
            is HttpException -> handleHttpExceptionLoadRepo(e)
            else -> handleUnexpectedErrorLoadRepo()
        }
        Log.e(TAG, e.message.toString())
    }

    private fun handleSuccessLoadReadme(string: String) {
        readmeState = ReadmeState.Loaded(string)
    }

    private suspend fun handleErrorLoadReadme(e: Throwable) {
        when (e) {
            is IOException -> handleIOExceptionLoadReadme()
            is HttpException -> handleHttpExceptionLoadReadme(e)
            else -> handleUnexpectedErrorLoadReadme()
        }
        Log.e(TAG, e.message.toString())
    }

    private fun handleIOExceptionLoadRepo() {
        _state.value = State.Error(R.string.network_error)
    }

    private fun handleIOExceptionLoadReadme() {
        readmeState = ReadmeState.Error(R.string.network_error)
    }

    private suspend fun handleHttpExceptionLoadRepo(e: HttpException) {
        when (e.code()) {
            401 -> forceLogout()
            403 -> _state.value = State.Error(R.string.forbidden_error)
            404 -> _state.value = State.Error(R.string.repository_not_found)

            else -> _state.value = State.Error(R.string.server_error)
        }
    }

    private suspend fun handleHttpExceptionLoadReadme(e: HttpException) {
        when (e.code()) {
            401 -> forceLogout()
            403 -> readmeState = ReadmeState.Error(R.string.forbidden_error)
            404 -> readmeState = ReadmeState.Empty

            else -> readmeState = ReadmeState.Error(R.string.server_error)
        }
    }

    private fun handleUnexpectedErrorLoadRepo() {
        _state.value = State.Error(R.string.unexpected_error)
    }

    private fun handleUnexpectedErrorLoadReadme() {
        readmeState = ReadmeState.Error(R.string.unexpected_error)
    }

    private suspend fun forceLogout() {
        _actions.emit(Action.ForceLogout(R.string.unauthorized_error))
        sessionManager.logout()
    }

    fun onLinkPressed() {
        openLink()
    }

    private fun openLink() {
        viewModelScope.launch {
            _actions.emit(Action.OpenLink(repository.url))
        }
    }


    sealed interface State {
        object Loading : State
        data class Error(val error: Int) : State
        data class Loaded(
            val gitHubRepo: RepoDetails,
            val readmeState: ReadmeState?
        ) : State
    }

    sealed interface ReadmeState {
        object Loading : ReadmeState
        object Empty : ReadmeState
        data class Error(val error: Int) : ReadmeState
        data class Loaded(val markdown: String) : ReadmeState
    }

    sealed interface Action {
        data class ForceLogout(val message: Int) : Action
        data class OpenLink(val link: String) : Action
    }

    companion object {
        private const val TAG = "REPO_INFO"
    }


}