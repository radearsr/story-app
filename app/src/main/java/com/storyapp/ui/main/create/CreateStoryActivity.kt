package com.storyapp.ui.main.create

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.storyapp.BuildConfig
import com.storyapp.R
import com.storyapp.data.ResultState
import com.storyapp.databinding.ActivityCreateStoryBinding
import com.storyapp.ui.components.DialogInformation
import com.storyapp.utils.reduceFileImage
import com.storyapp.utils.uriToFile
import org.koin.androidx.viewmodel.ext.android.viewModel


class CreateStoryActivity : AppCompatActivity() {
    private val viewModel: CreateStoryViewModel by viewModel()

    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null

    private lateinit var binding: ActivityCreateStoryBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

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

    private fun cameraPermissionGranted() = checkPermission(CAMERA_PERMISSION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!cameraPermissionGranted()) {
            requestPermissionLauncher.launch(CAMERA_PERMISSION)
        }

        setupButtonStartEnable()
        binding.buttonAdd.setOnClickListener {
            uploadImage()
        }
        binding.fabBack.setOnClickListener {
            finish()
        }
        binding.btnGallery.setOnClickListener {
            startGallery()
        }
        binding.cbComp.tvCheckbox.setOnClickListener {
            with(binding.cbComp.checkbox) {
                isChecked = !isChecked
            }
        }
        binding.cbComp.checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getMyLocation()
                return@setOnCheckedChangeListener
            }
            currentLongitude = null
            currentLatitude = null
        }
        binding.btnCamera.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            launcherIntentCamera.launch(intent)
        }
        binding.edAddDescription.addTextChangedListener(
            onTextChanged = { _, _, _, _ ->
                setupButtonStartEnable()
            }
        )
        setupObserver()
    }

    private fun setupButtonStartEnable() {
        with(binding) {
            buttonAdd.isEnabled = edAddDescription.text.isNotEmpty()
        }
    }

    private fun uploadImage() {
        val currentImage = viewModel.getCurrentImage()
        currentImage?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.edAddDescription.text.toString()
            viewModel.uploadStory(imageFile, description, currentLatitude, currentLongitude).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> {
                            setViewLoading(true)
                        }

                        is ResultState.Success -> {
                            setViewLoading(false)
                            if (BuildConfig.DEBUG) Log.d(TAG, "State Success ${result.data}")
                            finish()
                        }

                        is ResultState.Error -> {
                            setViewLoading(false)
                            if (BuildConfig.DEBUG) Log.e(TAG, "State Error ${result.error}")
                            val dialog = DialogInformation(
                                this@CreateStoryActivity,
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

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    if (BuildConfig.DEBUG) Log.d(TAG, "Longitude: ${location.longitude} | Latitude: ${location.latitude}")
                    currentLatitude = location.latitude
                    currentLongitude = location.longitude
                } else {
                    Toast.makeText(
                        this@CreateStoryActivity,
                        getString(R.string.location_not_found),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestMultiplePermissions.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.entries.forEach {
            Log.d(TAG, "${it.key} = ${it.value}")
        }
    }

    companion object {
        private val TAG = CreateStoryActivity::class.java.simpleName
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
    }
}