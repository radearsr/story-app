package com.storyapp.ui.welcome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.storyapp.R
import com.storyapp.databinding.ActivityWelcomeBinding
import com.storyapp.ui.ViewModelFactory
import com.storyapp.ui.auth.login.LoginActivity
import com.storyapp.ui.auth.register.RegisterActivity
import com.storyapp.ui.main.MainActivity

class WelcomeActivity : AppCompatActivity() {

    private val viewModel by viewModels<WelcomeViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.currentSession().observe(this) {
            Log.e("Welcome", "USER $it")
            if (it.isLogin) {
                val intent = Intent(this@WelcomeActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.btnToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}