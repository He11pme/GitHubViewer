package ivan.mineev.githubviewer.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import ivan.mineev.githubviewer.data.repository.AppRepositoryImpl
import ivan.mineev.githubviewer.domain.repository.AppRepository

@Module
@InstallIn(ActivityRetainedComponent::class)
interface RepositoryModule {

    @Binds
    @ActivityRetainedScoped
    fun bindAppRepository(impl: AppRepositoryImpl): AppRepository

}