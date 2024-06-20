package com.storyapp.ui.auth.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.storyapp.BuildConfig
import com.storyapp.R
import com.storyapp.data.ResultState
import com.storyapp.databinding.ActivityLoginBinding
import com.storyapp.ui.UserViewModelFactory
import com.storyapp.ui.auth.AuthViewModel
import com.storyapp.ui.auth.register.RegisterActivity
import com.storyapp.ui.components.DialogInformation
import com.storyapp.ui.main.MainActivity
import com.storyapp.ui.welcome.WelcomeActivity
import com.storyapp.utils.validEmail
import com.storyapp.utils.validPassword

class LoginActivity : AppCompatActivity() {

    private val authViewModel by viewModels<AuthViewModel> {
        UserViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()
        setupView()
        setupButtonStartEnable()

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text
            val password = binding.edLoginPassword.text
            startLogin(email.toString(), password.toString())
        }

        binding.edLoginEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setupButtonStartEnable()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.edLoginPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setupButtonStartEnable()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.fabBack.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.tvToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun playAnimation() {
        val imageLogo = ObjectAnimator.ofFloat(binding.ivMainLogo, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)
        val title = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)
        val subtitle = ObjectAnimator.ofFloat(binding.tvSubtitle, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)
        val labelEmail =
            ObjectAnimator.ofFloat(binding.tvLabelEmail, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)
        val inputEmail =
            ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)
        val labelPassword =
            ObjectAnimator.ofFloat(binding.tvLabelPassword, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)
        val inputPassword =
            ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)
        val buttonLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)
        val haveAccount =
            ObjectAnimator.ofFloat(binding.tvHaveAccount, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)
        val toRegister =
            ObjectAnimator.ofFloat(binding.tvToRegister, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)

        AnimatorSet().apply {
            playSequentially(
                imageLogo,
                title,
                subtitle,
                labelEmail,
                inputEmail,
                labelPassword,
                inputPassword,
                buttonLogin,
                haveAccount,
                toRegister
            )
            start()
        }
    }

    private fun setupButtonStartEnable() {
        val email = binding.edLoginEmail.text.toString()
        val password = binding.edLoginPassword.text.toString()
        binding.btnLogin.isEnabled = validEmail(email) && validPassword(password)
    }

    private fun startLogin(email: String, password: String) {
        authViewModel.loginAction(email, password).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> {
                        setViewLoading(true)
                    }

                    is ResultState.Success -> {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "Success State ${result.data}")
                        }
                        setViewLoading(false)
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    is ResultState.Error -> {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, "Error State ${result.error}")
                        }
                        val dialog = DialogInformation(
                            this,
                            getString(R.string.txt_error),
                            result.error,
                            getString(R.string.txt_close),
                            true
                        )
                        dialog.setOnButtonClickCallback(object :
                            DialogInformation.OnButtonClickCallback {
                            override fun onButtonClose(dialog: Dialog) {
                                dialog.dismiss()
                            }
                        })
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.show()
                        setViewLoading(false)
                    }
                }
            }
        }
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

    private fun setViewLoading(isLoading: Boolean) {
        with(binding.loadingComp.clLoading) {
            visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        binding.btnLogin.isEnabled = !isLoading
    }


    companion object {
        private val TAG = LoginActivity::class.java.simpleName
        private const val ANIMATION_DURATION = 250L
    }
}