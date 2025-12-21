package ivan.mineev.githubviewer.managers

import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

@ActivityRetainedScoped
class AppBarManager @Inject constructor() {

    private val _action = MutableSharedFlow<AppBarAction>()
    val action: Flow<AppBarAction> = _action

    suspend fun setTitleAppBar(title: String) {
        _action.emit(AppBarAction.SetTitle(title))
    }

    sealed interface AppBarAction {
        data class SetTitle(val title: String) : AppBarAction
    }

}