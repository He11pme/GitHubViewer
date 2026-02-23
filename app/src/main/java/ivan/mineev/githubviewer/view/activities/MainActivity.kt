package ivan.mineev.githubviewer.view.activities

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import ivan.mineev.githubviewer.R
import ivan.mineev.githubviewer.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {

        setSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        initNavHostFragment()
        bindAction()
        setInsets()
        setupViews()

    }

    private fun setupViews() {
        setupAppBar()
    }

    private fun setupAppBar() {
        val appBarConfig =
            AppBarConfiguration(setOf(R.id.authFragment, R.id.repositoriesListFragment))
        binding.toolbar.setupWithNavController(navHostFragment.navController, appBarConfig)

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    viewModel.onLogoutPressed()
                    true
                }

                else -> false
            }
        }
        observeDestinationChanges()
    }

    private fun observeDestinationChanges() {
        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.authFragment -> {
                    binding.appBar.visibility = View.GONE
                }

                R.id.repositoriesListFragment -> {
                    binding.appBar.visibility = View.VISIBLE
                }

                R.id.detailInfoFragment -> {
                    binding.appBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setSplashScreen() {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            viewModel.keepSplashScreen
        }
    }

    private fun setInsets() {
        fun setMainInsets() {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
                insets
            }
        }

        fun setAppBarInsets() {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.appBar)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.updatePadding(top = systemBars.top)
                insets
            }
        }

        setMainInsets()
        setAppBarInsets()
    }

    private fun bindAction() {
        lifecycleScope.launch {
            viewModel.actions.collect(::handleAction)
        }
    }

    private fun handleAction(action: MainActivityViewModel.Action) {
        when (action) {
            MainActivityViewModel.Action.SetAuthAsStart -> {
                setStartFragment(StartFragment.AUTH.ID)
            }

            MainActivityViewModel.Action.SetReposAsStart -> {
                setStartFragment(StartFragment.REPOSITORIES.ID)
            }

            MainActivityViewModel.Action.RouteToAuth -> {
                navigateToAuth()
            }

            is MainActivityViewModel.Action.SetTitleAppBar -> {
                setTitleAppBar(action)
            }
        }
    }

    private fun setTitleAppBar(action: MainActivityViewModel.Action.SetTitleAppBar) {
        binding.toolbar.title = action.title
    }

    private fun navigateToAuth() {
        navHostFragment.navController.navigate(
            R.id.authFragment,
            null,
            NavOptions.Builder()
                .setPopUpTo(R.id.main_nav, inclusive = true)
                .setRestoreState(false)
                .build()
        )
    }

    private fun setStartFragment(fragmentId: Int) {
        navHostFragment.navController.setStartDestination(fragmentId, R.navigation.main_nav)
    }

    private fun initNavHostFragment() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
    }

    enum class StartFragment(val ID: Int) {
        AUTH(R.id.authFragment),
        REPOSITORIES(R.id.repositoriesListFragment)
    }
}

private fun NavController.setStartDestination(fragmentId: Int, graphRes: Int) {
    graph = navInflater.inflate(graphRes).apply {
        setStartDestination(fragmentId)
    }
}