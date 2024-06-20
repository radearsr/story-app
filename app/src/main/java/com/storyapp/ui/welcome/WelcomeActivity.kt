package com.storyapp.ui.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.storyapp.databinding.ActivityWelcomeBinding
import com.storyapp.ui.auth.login.LoginActivity
import com.storyapp.ui.auth.register.RegisterActivity

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        playAnimation()

        binding.btnToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun playAnimation() {
        val welcomeImageScaleX = ObjectAnimator.ofFloat(binding.ivWelcomeIllustration, View.SCALE_X, 0.5f, 1.0f)
        val welcomeImageScaleY = ObjectAnimator.ofFloat(binding.ivWelcomeIllustration, View.SCALE_Y, 0.5f, 1.0f)
        val welcomeImageAnimatorSet = AnimatorSet().apply {
            playTogether(welcomeImageScaleX, welcomeImageScaleY)
            duration = 800
        }
        welcomeImageAnimatorSet.start()


        val logoApp = ObjectAnimator.ofFloat(binding.ivLogoApp, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)
        val titleApp = ObjectAnimator.ofFloat(binding.tvTitleApp, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)
        val subTitleApp = ObjectAnimator.ofFloat(binding.tvSubtitleApp, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)
        val titleWelcome = ObjectAnimator.ofFloat(binding.tvWelcomeTitle, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)
        val descriptionWelcome = ObjectAnimator.ofFloat(binding.tvWelcomeDescription, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)
        val buttonLogin = ObjectAnimator.ofFloat(binding.btnToLogin, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)
        val buttonRegister = ObjectAnimator.ofFloat(binding.btnToRegister, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)
        val fadeAnimatorSet =  AnimatorSet().apply {
            playSequentially(logoApp, titleApp, subTitleApp, titleWelcome, descriptionWelcome, buttonLogin, buttonRegister)
        }
        fadeAnimatorSet.start()


        val logoAppScaleX = ObjectAnimator.ofFloat(binding.ivLogoApp, View.SCALE_X, 0.9f, 1.0f).apply {
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }
        val logoAppScaleY = ObjectAnimator.ofFloat(binding.ivLogoApp, View.SCALE_Y, 0.9f, 1.0f).apply {
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }
        val logoAppAnimatorSet = AnimatorSet().apply {
            playTogether(logoAppScaleX, logoAppScaleY)
            duration = 2000
        }
        logoAppAnimatorSet.start()
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
        private const val ANIMATION_DURATION = 300L
    }
}