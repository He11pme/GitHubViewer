package ivan.mineev.githubviewer.utils

import ivan.mineev.githubviewer.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

class SessionManager @Inject constructor(private val appRepository: AppRepository) {

    private val _action = MutableSharedFlow<SessionAction>()

    val action: Flow<SessionAction> = _action

    suspend fun logout() {
        appRepository.logout()
        _action.emit(SessionAction.Logout)
    }

    sealed interface SessionAction {
        object Logout : SessionAction
    }

}