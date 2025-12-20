package ivan.mineev.githubviewer.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ivan.mineev.githubviewer.repository.AppRepository
import ivan.mineev.githubviewer.utils.AppBarManager
import ivan.mineev.githubviewer.utils.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val sessionManager: SessionManager,
    private val appBarManager: AppBarManager
) :
    ViewModel() {

    private val _actions = MutableSharedFlow<Action>()
    val actions: Flow<Action> get() = _actions

    var keepSplashScreen = true

    init {
        trySignIn()
        observeSession()
        observeAppBarManager()
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
        _actions.emit(Action.SetReposAsStart)
    }

    private suspend fun handleFailureSignIn() {
        _actions.emit(Action.SetAuthAsStart)
    }

    private fun releaseSplashScreen() {
        keepSplashScreen = false
    }

    private fun observeSession() {
        viewModelScope.launch {
            sessionManager.action.collect { action ->
                when (action) {
                    SessionManager.SessionAction.Logout -> {
                        _actions.emit(Action.RouteToAuth)
                    }
                }
            }
        }
    }

    private fun observeAppBarManager() {
        viewModelScope.launch {
            appBarManager.action.collect { action ->
                when (action) {
                    is AppBarManager.AppBarAction.SetTitle -> {
                        _actions.emit(Action.SetTitleAppBar(action.title))
                    }
                }
            }
        }
    }

    fun onLogoutPressed() = logout()

    private fun logout() {
        viewModelScope.launch {
            sessionManager.logout()
        }
    }

    sealed interface Action {
        object SetAuthAsStart : Action
        object SetReposAsStart : Action
        object RouteToAuth : Action

        data class SetTitleAppBar(val title: String) : Action
    }

}