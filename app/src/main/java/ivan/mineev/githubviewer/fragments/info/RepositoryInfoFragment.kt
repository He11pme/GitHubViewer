package ivan.mineev.githubviewer.fragments.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ivan.mineev.githubviewer.R
import ivan.mineev.githubviewer.databinding.FragmentDetailInfoBinding
import ivan.mineev.githubviewer.model.RepoDetails
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RepositoryInfoFragment : Fragment() {

    private lateinit var binding: FragmentDetailInfoBinding
    private val viewModel: RepositoryInfoViewModel by viewModels()

    var repoNameProvided: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDetailInfoBinding.inflate(layoutInflater, container, false)

        getArgs()
        bindToViewModel()
        initLoadRepo(repoNameProvided)

        return binding.root
    }

    private fun getArgs() {
        repoNameProvided =
            arguments?.getString("nameRepo") ?: throw Exception("Not bundle id repository")
    }

    private fun bindToViewModel() {
        bindState()
        bindAction()
    }

    private fun initLoadRepo(repoName: String) = viewModel.loadRepo(repoName)


    private fun bindState() {
        viewModel.state.observe(viewLifecycleOwner, ::handleState)
    }

    private fun bindAction() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.actions.collect(::handleAction)
        }
    }

    private fun handleState(state: RepositoryInfoViewModel.State) {
        renderDataAboutRepo(state)
        renderReadme(state)
        renderDescriptionAnimation(state)
        renderAnimation(state)
    }

    private fun handleAction(action: RepositoryInfoViewModel.Action) {
        when (action) {
            is RepositoryInfoViewModel.Action.ForceLogout -> showError(action.message)
        }
    }

    private fun renderDataAboutRepo(state: RepositoryInfoViewModel.State) {
        if (state is RepositoryInfoViewModel.State.Loaded) {
            setDataAboutRepo(state.gitHubRepo)
        }
        binding.groupDataRepo.visibility =
            if (state is RepositoryInfoViewModel.State.Loaded) View.VISIBLE else View.GONE
    }

    private fun setDataAboutRepo(repo: RepoDetails) {
        binding.apply {
            linkView.label = repo.url
            licenceTV.text = repo.license?.name ?: "not found"
            starsCounter.count = repo.stars.toString()
            forksCounter.count = repo.forks.toString()
            watchersCounter.count = repo.watchers.toString()
        }
    }

    private fun renderReadme(state: RepositoryInfoViewModel.State) {
        if (state is RepositoryInfoViewModel.State.Loaded) {
            if (state.readmeState != null) setReadme(state.readmeState)
        }
    }

    private fun setReadme(readmeState: RepositoryInfoViewModel.ReadmeState) {
        binding.readmeView.text = if (readmeState is RepositoryInfoViewModel.ReadmeState.Loaded) readmeState.markdown else ""
    }

    private fun renderDescriptionAnimation(state: RepositoryInfoViewModel.State) {
        binding.descriptionAnimation.apply {
            visibility = when(state) {
                is RepositoryInfoViewModel.State.Loaded -> {
                    when(state.readmeState) {
                        is RepositoryInfoViewModel.ReadmeState.Loaded -> View.GONE
                        else -> View.VISIBLE
                    }
                }
                else -> View.VISIBLE
            }
            text = when(state) {
                is RepositoryInfoViewModel.State.Error -> getString(state.error)
                is RepositoryInfoViewModel.State.Loaded -> {
                    when(state.readmeState) {
                        RepositoryInfoViewModel.ReadmeState.Empty -> getString(R.string.readme_not_found)
                        is RepositoryInfoViewModel.ReadmeState.Error -> getString(state.readmeState.error)
                        RepositoryInfoViewModel.ReadmeState.Loading -> getString(R.string.load_readme)
                        else -> ""
                    }
                }
                RepositoryInfoViewModel.State.Loading -> getString(R.string.loading)
            }
        }
    }

    private fun renderAnimation(state: RepositoryInfoViewModel.State) {
        if (state is RepositoryInfoViewModel.State.Loading) {
            setupLoadingAnimation()
            binding.animationViewRepos.playAnimation()
        }
        if (state is RepositoryInfoViewModel.State.Loaded) {
            if (state.readmeState is RepositoryInfoViewModel.ReadmeState.Empty) {
                setupEmptyListAnimation()
                binding.animationViewRepos.playAnimation()
            } else {
                binding.animationViewRepos.cancelAnimation()
                binding.animationViewRepos.visibility = View.GONE
            }
        }
        if (state is RepositoryInfoViewModel.State.Error) {
            setupErrorAnimation()
            binding.animationViewRepos.playAnimation()
        }
    }

    private fun setupErrorAnimation() {
        setupBasicAnimation()
        binding.animationViewRepos.repeatCount = 0
        setAnimation(R.raw.reject_animation_state)
    }

    private fun setupLoadingAnimation() {
        setupBasicAnimation()
        setupInfiniteAnimation()
        setAnimation(R.raw.loading_animation)
    }

    private fun setupEmptyListAnimation() {
        setupBasicAnimation()
        setupInfiniteAnimation()
        setAnimation(R.raw.empty_list_animation)
    }

    private fun setupInfiniteAnimation() {
        binding.animationViewRepos.repeatCount = LottieDrawable.INFINITE
    }

    private fun setupBasicAnimation() {
        binding.animationViewRepos.visibility = View.VISIBLE
    }

    private fun setAnimation(animationId: Int) {
        binding.animationViewRepos.setAnimation(animationId)
    }

    private fun showError(resId: Int) {
        showSnackbar(getString(resId))
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }


}