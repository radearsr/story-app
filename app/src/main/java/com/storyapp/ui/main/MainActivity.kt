package com.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.storyapp.R
import com.storyapp.data.ResultState
import com.storyapp.data.remote.response.ListStoryItem
import com.storyapp.data.repository.StoryRepository
import com.storyapp.databinding.ActivityMainBinding
import com.storyapp.ui.ViewModelFactory

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getStories().observe(this) { result ->
            if(result != null) {
                when(result) {
                    is ResultState.Loading -> {
                        Log.d(TAG, "Loading Stories")
                    }
                    is ResultState.Success -> {
                        Log.d(TAG, "State Success Stories")
                        setupRecyclerView(result.data)
                    }
                    is ResultState.Error -> {
                        Log.e(TAG, "Error State Stories")
                    }
                }
            }
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