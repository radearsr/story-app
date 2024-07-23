package com.storyapp.ui.main

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.storyapp.BuildConfig
import com.storyapp.R
import com.storyapp.data.ResultState
import com.storyapp.data.remote.response.ListStoryItem
import com.storyapp.databinding.ActivityMainBinding
import com.storyapp.ui.StoryViewModelFactory
import com.storyapp.ui.components.DialogConfirmation
import com.storyapp.ui.components.DialogInformation
import com.storyapp.ui.main.create.CreateStoryActivity
import com.storyapp.ui.main.maps.MapsActivity
import com.storyapp.ui.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        StoryViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabCreateStory.setOnClickListener {
            val intent = Intent(this@MainActivity, CreateStoryActivity::class.java)
            startActivity(intent)
        }

        binding.rvListStory.layoutManager = LinearLayoutManager(this)

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    showDialogConfirmation(
                        getString(R.string.txt_confirm),
                        getString(R.string.txt_message_logout),
                        getString(R.string.txt_logout),
                        getString(R.string.txt_close)
                    )
                }
                R.id.action_maps -> {
                    val intent = Intent(this@MainActivity, MapsActivity::class.java)
                    startActivity(intent)
                }
            }
            return@setOnMenuItemClickListener true
        }
    }

    override fun onResume() {
        super.onResume()
        retrieveData()
    }

    private fun retrieveData() {
        val adapter = StoryAdapter()
        binding.rvListStory.adapter = adapter
        viewModel.stories.observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    private fun showDialogConfirmation(
        title: String,
        message: String,
        positiveString: String,
        negativeString: String
    ) {
        val dialog = DialogConfirmation(
            this@MainActivity, title, message,
            false,
            positiveString,
            negativeString
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setOnButtonClickCallback(object :
            DialogConfirmation.OnButtonClickCallback {
            override fun onButtonPositive(dialog: Dialog) {
                viewModel.logoutSession()
                val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun onButtonNegative(dialog: Dialog) {
                dialog.dismiss()
            }
        })
        dialog.show()
    }


    private fun setViewError(message: String) {
        with(binding.errorComp) {
            clError.visibility = View.VISIBLE
            tvErrorMessage.text = message
        }
    }

    private fun setViewLoading(isLoading: Boolean) {
        binding.errorComp.clError.visibility = View.GONE
        with(binding.loadingComp.clLoading) {
            visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}