package ivan.mineev.githubviewer.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ivan.mineev.githubviewer.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val appRepository: AppRepository) :
    ViewModel() {

    private val _actions = MutableSharedFlow<Action>()
    val actions: Flow<Action> get() = _actions

    var keepSplashScreen = true

    init {
        trySignIn()
    }

    private fun trySignIn() {
        viewModelScope.launch {
            appRepository.signIn().apply {
                onSuccess { handleSuccessSignIn() }
                onFailure { handleFailureSignIn() }
            }
            releaseSplashScreen()
        }

    }

    private suspend fun handleSuccessSignIn() {
        _actions.emit(Action.RouteToRepositories)
    }

    private suspend fun handleFailureSignIn() {
        _actions.emit(Action.RouteToAuth)
    }

    private fun releaseSplashScreen() {
        keepSplashScreen = false
    }

    sealed interface Action {
        object RouteToAuth : Action
        object RouteToRepositories : Action
    }

}