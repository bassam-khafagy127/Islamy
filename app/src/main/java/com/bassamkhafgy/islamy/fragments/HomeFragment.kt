package com.bassamkhafgy.islamy.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.bassamkhafgy.islamy.R
import com.bassamkhafgy.islamy.data.remote.Timings
import com.bassamkhafgy.islamy.databinding.FragmentHomeBinding
import com.bassamkhafgy.islamy.utill.Constants
import com.bassamkhafgy.islamy.utill.getSystemDate
import com.bassamkhafgy.islamy.utill.isInternetConnected
import com.bassamkhafgy.islamy.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel by viewModels<HomeViewModel>()

    //UserCurrentLocation
    private val currentLocation = Location("")

    //UserCurrentAddress
    private var currentAddress = ""

    //CurrentPrayingTimings
    private val currentTimings = Timings("", "", "", "", "", "", "", "", "", "", "")

    override

    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission()
        viewModel.getLocationCoordination()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        //layout Inflation and preparation
        binding = FragmentHomeBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addCallback(view)

        //location live data
        lifecycleScope.launch {
            viewModel.flowLocationData.collect {
                currentLocation.latitude = it!!.latitude
                currentLocation.longitude = it.longitude
                currentLocation.altitude = it.altitude
            }
        }

        //getRemoteData
        if (isInternetConnected(requireContext())) {
            //is connected
            lifecycleScope.launch {
                delay(125)
                viewModel.getRemoteTimings(
                    viewModel.getTimeForApi(),
                    currentLocation.latitude.toString(),
                    currentLocation.longitude.toString()
                )
            }
            //refresh address
            lifecycleScope.launch {
                delay(180)
                currentAddress = viewModel.getUserAddress(
                    currentLocation.latitude.toString(),
                    currentLocation.longitude.toString()
                )
            }
        } else {
            //is not connected
            lifecycleScope.launch (Dispatchers.IO){
                viewModel.getCachedTimings()
            }

        }



        //        refresh timings
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.prayingTimingsFlow.collect {
                currentTimings.fajr = it.fajr
                currentTimings.sunrise = it.sunrise
                currentTimings.dhuhr = it.dhuhr
                currentTimings.asr = it.asr
                currentTimings.maghrib = it.maghrib
                currentTimings.isha = it.isha
            }
        }

        //getCurrentUserAddress
        lifecycleScope.launch {
            lifecycleScope.launch {
                viewModel.liveAddressFlow.collect {
                    currentAddress = it
                }
            }
        }

    }


    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                Constants.Location.LOCATION_PERMESSION_CODE
            )
        }
    }

    private fun addCallback(view: View) {

        binding.settingBtn.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSplashFragment()
            Navigation.findNavController(view).navigate(action)

        }

        //goto Qibla Fragment
        binding.qiblaBtn.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToQiblaFragment(
                currentAddress,
                getSystemDate(),
                currentLocation.latitude.toFloat(),
                currentLocation.longitude.toFloat(),
                currentLocation.altitude.toFloat()
            )
            Navigation.findNavController(view).navigate(action)
        }

        binding.topLocationBtn.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment(
                currentAddress
            )
            Navigation.findNavController(view).navigate(action)
        }

    }
}
