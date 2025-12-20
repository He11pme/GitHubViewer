package ivan.mineev.githubviewer.fragments.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ivan.mineev.githubviewer.R
import ivan.mineev.githubviewer.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(val appRepository: AppRepository) : ViewModel() {

    val token = MutableLiveData<String>()

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> get() = _state

    private val _actions = MutableSharedFlow<Action>()
    val actions: Flow<Action> get() = _actions

    fun onSignButtonPressed() = signIn()

    private fun signIn() {
        token.value.let { currentToken ->

            if (currentToken.isNullOrBlank()) {
                _state.value = State.InvalidInput(R.string.empty_token)
                return
            }

            viewModelScope.launch { _actions.emit(Action.HideKeyboard) }

            _state.value = State.Loading

            trySignIn(currentToken)
        }
    }

    private fun trySignIn(currentToken: String) {
        viewModelScope.launch {
            appRepository.signIn(currentToken).apply {
                onSuccess { handleSuccess() }
                onFailure { e -> handleError(e) }
            }
        }
    }

    private suspend fun handleSuccess() {
        _state.value = State.Idle
        _actions.emit(Action.RouteToMain)
    }

    private suspend fun handleError(e: Throwable) {
        when (e) {
            is IOException -> handleIOException()
            is HttpException -> handleHttpException(e)
            else -> handleUnexpectedError()
        }
        Log.e(TAG, e.message.toString())
    }

    private suspend fun handleUnexpectedError() {
        _state.value = State.Idle
        _actions.emit(Action.ShowError(R.string.unexpected_error))
    }

    private suspend fun handleHttpException(e: HttpException) {
        val typeError = when (e.code()) {
            401 -> R.string.unauthorized_error_login.also { _state.value = State.InvalidInput(it) }
            403 -> R.string.forbidden_error.also { _state.value = State.Idle }
            else -> R.string.server_error.also { _state.value = State.Idle }
        }
        _actions.emit(Action.ShowError(typeError))
    }

    private suspend fun handleIOException() {
        _state.value = State.Idle
        _actions.emit(Action.ShowError(R.string.network_error))
    }

    sealed interface State {
        object Idle : State
        object Loading : State

        // reason is id for string resources
        data class InvalidInput(val reason: Int) : State
    }

    sealed interface Action {
        // message is id fro string resources
        data class ShowError(val message: Int) : Action
        object RouteToMain : Action
        object HideKeyboard : Action
    }

    companion object {
        private const val TAG = "AUTH"
    }

}