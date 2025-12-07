package ivan.mineev.githubviewer.fragments.repos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ivan.mineev.githubviewer.repository.AppRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepositoriesViewModel @Inject constructor(
    val appRepository: AppRepository
) : ViewModel() {

    fun loadRepositories() {
        viewModelScope.launch {
            appRepository.loadRepositories()
        }
    }

}