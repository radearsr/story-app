package com.storyapp.ui.main.create

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.storyapp.BuildConfig
import com.storyapp.R
import com.storyapp.data.ResultState
import com.storyapp.databinding.ActivityCreateStoryBinding
import com.storyapp.ui.StoryViewModelFactory
import com.storyapp.ui.components.DialogInformation
import com.storyapp.ui.main.MainActivity
import com.storyapp.utils.reduceFileImage
import com.storyapp.utils.uriToFile


class CreateStoryActivity : AppCompatActivity() {
    private val viewModel by viewModels<CreateStoryViewModel> {
        StoryViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityCreateStoryBinding

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.setCurrentImage(uri)
        } else {
            Log.d(TAG, getString(R.string.txt_no_media))
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CameraActivity.CAMERA_RESULT) {
            val resultUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERA_IMAGE)?.toUri()
            if (resultUri != null) {
                viewModel.setCurrentImage(resultUri)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showToast(getString(R.string.txt_granted))
        } else {
            showToast(getString(R.string.txt_denied))
        }
    }

    private fun allPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        REQUIRED_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
        binding.buttonAdd.setOnClickListener {
            uploadImage()
        }
        binding.fabBack.setOnClickListener {
            finish()
        }
        binding.btnGallery.setOnClickListener {
            startGallery()
        }
        binding.btnCamera.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            launcherIntentCamera.launch(intent)
        }

        setupObserver()
    }

    private fun uploadImage() {
        val currentImage = viewModel.getCurrentImage()
        currentImage?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.edAddDescription.text.toString()
            viewModel.uploadStory(imageFile, description).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> {
                            setViewLoading(true)
                        }

                        is ResultState.Success -> {
                            setViewLoading(false)
                            if (BuildConfig.DEBUG) Log.d(TAG, "State Success ${result.data}")
                            val isGuestMode = intent.getBooleanExtra(EXTRA_GUEST_MODE, false)
                            if (isGuestMode) {
                                val intent = Intent(this@CreateStoryActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                                return@observe
                            }
                            finish()
                        }

                        is ResultState.Error -> {
                            setViewLoading(false)
                            if (BuildConfig.DEBUG) Log.e(TAG, "State Error ${result.error}")
                            val dialog = DialogInformation(
                                this@CreateStoryActivity,
                                getString(R.string.txt_alert),
                                result.error,
                                true
                            )
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                            dialog.show()
                        }
                    }
                }
            }
        } ?: showToast(getString(R.string.txt_image_required))
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupObserver() {
        viewModel.currentImage.observe(this) { uri ->
            binding.ivPreview.apply {
                setImageURI(uri)
                background = ContextCompat.getDrawable(
                    this@CreateStoryActivity,
                    R.drawable.rounded_background
                )
                clipToOutline = true
            }
        }
    }

    private fun setViewLoading(isLoading: Boolean) {
        with(binding.loadingComp.clLoading) {
            visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        binding.buttonAdd.isEnabled = !isLoading
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    companion object {
        private val TAG = CreateStoryActivity::class.java.simpleName
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        const val EXTRA_GUEST_MODE = "extra_guest_mode"
    }
}