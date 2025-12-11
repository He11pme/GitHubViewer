package ivan.mineev.githubviewer.fragments.repos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.airbnb.lottie.LottieDrawable
import dagger.hilt.android.AndroidEntryPoint
import ivan.mineev.githubviewer.R
import ivan.mineev.githubviewer.databinding.FragmentRepositoriesListBinding
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator

@AndroidEntryPoint
class RepositoriesListFragment : Fragment() {

    private lateinit var binding: FragmentRepositoriesListBinding

    private val viewModel: RepositoriesViewModel by viewModels()

    private val adapter = ReposAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRepositoriesListBinding.inflate(layoutInflater, container, false)

        setupViews()
        bindToViewModel()

        viewModel.loadRepositories()

        return binding.root
    }

    private fun setupViews() {
        setupRecyclerRepos()
        setupAnimationView()
    }

    private fun setupRecyclerRepos() {
        binding.apply {
            recyclerRepositories.adapter = adapter
            recyclerRepositories.itemAnimator = SlideInDownAnimator()
        }
    }

    private fun bindToViewModel() {
        bindState()
    }

    private fun bindState() {
        viewModel.state.observe(viewLifecycleOwner, ::handleState)
    }

    private fun handleState(state: RepositoriesViewModel.State) {
        renderRepositories(state)
        renderAnimation(state)
    }

    private fun renderRepositories(state: RepositoriesViewModel.State) {
        if (state is RepositoriesViewModel.State.Loaded) submitList(state)
    }

    private fun submitList(state: RepositoriesViewModel.State.Loaded) {
        adapter.submitList(state.repos)
    }

    private fun renderAnimation(state: RepositoriesViewModel.State) {
        if (state is RepositoriesViewModel.State.Loading) {
            setupAnimation(R.raw.loading_list_animation)
            binding.animationViewRepos.playAnimation()
        }
        if (state is RepositoriesViewModel.State.Empty) {
            setupAnimation(R.raw.empty_list_animation)
            binding.animationViewRepos.playAnimation()
        }
        if (state is RepositoriesViewModel.State.Loaded) {
            binding.animationViewRepos.cancelAnimation()
            binding.animationViewRepos.visibility = View.GONE
        }
    }

    private fun setupAnimationView() {
        binding.animationViewRepos.apply {
            visibility = View.VISIBLE
            repeatCount = LottieDrawable.INFINITE
        }
    }

    private fun setupAnimation(animationId: Int) {
        binding.animationViewRepos.setAnimation(animationId)
    }

}