package ivan.mineev.githubviewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ivan.mineev.githubviewer.databinding.FragmentAuthBinding

class AuthFragment : Fragment() {

    private lateinit var binding: FragmentAuthBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAuthBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}