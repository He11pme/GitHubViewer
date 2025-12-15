package ivan.mineev.githubviewer.fragments.info

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ivan.mineev.githubviewer.model.RepoDetails
import ivan.mineev.githubviewer.repository.AppRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepositoryInfoViewModel @Inject constructor(val appRepository: AppRepository): ViewModel()  {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> get() = _state

    private lateinit var repository: RepoDetails

    fun loadRepo(nameRepo: String) {
        _state.value = State.Loading
        viewModelScope.launch {
            tryLoadRepository(nameRepo).apply {
                onSuccess { handleSuccess(it) }
                onFailure { e -> handleError(e) }
            }
        }
    }

    private suspend fun tryLoadRepository(nameRepo: String): Result<RepoDetails> {
        return appRepository.loadRepo(nameRepo)
    }

    private fun handleSuccess(details: RepoDetails) {
        setRepository(details)
        _state.value = State.Loaded(details, null)
    }

    private fun setRepository(details: RepoDetails) {
        repository = details
    }

    private fun handleError(e: Throwable) {
        Log.e("DETAIL", e.message.toString())
    }

    sealed interface State {
        object Loading: State
        data class Error(val error: Int): State
        data class Loaded(
            val gitHubRepo: RepoDetails,
            val readmeState: ReadmeState?
        ) : State
    }

    sealed interface ReadmeState {
        object Loading: ReadmeState
        object Empty: ReadmeState
        data class Error(val error: Int): ReadmeState
        data class Loaded(val markdown: String): ReadmeState
    }



}