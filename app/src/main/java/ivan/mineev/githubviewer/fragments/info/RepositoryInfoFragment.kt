package ivan.mineev.githubviewer.fragments.info

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ivan.mineev.githubviewer.databinding.FragmentDetailInfoBinding

@AndroidEntryPoint
class RepositoryInfoFragment : Fragment() {

    private lateinit var binding: FragmentDetailInfoBinding
    private val viewModel: RepositoryInfoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDetailInfoBinding.inflate(layoutInflater, container, false)

        val repoName = arguments?.getString("nameRepo") ?: throw Exception()
        viewModel.loadRepo(repoName)

        bindToViewModel()

        return binding.root
    }

    private fun bindToViewModel() {
        bindState()
    }

    private fun bindState() {
        viewModel.state.observe(viewLifecycleOwner, ::handleState)
    }

    private fun handleState(state: RepositoryInfoViewModel.State) {
        if (state is RepositoryInfoViewModel.State.Loaded) Log.d("DETAIL","${state.gitHubRepo}")
    }

}