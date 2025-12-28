package ivan.mineev.githubviewer.fragments.repos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ivan.mineev.githubviewer.R
import ivan.mineev.githubviewer.databinding.FragmentRepositoriesListBinding
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RepositoriesListFragment : Fragment() {

    private lateinit var binding: FragmentRepositoriesListBinding

    private val viewModel: RepositoriesViewModel by viewModels()

    private val adapter = ReposAdapter(::openRepoDetails)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRepositoriesListBinding.inflate(layoutInflater, container, false)

        setupViews()
        bindToViewModel()
        initLoadRepositories()

        return binding.root
    }

    private fun initLoadRepositories() {
        viewModel.loadRepositories()
    }

    private fun setupViews() {
        setupRecyclerRepos()
        setupRefreshLayout()
    }

    private fun setupRecyclerRepos() {
        binding.apply {
            recyclerRepositories.adapter = adapter
            recyclerRepositories.itemAnimator = SlideInDownAnimator()
        }
    }

    private fun setupRefreshLayout() {
        binding.refreshLayout.setOnRefreshListener {
            viewModel.reloadRepositories()
        }
    }

    private fun bindToViewModel() {
        bindState()
        bindAction()
    }

    private fun bindState() {
        viewModel.state.observe(viewLifecycleOwner, ::handleState)
    }

    private fun bindAction() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.actions.collect(::handleAction)
        }
    }

    private fun handleState(state: RepositoriesViewModel.State) {
        renderRepositories(state)
        renderAnimation(state)
        renderDescriptionAnimation(state)
        renderRefreshLayout(state)
    }

    private fun renderRefreshLayout(state: RepositoriesViewModel.State) {
        binding.refreshLayout.isRefreshing = state is RepositoriesViewModel.State.Reloading
    }

    private fun handleAction(action: RepositoriesViewModel.Action) {
        when (action) {
            is RepositoriesViewModel.Action.ForceLogout -> showError(action.message)
        }
    }

    private fun renderDescriptionAnimation(state: RepositoriesViewModel.State) {
        if (state is RepositoriesViewModel.State.Error) {
            binding.descriptionAnimation.apply {
                visibility = View.VISIBLE
                text = getString(state.error)
            }
        }
        if (state is RepositoriesViewModel.State.Loading) {
            binding.descriptionAnimation.apply {
                visibility = View.VISIBLE
                text = getString(R.string.loading)
            }
        }
        if (state is RepositoriesViewModel.State.Empty) {
            binding.descriptionAnimation.apply {
                visibility = View.VISIBLE
                text = getString(R.string.empty_list)
            }
        }
        if (state is RepositoriesViewModel.State.Loaded) {
            binding.descriptionAnimation.visibility = View.GONE
        }
    }

    private fun renderRepositories(state: RepositoriesViewModel.State) {
        if (state is RepositoriesViewModel.State.Loaded) submitList(state)
    }

    private fun submitList(state: RepositoriesViewModel.State.Loaded) {
        adapter.submitList(state.repos)
    }

    private fun renderAnimation(state: RepositoriesViewModel.State) {
        if (state is RepositoriesViewModel.State.Loading) {
            setupLoadingAnimation()
            binding.animationViewRepos.playAnimation()
        }
        if (state is RepositoriesViewModel.State.Empty) {
            setupEmptyListAnimation()
            binding.animationViewRepos.playAnimation()
        }
        if (state is RepositoriesViewModel.State.Loaded) {
            binding.animationViewRepos.cancelAnimation()
            binding.animationViewRepos.visibility = View.GONE
        }
        if (state is RepositoriesViewModel.State.Error) {
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

    private fun openRepoDetails(name: String) {
        val bundle = Bundle().apply { putString("nameRepo", name) }
        binding.root.findNavController()
            .navigate(R.id.navigateFromRepositoriesListFragmentToDetailInfoFragment, bundle)
    }

}