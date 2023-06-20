package com.bassamkhafgy.islamy.fragments

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
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
import com.bassamkhafgy.islamy.data.local.PrayerTime
import com.bassamkhafgy.islamy.data.remote.Timings
import com.bassamkhafgy.islamy.databinding.FragmentHomeBinding
import com.bassamkhafgy.islamy.utill.Constants
import com.bassamkhafgy.islamy.utill.Constants.STORED_ADDRESS
import com.bassamkhafgy.islamy.utill.Resource
import com.bassamkhafgy.islamy.utill.convertDateFormat
import com.bassamkhafgy.islamy.utill.getDayCounter
import com.bassamkhafgy.islamy.utill.getNextAzanTitle
import com.bassamkhafgy.islamy.utill.getSystemDate
import com.bassamkhafgy.islamy.utill.isInternetConnected
import com.bassamkhafgy.islamy.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel by viewModels<HomeViewModel>()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    //UserCurrentLocation
    private val currentLocation = Location("")

    //UserCurrentAddress
    private var currentAddress = ""

    private var dayCounter = 0

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
        viewModel.getLocationCoordination()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addCallback(view)

        if (isInternetConnected(requireContext())) {
            lifecycleScope.launch { isInterNetConnectedTimings() }
        } else {
            lifecycleScope.launch {
                isInterNetNotConnectedTimings()
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.liveAddressFlow.collect {
                currentAddress = it
                delay(2000)
                viewModel.updateAddress(currentAddress)
            }
        }
    }

    private suspend fun isInterNetConnectedTimings() {
        viewModel.flowLocationData.collect { locationState ->
            when (locationState) {
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "${locationState.message}", Toast.LENGTH_LONG)
                        .show()
                }

                is Resource.Loading -> {
                    Log.e(Constants.ERROR_TAG + "ADDRESS_GEOCODER LAT LONG", "Lat:Long Loading")
                }

                is Resource.Success -> {
                    //ForQiblaCompass
                    currentLocation.latitude = locationState.data!!.latitude
                    currentLocation.altitude = locationState.data.altitude
                    currentLocation.longitude = locationState.data.longitude
                    //getAddress
                    viewModel.getUserAddress(
                        locationState.data.latitude.toString(),
                        locationState.data.longitude.toString()
                    )

                    //getRemoteTimings
                    lifecycleScope.launch {
                        viewModel.getRemoteTimings(
                            viewModel.getTimeForApi(),
                            locationState.data.latitude.toString(),
                            locationState.data.longitude.toString()
                        )
                    }
                    //getNextPrayName
                    nextPrayTitle()

                }

                is Resource.Unspecified -> {
                    Log.e(Constants.ERROR_TAG + "ADDRESS_GEOCODER LAT LONG", "Lat:Long Unspecified")
                }
            }
        }

    }

    private suspend fun isInterNetNotConnectedTimings() {
        //getLocalTimingsFromDataBase
        viewModel.getCachedTimings()

        //getNextPrayName
        nextPrayTitle()
    }

    //getNextPray
    private suspend fun nextPrayTitle() {
        viewModel.prayingTimingsFlow.collect { timingsState ->
            when (timingsState) {
                is Resource.Error -> {
                    Log.e(Constants.ERROR_TAG + "nextPrayTitle", "Error")
                }

                is Resource.Loading -> {
                    Log.e(Constants.ERROR_TAG + "nextPrayTitle", "Loading")
                }

                is Resource.Success -> {
                    viewModel.prayingTimingsFlow.collect {
                        val prayerTimes = listOf(
                            PrayerTime(
                                resources.getString(R.string.fagrTime),
                                timingsState.data!!.fajr
                            ),
                            PrayerTime(
                                resources.getString(R.string.shroukTime),
                                timingsState.data.sunrise
                            ),
                            PrayerTime(
                                resources.getString(R.string.zohrTime),
                                timingsState.data.dhuhr
                            ),
                            PrayerTime(
                                resources.getString(R.string.asrTime),
                                timingsState.data.asr
                            ),
                            PrayerTime(
                                resources.getString(R.string.magrbeTime),
                                timingsState.data.maghrib
                            ),
                            PrayerTime(
                                resources.getString(R.string.isha_time),
                                timingsState.data.isha
                            ),
                        )
                        viewModel.getNextPrayer(prayerTimes)
                    }
                }

                is Resource.Unspecified -> {
                    Log.e(Constants.ERROR_TAG + "nextPrayTitle", "Unspecified")
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
                Constants.Location.LOCATION_PERMISSION_CODE
            )
        }
    }

    //system interactive
    private fun addCallback(view: View) {
        binding.dateTV.text = getSystemDate()

        binding.searchBtn.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment(
                currentAddress
            )
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

        binding.nextArrowTimeBTN.setOnClickListener {
            getDayTimings("++")
        }

        binding.lastArrowTimeBTN.setOnClickListener {
            getDayTimings("--")
        }
    }

    private fun getDayTimings(counterType: String) {
        if (counterType == "--") {
            dayCounter--
        }
        if (counterType == "++") {
            dayCounter++
        }
        if (isInternetConnected(requireContext())) {
            lifecycleScope.launch {
                viewModel.getNextAndLastDayTimings(
                    getDayCounter(dayCounter),
                    currentLocation.longitude.toString(),
                    currentLocation.longitude.toString()
                )
            }
            binding.dateTV.text = convertDateFormat(getDayCounter(dayCounter))
        } else Toast.makeText(
            requireContext(),
            "Please Check Your Connection",
            Toast.LENGTH_LONG
        ).show()
    }

}
