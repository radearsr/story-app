package com.storyapp.ui.auth.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.storyapp.BuildConfig
import com.storyapp.R
import com.storyapp.data.ResultState
import com.storyapp.databinding.ActivityRegisterBinding
import com.storyapp.ui.auth.AuthViewModel
import com.storyapp.ui.auth.login.LoginActivity
import com.storyapp.ui.components.DialogInformation
import com.storyapp.ui.welcome.WelcomeActivity
import com.storyapp.utils.validEmail
import com.storyapp.utils.validPassword
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()
        setupView()

        with(binding) {

            btnRegister.setOnClickListener {
                val name = edRegisterName.text.toString()
                val email = edLoginEmail.text.toString()
                val password = edLoginPassword.text.toString()
                registerStart(name, email, password)
            }

            edRegisterName.addTextChangedListener(
                onTextChanged = { _, _, _, _ ->
                    setupButtonStartEnable()
                }
            )

            edLoginEmail.addTextChangedListener(
                onTextChanged = { _, _, _, _ ->
                    setupButtonStartEnable()
                }
            )

            edLoginPassword.addTextChangedListener(
                onTextChanged = { _, _, _, _ ->
                    setupButtonStartEnable()
                }
            )

            fabBack.setOnClickListener {
                val intent = Intent(this@RegisterActivity, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
            }

            tvToLogin.setOnClickListener {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun playAnimation() {
        val imageLogo = ObjectAnimator.ofFloat(binding.ivMainLogo, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)
        val title = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)
        val subtitle = ObjectAnimator.ofFloat(binding.tvSubtitle, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)
        val labelName =
            ObjectAnimator.ofFloat(binding.tvLabelName, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)
        val inputName =
            ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)
        val labelEmail =
            ObjectAnimator.ofFloat(binding.tvLabelEmail, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)
        val inputEmail =
            ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)
        val labelPassword =
            ObjectAnimator.ofFloat(binding.tvLabelPassword, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)
        val inputPassword =
            ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)
        val buttonLogin = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)
        val haveAccount =
            ObjectAnimator.ofFloat(binding.tvHaveAccount, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)
        val toLogin =
            ObjectAnimator.ofFloat(binding.tvToLogin, View.ALPHA, 1f).setDuration(ANIMATION_DURATION)

        AnimatorSet().apply {
            playSequentially(
                imageLogo,
                title,
                subtitle,
                labelName,
                inputName,
                labelEmail,
                inputEmail,
                labelPassword,
                inputPassword,
                buttonLogin,
                haveAccount,
                toLogin
            )
            start()
        }
    }

    private fun setupButtonStartEnable() {
        with(binding) {
            val fullName = edRegisterName.text.toString()
            val email = edLoginEmail.text.toString()
            val password = edLoginPassword.text.toString()
            btnRegister.isEnabled =
                fullName.isNotEmpty() && validEmail(email) && validPassword(password)
        }
    }

    private fun registerStart(name: String, email: String, password: String) {
        authViewModel.registerAction(name, email, password).observe(this) { result ->
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
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
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
                        dialog.setOnButtonClickCallback(object : DialogInformation.OnButtonClickCallback{
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


    private fun setViewLoading(isLoading: Boolean) {
        with(binding.loadingComp.clLoading) {
            visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        binding.btnRegister.isEnabled = !isLoading
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
        private val TAG = RegisterActivity::class.java.simpleName
        private const val ANIMATION_DURATION = 250L
    }
}