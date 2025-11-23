package ivan.mineev.githubviewer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class AuthViewModel : ViewModel() {

    val token = MutableLiveData<String>()
    private val _state = MutableLiveData<State>()
    val state: LiveData<State> get() = _state
    private val _actions = MutableSharedFlow<Action>()
    val actions: Flow<Action> get() = _actions

    fun onSignButtonPressed() {
        token.value.let { currentToken ->

            if (currentToken.isNullOrBlank()) {
                _state.value = State.InvalidInput("Token cannot be empty")
                return
            }

            _state.value = State.Loading

            viewModelScope.launch {
                try {
                    if (checkToken(currentToken)) {
                        _actions.emit(Action.RouteToMain)
                    } else {
                        _actions.emit(Action.ShowError("Invalid Token"))
                        _state.value = State.Idle
                    }
                } catch (e: Exception) {
                    _actions.emit(Action.ShowError("Network error: ${e.message}"))
                    _state.value = State.Idle
                }
            }
        }

    }

    fun checkToken(token: String): Boolean {
        return if (token == "1234") true
        else false
    }

    sealed interface State {
        object Idle : State
        object Loading : State
        data class InvalidInput(val reason: String) : State
    }

    sealed interface Action {
        data class ShowError(val message: String) : Action
        object RouteToMain : Action
    }

}