package com.storyapp.ui.welcome

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.storyapp.BuildConfig
import com.storyapp.R
import com.storyapp.data.repository.UserRepository
import com.storyapp.databinding.ActivitySplashScreenBinding
import com.storyapp.ui.UserViewModelFactory
import com.storyapp.ui.main.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private val viewModel by viewModels<WelcomeViewModel> {
        UserViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()

        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.currentSession().observe(this) { session ->
                if (BuildConfig.DEBUG) Log.d("SESSION", "CURRENT SESSION $session")
                if (!session.isLogin) {
                    val intent = Intent(this@SplashScreenActivity, WelcomeActivity::class.java)
                    startActivity(intent)
                    finish()
                    return@observe
                }
                val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, DELAY_SCREEN)
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    companion object {
        const val DELAY_SCREEN = 3000L
    }
}