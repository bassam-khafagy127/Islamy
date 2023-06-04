package com.bassamkhafgy.islamy.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.bassamkhafgy.islamy.R
import com.bassamkhafgy.islamy.databinding.FragmentHomeBinding
import com.bassamkhafgy.islamy.utill.Constants
import com.bassamkhafgy.islamy.utill.Constants.Location.CAIRO_LAT
import com.bassamkhafgy.islamy.utill.Constants.Location.CAIRO_LONG
import com.bassamkhafgy.islamy.utill.convertToApiDateFormat
import com.bassamkhafgy.islamy.utill.getSystemDate
import com.bassamkhafgy.islamy.utill.isInternetConnected
import com.bassamkhafgy.islamy.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding


    private val viewModel by viewModels<HomeViewModel>()

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var altitued: Double = 0.0

    private var fagr = ""
    private var sunrise = ""
    private var duhr = ""
    private var asr = ""
    private var magribe = ""
    private var isha = ""

    private var currentHour = ""

    private var address = ""
    private var date = getSystemDate()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        checkPermission()
        //getLocation
        viewModel.getLocation()
        //layout Inflation and preparation
        binding = FragmentHomeBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkPermission()
        viewModel.getCurrentHour()
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.locationLiveData.collect {
                latitude = it.latitude
                longitude = it.longitude
                altitued = it.latitude
            }
        }

        viewModel.getRemainingTimeToNextPrayer("1:09", "4:4")

        viewModel.getDate()
        if (isInternetConnected(requireContext())) {
            //AddressGeocoder

            lifecycleScope.launch(Dispatchers.Main) {
                if (latitude == 0.0 || longitude == 0.0) {
                    viewModel.getTimings(
                        convertToApiDateFormat(date),
                        CAIRO_LAT.toString(),
                        CAIRO_LONG.toString()
                    )
                } else {

                    viewModel.getTimings(
                        convertToApiDateFormat(date), latitude.toString(), longitude.toString()
                    )

                }
                lifecycleScope.launch {
                    if (latitude == 0.0 || longitude == 0.0) {
                        viewModel.getAddress(CAIRO_LAT, CAIRO_LONG)
                    } else viewModel.getAddress(latitude, longitude)

                }

            }

            lifecycleScope.launch {
                viewModel.dataLiveData.collect { newDate ->
                    date = newDate
                }
            }

            lifecycleScope.launch {
                viewModel.addressLiveData.collect { newAddress ->
                    address = newAddress
                }
            }

            //Prayer Times
            lifecycleScope.launch {
                viewModel.remoteFagrLiveData.collect { newValue ->
                    fagr = newValue
                }
            }
            lifecycleScope.launch {
                viewModel.remoteSunriseLiveData.collect { newValue -> sunrise = newValue }
            }

            lifecycleScope.launch {
                viewModel.remoteDuhrLiveData.collect { newValue -> duhr = newValue }
            }
            lifecycleScope.launch {
                viewModel.remoteAsrLiveData.collect { newValue -> sunrise = newValue }
            }
            lifecycleScope.launch {
                viewModel.remoteMagribeLiveData.collect { newValue -> magribe = newValue }
            }
            lifecycleScope.launch {
                viewModel.remoteIshaLiveData.collect { newValue -> magribe = newValue }

            }
            Log.e("REMAiningTime: currentHour", currentHour)

        } else {
            Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_LONG).show()
        }

        lifecycleScope.launch {
            viewModel.currentHourLiveData.collect {
                currentHour = it
            }
        }
        lifecycleScope.launch {
            viewModel.remainingTimeLiveData.collect {
                binding.nextPrayerTimeTV.text = it
            }
        }

        setTimes()
        addCallbacks(view)
    }


    private fun addCallbacks(view: View) {
        binding.apply {

            qiblaBtn.setOnClickListener {
                val lat = latitude
                if (lat == 0.0) {
                    val action = HomeFragmentDirections.actionHomeFragmentToQiblaFragment(
                        address,
                        date,
                        CAIRO_LAT.toFloat(),
                        CAIRO_LONG.toFloat(),
                        altitued.toFloat()
                    )
                    Navigation.findNavController(view).navigate(action)
                } else {
                    val action = HomeFragmentDirections.actionHomeFragmentToQiblaFragment(
                        address,
                        date,
                        latitude.toFloat(),
                        longitude.toFloat(),
                        altitued.toFloat()
                    )
                    Navigation.findNavController(view).navigate(action)
                }

            }

            settingBtn.setOnClickListener {
                Toast.makeText(requireContext(), "$latitude", Toast.LENGTH_LONG).show()
            }

        }

    }

    private fun setTimes() {
        binding.apply {
            fagrTextView.text = fagr
            shroukTextView.text = sunrise
            zohrCardTextView.text = duhr
            asrCardTextView.text = asr
            magrebTextView.text = magribe
            ishaTextView.text = isha

        }
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                Constants.Location.LOCATION_PERMESSION_CODE
            )
        }
    }


}

