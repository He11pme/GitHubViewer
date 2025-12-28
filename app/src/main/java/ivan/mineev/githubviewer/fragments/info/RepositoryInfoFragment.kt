package ivan.mineev.githubviewer.fragments.info

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import ivan.mineev.githubviewer.R
import ivan.mineev.githubviewer.databinding.FragmentDetailInfoBinding
import ivan.mineev.githubviewer.model.RepoDetails
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RepositoryInfoFragment : Fragment() {

    private lateinit var binding: FragmentDetailInfoBinding
    private val viewModel: RepositoryInfoViewModel by viewModels()

    private val markwon by lazy { Markwon.create(requireContext()) }

    var repoNameProvided: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDetailInfoBinding.inflate(layoutInflater, container, false)

        getArgs()
        setTitleAppBar(repoNameProvided)
        bindToViewModel()
        initLoadRepo(repoNameProvided)
        setupViews()

        return binding.root
    }

    private fun getArgs() {
        repoNameProvided =
            arguments?.getString("nameRepo")
                ?: throw RuntimeException("Repository name not provided")
    }

    private fun setTitleAppBar(repoName: String) {
        viewModel.setTitleAppBar(repoName)
    }

    private fun bindToViewModel() {
        bindState()
        bindAction()
    }

    private fun initLoadRepo(repoName: String) = viewModel.loadRepo(repoName)


    private fun bindState() {
        viewModel.state.observe(viewLifecycleOwner, ::handleState)
    }

    private fun setupViews() {
        setupLinkView()
    }

    private fun setupLinkView() {
        binding.linkView.setOnClickListener { viewModel.onLinkPressed() }
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
            is RepositoryInfoViewModel.Action.OpenLink -> openLink(action.link)
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
            licenceTV.text = repo.license?.name ?: getString(R.string.not_found)
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
        if (readmeState is RepositoryInfoViewModel.ReadmeState.Loaded)
            markwon.setMarkdown(binding.readmeView, readmeState.markdown)
    }

    private fun renderDescriptionAnimation(state: RepositoryInfoViewModel.State) {
        binding.descriptionAnimation.apply {
            visibility = when (state) {
                is RepositoryInfoViewModel.State.Loaded -> {
                    when (state.readmeState) {
                        is RepositoryInfoViewModel.ReadmeState.Loaded -> View.GONE
                        else -> View.VISIBLE
                    }
                }

                else -> View.VISIBLE
            }
            text = when (state) {
                is RepositoryInfoViewModel.State.Error -> getString(state.error)
                is RepositoryInfoViewModel.State.Loaded -> {
                    when (state.readmeState) {
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
            when (state.readmeState) {
                RepositoryInfoViewModel.ReadmeState.Empty -> {
                    setupEmptyListAnimation()
                    binding.animationViewRepos.playAnimation()
                }

                is RepositoryInfoViewModel.ReadmeState.Error -> {
                    setupErrorAnimation()
                    binding.animationViewRepos.playAnimation()
                }

                is RepositoryInfoViewModel.ReadmeState.Loaded -> {
                    binding.animationViewRepos.cancelAnimation()
                    binding.animationViewRepos.visibility = View.GONE
                }

                else -> {}
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

    private fun openLink(link: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = link.toUri()
        startActivity(intent)
    }

    private fun showError(resId: Int) {
        showSnackbar(getString(resId))
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

}