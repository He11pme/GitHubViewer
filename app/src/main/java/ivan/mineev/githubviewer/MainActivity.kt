package ivan.mineev.githubviewer

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var keyValueStorage: KeyValueStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPref = getPreferences(MODE_PRIVATE)

        keyValueStorage.saveToken(sharedPref.getString(getString(R.string.saved_token), null))

        keyValueStorage.authToken.observe(this) { token ->
            with(sharedPref.edit()) {
                putString(getString(R.string.saved_token), token)
                apply()
            }
        }

    }
}