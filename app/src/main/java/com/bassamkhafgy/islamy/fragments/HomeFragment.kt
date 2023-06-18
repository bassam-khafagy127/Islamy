package com.bassamkhafgy.islamy.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
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
import com.bassamkhafgy.islamy.utill.convertDateFormat
import com.bassamkhafgy.islamy.utill.getDayCounter
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
            lifecycleScope.launch(Dispatchers.IO) {
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

        lifecycleScope.launch {
            delay(3000)
            val prayerTimes = listOf(
                PrayerTime(resources.getString(R.string.fagrTime), "${currentTimings.fajr}"),
                PrayerTime(resources.getString(R.string.shroukTime), "${currentTimings.sunrise}"),
                PrayerTime(resources.getString(R.string.zohrTime), "${currentTimings.dhuhr}"),
                PrayerTime(resources.getString(R.string.asrTime), "${currentTimings.asr}"),
                PrayerTime(resources.getString(R.string.magrbeTime), "${currentTimings.maghrib}"),
                PrayerTime(resources.getString(R.string.isha_time), "${currentTimings.isha}"),
            )
            viewModel.getNextPrayer(prayerTimes)
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
