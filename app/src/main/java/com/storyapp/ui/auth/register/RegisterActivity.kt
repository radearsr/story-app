package com.storyapp.ui.auth.register

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
import com.storyapp.data.ResultState
import com.storyapp.databinding.ActivityRegisterBinding
import com.storyapp.ui.UserViewModelFactory
import com.storyapp.ui.auth.AuthViewModel
import com.storyapp.ui.auth.login.LoginActivity
import com.storyapp.ui.components.DialogInformation
import com.storyapp.ui.main.MainActivity
import com.storyapp.utils.validEmail
import com.storyapp.utils.validPassword

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authViewModel by viewModels<AuthViewModel> {
        UserViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

        with(binding) {

            btnRegister.setOnClickListener {
                val name = edRegisterName.text.toString()
                val email = edLoginEmail.text.toString()
                val password = edLoginPassword.text.toString()
                registerStart(name, email, password)
            }

            edRegisterName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setupButtonStartEnable()
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            edLoginEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setupButtonStartEnable()
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            edLoginPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setupButtonStartEnable()
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            fabBack.setOnClickListener {
                finish()
            }

            tvToLogin.setOnClickListener {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupButtonStartEnable() {
        with(binding) {
            val fullName = edRegisterName.text.toString()
            val email = edLoginEmail.text.toString()
            val password = edLoginPassword.text.toString()
            btnRegister.isEnabled = fullName.isNotEmpty() && validEmail(email) && validPassword(password)
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
                        showDialog("ERROR", result.error, false)
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

    private fun showDialog(title: String, message: String, cancelable: Boolean) {
        val dialog = DialogInformation(this, title, message, cancelable)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.show()
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
    }
}