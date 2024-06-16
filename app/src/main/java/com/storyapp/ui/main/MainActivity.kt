package com.storyapp.ui.main

import android.app.Dialog
import android.content.Intent
import android.os.Build
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
import com.storyapp.ui.main.detail.DetailStoryActivity
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
            }
            return@setOnMenuItemClickListener true
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getStories().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> {
                        setViewLoading(true)
                    }

                    is ResultState.Success -> {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "State Success ${result.data}")
                        }
                        setViewLoading(false)
                        setupRecyclerView(result.data)
                    }

                    is ResultState.Error -> {
                        setViewLoading(false)
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, "State Error ${result.error}")
                        }
                        when (result.error.toInt()) {
                            401 -> {
                                showDialogConfirmation(
                                    getString(R.string.txt_alert),
                                    getString(R.string.txt_unauthorize),
                                    getString(R.string.txt_login),
                                    getString(R.string.txt_close)
                                )
                                setViewError(getString(R.string.txt_unauthorize))
                            }

                            404 -> {
                                setViewError(getString(R.string.txt_not_found))
                            }

                            else -> {
                                setViewError(getString(R.string.txt_server_error))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showDialogConfirmation(
        title: String,
        message: String,
        yesString: String,
        noString: String
    ) {
        val dialog = DialogConfirmation(
            this@MainActivity, title, message,
            false,
            yesString,
            noString
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setOnButtonClickCallback(object :
            DialogConfirmation.OnButtonClickCallback {
            override fun onButtonYes(dialog: Dialog) {
                viewModel.logoutSession()
                val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun onButtonNo(dialog: Dialog) {
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

    private fun setupRecyclerView(stories: List<ListStoryItem>) {
        binding.rvListStory.layoutManager = LinearLayoutManager(this)
        val storyAdapter = StoryAdapter(stories)
        binding.rvListStory.adapter = storyAdapter

        storyAdapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(storyId: String) {
                val intent = Intent(this@MainActivity, DetailStoryActivity::class.java)
                intent.putExtra(DetailStoryActivity.EXTRA_STORY_ID, storyId)
                startActivity(intent)
            }
        })
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}