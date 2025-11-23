package ivan.mineev.githubviewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import ivan.mineev.githubviewer.databinding.FragmentAuthBinding

class AuthFragment : Fragment() {

    private lateinit var binding: FragmentAuthBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAuthBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding.viewModel = viewModel

        binding.signInButton.setOnClickListener { viewModel.onSignButtonPressed() }

        viewModel.state.observe(viewLifecycleOwner) { state ->
//            binding.button.visibility = if (state == AuthViewModel.State.Loading) View.GONE else View.VISIBLE
            binding.tokenInputLayout.error = if (state is AuthViewModel.State.InvalidInput) state.reason else null
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.actions.collect { action ->
                when(action) {
                    AuthViewModel.Action.RouteToMain -> binding.root.findNavController().navigate(R.id.navigateFromAuthFragmentToRepositoriesListFragment)
                    is AuthViewModel.Action.ShowError -> {
                        Snackbar.make(binding.root, action.message, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return binding.root
    }

}