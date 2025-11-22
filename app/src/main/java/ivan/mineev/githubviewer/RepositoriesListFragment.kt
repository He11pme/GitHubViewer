package ivan.mineev.githubviewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ivan.mineev.githubviewer.databinding.FragmentRepositoriesListBinding

class RepositoriesListFragment : Fragment() {

    private lateinit var binding: FragmentRepositoriesListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRepositoriesListBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

}