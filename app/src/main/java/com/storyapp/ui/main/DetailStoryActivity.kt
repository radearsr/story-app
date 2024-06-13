package com.storyapp.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.storyapp.R
import com.storyapp.data.ResultState
import com.storyapp.databinding.ActivityDetailStoryBinding
import com.storyapp.ui.StoryViewModelFactory

class DetailStoryActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        StoryViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityDetailStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBackAction.setOnClickListener {
            finish()
        }
        val storyId = intent.getStringExtra(EXTRA_STORY_ID)
        if (storyId != null) {
            viewModel.getDetailStory(storyId).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> {
                            Log.i(TAG, "Loading State Detail Story")
                        }

                        is ResultState.Success -> {
                            Glide
                                .with(this)
                                .load(result.data.photoUrl)
                                .into(binding.ivDetailPhoto)

                            binding.tvDetailName.text = result.data.name
                            binding.tvDetailDescription.text = result.data.description
                        }

                        is ResultState.Error -> {
                            Log.e(TAG, "Error State Detail Story")
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_STORY_ID = "extra_story_id"
        private val TAG = DetailStoryActivity::class.java.simpleName
    }
}