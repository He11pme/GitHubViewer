package ivan.mineev.githubviewer.fragments.auth

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ivan.mineev.githubviewer.R
import ivan.mineev.githubviewer.databinding.FragmentAuthBinding
import ivan.mineev.githubviewer.utils.AnimatorListenerAdapter
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private lateinit var binding: FragmentAuthBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAuthBinding.inflate(layoutInflater, container, false)

        binding.viewModel = viewModel

        bindToViewModel()
        setInsets()
        setupViews()

        return binding.root
    }

    private fun bindToViewModel() {
        bindState()
        bindAction()
    }

    private fun setInsets() {
        setInsetsSignInButton()
    }

    private fun setInsetsSignInButton() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.signInButtonContainer) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            v.setPadding(0, 0, 0, imeInsets.bottom)
            insets
        }
    }

    private fun setupViews() {
        setupTokenInput()
    }

    private fun setupTokenInput() {
        binding.tokenInputEditText.setOnEditorActionListener { _, actionId, _ ->
            handleEditorAction(actionId)
        }
    }

    private fun handleEditorAction(actionId: Int): Boolean =
        if (actionId == EditorInfo.IME_ACTION_DONE) enterPressed()
        else false

    private fun enterPressed(): Boolean {
        viewModel.onSignButtonPressed()
        return true
    }

    private fun bindState() {
        viewModel.state.observe(viewLifecycleOwner, ::handleState)
    }

    private fun handleState(state: AuthViewModel.State) {
        renderAnimation(state)
        renderTokenField(state)
        renderButtonState(state)
    }

    private fun renderAnimation(state: AuthViewModel.State) {
        if (state is AuthViewModel.State.Loading) {
            setupLoadingAnimation()
            binding.animationView.playAnimation()
        }
    }

    private fun renderTokenField(state: AuthViewModel.State) {
        binding.tokenInputLayout.error =
            if (state is AuthViewModel.State.InvalidInput) getString(state.reason) else null
    }

    private fun renderButtonState(state: AuthViewModel.State) {
        binding.signInButton.isEnabled = state !is AuthViewModel.State.Loading
        binding.signInButton.text =
            if (state is AuthViewModel.State.Loading) getString(R.string.loading)
            else getString(R.string.sign_in)
    }

    private fun bindAction() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.actions.collect(::handleAction)
        }
    }

    private fun handleAction(action: AuthViewModel.Action) {
        when (action) {
            AuthViewModel.Action.RouteToMain -> playResultAnimation(doEnd = ::goToNextFragment)

            is AuthViewModel.Action.ShowError -> {
                playRejectAnimation()
                showError(action.message)
            }

            AuthViewModel.Action.HideKeyboard -> hideKeyboard()
        }
    }

    private fun playRejectAnimation() {
        setupResultAnimation(R.raw.reject_animation)
        binding.animationView.playAnimation()
    }

    private fun playResultAnimation(doEnd: () -> Unit) {
        setupResultAnimation(R.raw.approved_animation, doEnd)
        binding.animationView.playAnimation()
    }

    private fun setupResultAnimation(animationId: Int, doEnd: (() -> Unit)? = null) {
        binding.animationView.apply {
            repeatCount = 0
            setAnimation(animationId)
            addAnimatorListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    visibility = View.GONE
                    removeAnimatorListener(this)
                    doEnd?.invoke()
                }
            })
        }
    }

    private fun setupLoadingAnimation() {
        binding.animationView.apply {
            visibility = View.VISIBLE
            repeatCount = LottieDrawable.INFINITE
            setAnimation(R.raw.loading_animation)
        }
    }

    private fun showError(resId: Int) {
        showSnackbar(getString(resId))
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun goToNextFragment() {
        binding.root.findNavController()
            .navigate(R.id.navigateFromAuthFragmentToRepositoriesListFragment)
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

}

