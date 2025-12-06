package ivan.mineev.githubviewer.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import ivan.mineev.githubviewer.R
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {

        setSplashScreen()

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        initNavHostFragment()
        bindAction()
        setInsets()

    }

    private fun setSplashScreen() {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            viewModel.keepSplashScreen
        }
    }

    private fun setInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun bindAction() {
        lifecycleScope.launch {
            viewModel.actions.collect(::handleAction)
        }
    }

    private fun handleAction(action: MainActivityViewModel.Action) {
        when (action) {
            MainActivityViewModel.Action.RouteToAuth -> {
                setStartFragment(StartFragment.AUTH.ID)
            }

            MainActivityViewModel.Action.RouteToRepositories -> {
                setStartFragment(StartFragment.REPOSITORIES.ID)
            }
        }
    }

    private fun setStartFragment(fragmentId: Int) {
        navHostFragment.navController.setStartDestination(fragmentId, R.navigation.main_nav)
    }

    private fun initNavHostFragment() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
    }


    fun NavController.setStartDestination(fragmentId: Int, graphRes: Int) {
        graph = navInflater.inflate(graphRes).apply {
            setStartDestination(fragmentId)
        }
    }

    enum class StartFragment(val ID: Int) {
        AUTH(R.id.authFragment),
        REPOSITORIES(R.id.repositoriesListFragment)
    }
}