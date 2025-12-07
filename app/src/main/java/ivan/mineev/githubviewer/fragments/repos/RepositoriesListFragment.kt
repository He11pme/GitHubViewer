package ivan.mineev.githubviewer.fragments.repos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ivan.mineev.githubviewer.databinding.FragmentRepositoriesListBinding

@AndroidEntryPoint
class RepositoriesListFragment : Fragment() {

    private lateinit var binding: FragmentRepositoriesListBinding

    private val viewModel: RepositoriesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRepositoriesListBinding.inflate(layoutInflater, container, false)

        viewModel.loadRepositories()

        return binding.root
    }

}