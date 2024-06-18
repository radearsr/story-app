package com.storyapp.ui.main.detail

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.storyapp.BuildConfig
import com.storyapp.R
import com.storyapp.data.ResultState
import com.storyapp.databinding.ActivityDetailStoryBinding
import com.storyapp.ui.StoryViewModelFactory
import com.storyapp.ui.components.DialogInformation
import com.storyapp.utils.getTimeAgo

class DetailStoryActivity : AppCompatActivity() {
    private val viewModel by viewModels<DetailStoryViewModel> {
        StoryViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityDetailStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabBack.setOnClickListener {
            finish()
        }
        val storyId = intent.getStringExtra(EXTRA_STORY_ID)
        if (storyId != null) {
            viewModel.getDetailStory(storyId).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> {
                            setViewLoading(true)
                        }

                        is ResultState.Success -> {
                            if (BuildConfig.DEBUG) Log.d(TAG, "State Success ${result.data}")
                            setViewLoading(false)
                            Glide
                                .with(this)
                                .load(result.data.photoUrl)
                                .placeholder(R.drawable.ic_place_holder)
                                .into(binding.ivDetailPhoto)
                            binding.ivDetailPhoto.clipToOutline = true
                            with(binding) {
                                tvDetailName.text = result.data.name
                                tvDetailDescription.text = result.data.description
                                tvDate.text = getTimeAgo(this@DetailStoryActivity, result.data.createdAt)
                            }
                        }

                        is ResultState.Error -> {
                            if (BuildConfig.DEBUG) Log.e(TAG, "State Error ${result.error}")
                            setViewLoading(false)
                            val dialog = DialogInformation(
                                this@DetailStoryActivity,
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
                        }
                    }
                }
            }
        }
    }


    private fun setViewLoading(isLoading: Boolean) {
        with(binding.loadingComp.clLoading) {
            visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    companion object {
        const val EXTRA_STORY_ID = "extra_story_id"
        private val TAG = DetailStoryActivity::class.java.simpleName
    }
}