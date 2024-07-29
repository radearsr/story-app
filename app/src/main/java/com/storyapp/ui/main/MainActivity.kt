package com.storyapp.ui.main

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.storyapp.R
import com.storyapp.databinding.ActivityMainBinding
import com.storyapp.ui.components.DialogConfirmation
import com.storyapp.ui.main.create.CreateStoryActivity
import com.storyapp.ui.main.maps.MapsActivity
import com.storyapp.ui.welcome.WelcomeActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel()

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StoryAdapter
    private var isFirstLoading = true

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

        adapter = StoryAdapter()
        adapter.addLoadStateListener { combinedLoadStates ->
            when (combinedLoadStates.refresh) {
                is LoadState.Loading -> {
                    if (isFirstLoading) {
                        setViewLoading(true)
                        return@addLoadStateListener
                    }
                }

                is LoadState.NotLoading -> {
                    if (isFirstLoading) {
                        setViewLoading(false)
                        isFirstLoading = false
                        return@addLoadStateListener
                    }
                }

                is LoadState.Error -> {
                    val errorState = combinedLoadStates.refresh as LoadState.Error
                    val error = errorState.error
                    val errorMessage = error.message ?: getString(R.string.txt_unexpected_error)
                    if (isFirstLoading) {
                        setViewLoading(false)
                        setViewError(errorMessage)
                        isFirstLoading = false
                        return@addLoadStateListener
                    }
                }
            }
        }
        binding.rvListStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        viewModel.stories.observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    override fun onResume() {
        super.onResume()
        if (shouldRefresh) {
            adapter.refresh()
            shouldRefresh = false
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
        var shouldRefresh = false
    }
}