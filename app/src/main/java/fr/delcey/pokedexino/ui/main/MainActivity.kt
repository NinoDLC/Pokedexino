package fr.delcey.pokedexino.ui.main

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionManager
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import fr.delcey.pokedexino.R
import fr.delcey.pokedexino.databinding.MainActivityBinding
import fr.delcey.pokedexino.databinding.MainDrawerHeaderBinding
import fr.delcey.pokedexino.ui.utils.loadImageUrl
import fr.delcey.pokedexino.ui.utils.setTextElseGone
import fr.delcey.pokedexino.ui.utils.viewBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding { MainActivityBinding.inflate(it) }
    private val viewModel by viewModels<MainViewModel>()
    private val appBarConfiguration: AppBarConfiguration by lazy {
        AppBarConfiguration(
            setOf(R.id.navigation_fragment_pokemon_list, R.id.navigation_fragment_pokemon_detail),
            binding.mainDrawerLayout
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        setSupportActionBar(binding.mainToolbar)

        val headerBinding = MainDrawerHeaderBinding.bind(binding.mainNavigationView.getHeaderView(0))
        headerBinding.mainHeaderButtonLogin.setOnClickListener {
            viewModel.onConnectButtonClicked()
        }
        headerBinding.mainHeaderButtonLogout.setOnClickListener {
            viewModel.onDisconnectButtonClicked()
        }

        binding.mainFloatingActionButton.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show()
        }

        viewModel.viewActionLiveEvent.observe(this) {
            when (it) {
                is MainViewAction.Toast -> Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                is MainViewAction.NavigateForResult -> startActivityForResult(it.intent, it.requestCode)
            }
        }

        viewModel.viewStateLiveData.observe(this) {
            if (it.animateHeaderChange) {
                TransitionManager.beginDelayedTransition(headerBinding.root)
            }
            headerBinding.mainHeaderButtonLogin.isVisible = it.isLoginButtonVisible
            headerBinding.mainHeaderButtonLogout.isVisible = it.isLogoutButtonVisible
            headerBinding.mainHeaderCircularProgressIndicatorConnect.isVisible = it.isLoadingVisible
            headerBinding.mainHeaderImageViewUserPhoto.loadImageUrl(it.avatarUrl, circleCrop = true)
            headerBinding.mainHeaderTextViewUserName.setTextElseGone(it.userName)
            headerBinding.mainHeaderTextViewUserEmail.setTextElseGone(it.userEmail)
        }

        val navController = findNavController(R.id.main_navHostFragment)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.mainNavigationView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_toolbar, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean = findNavController(R.id.main_navHostFragment).navigateUp(appBarConfiguration)
        || super.onSupportNavigateUp()

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        viewModel.onActivityResult(requestCode, data)
    }
}