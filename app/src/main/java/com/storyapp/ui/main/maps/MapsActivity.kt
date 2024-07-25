package com.storyapp.ui.main.maps

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.storyapp.BuildConfig
import com.storyapp.R
import com.storyapp.data.ResultState
import com.storyapp.databinding.ActivityMapsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {


    private val boundsBuilder = LatLngBounds.Builder()

    private val viewModel: MapsViewModel by viewModel()

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupRetrieveStories()
    }

    private fun setupRetrieveStories() {
        viewModel.getStoryWithLocation().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> {
                        Log.d(TAG, "Is loading")
                        setViewLoading(true)
                    }
                    is ResultState.Success -> {
                        setViewLoading(false)
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "State Success ${result.data}")
                        }
                        result.data.forEach { data ->
                            val latLng = data.lat?.let { data.lon?.let { lat ->
                                LatLng(it,
                                    lat
                                )
                            } }
                            latLng?.let {
                                MarkerOptions()
                                    .position(it)
                                    .title(data.name)
                                    .snippet(data.description)
                            }?.let { markerOption ->
                                mMap.addMarker(markerOption)
                                boundsBuilder.include(latLng)
                            }
                        }
                        val bounds: LatLngBounds = boundsBuilder.build()
                        mMap.setOnMapLoadedCallback {
                            mMap.animateCamera(
                                CameraUpdateFactory.newLatLngBounds(
                                    bounds,
                                    resources.displayMetrics.widthPixels,
                                    resources.displayMetrics.heightPixels,
                                    0
                                )
                            )
                        }
                    }
                    is ResultState.Error -> {
                        setViewLoading(false)
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, "State Error ${result.error}")
                        }
                    }
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        setMapStyle()
    }


    private fun setViewLoading(isLoading: Boolean) {
        binding.errorComp.clError.visibility = View.GONE
        with(binding.loadingComp.clLoading) {
            visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    companion object {
        private val TAG = MapsActivity::class.java.simpleName
    }
}