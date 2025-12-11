package ivan.mineev.githubviewer.fragments.repos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ivan.mineev.githubviewer.model.Repo
import ivan.mineev.githubviewer.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepositoriesViewModel @Inject constructor(
    val appRepository: AppRepository
) : ViewModel() {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> get() = _state

    private val _actions = MutableSharedFlow<Action>()
    val actions: Flow<Action> get() = _actions

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

    private fun handleError(e: Throwable) {
        _state.value = State.Error(e.message ?: "")
        Log.e(TAG, e.message.toString())
    }

    sealed interface State {
        object Loading : State
        data class Loaded(val repos: List<Repo>) : State
        data class Error(val error: String) : State
        object Empty : State
    }

    sealed interface Action {
        data class RouteToRepo(val id: Int) : Action
    }

    companion object {
        private const val TAG = "REPOSITORIES"
    }

}