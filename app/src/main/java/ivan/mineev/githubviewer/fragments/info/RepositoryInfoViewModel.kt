package ivan.mineev.githubviewer.fragments.info

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ivan.mineev.githubviewer.R
import ivan.mineev.githubviewer.model.RepoDetails
import ivan.mineev.githubviewer.repository.AppRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class RepositoryInfoViewModel @Inject constructor(val appRepository: AppRepository) : ViewModel() {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> get() = _state

    private lateinit var repository: RepoDetails
    private var readmeState: ReadmeState? = null

    fun loadRepo(nameRepo: String) {
        _state.value = State.Loading
        viewModelScope.launch {
            tryLoadRepository(nameRepo).apply {
                onSuccess {
                    repository = it
                    _state.value = State.Loaded(repository, readmeState)
                }
                onFailure {
                    _state.value = State.Error(R.string.unexpected_error)
                    Log.e("DETAIL", it.message.toString())
                }
            }
            tryLoadReadme(nameRepo).apply {
                onSuccess {
                    readmeState = ReadmeState.Loaded(it)
                    if (state.value is State.Loaded) _state.value = State.Loaded(repository, readmeState)
                }
                onFailure { e ->
                    when(e) {
                        is HttpException -> {
                            if (e.code() == 404) {
                                readmeState = ReadmeState.Empty
                            }
                        }
                    }
                    Log.e("DETAIL", e.message.toString())
                }
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

    private fun handleSuccess(details: RepoDetails) {
//        setRepository(details)
        _state.value = State.Loaded(details, null)
    }

    private fun handleError(e: Throwable) {
        Log.e("DETAIL", e.message.toString())
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


}