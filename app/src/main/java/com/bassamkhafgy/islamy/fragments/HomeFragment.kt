package com.bassamkhafgy.islamy.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.bassamkhafgy.islamy.R
import com.bassamkhafgy.islamy.data.local.LastLocation
import com.bassamkhafgy.islamy.data.local.PrayerSchedule
import com.bassamkhafgy.islamy.data.local.PrayerScheduleConverter
import com.bassamkhafgy.islamy.databinding.FragmentHomeBinding
import com.bassamkhafgy.islamy.utill.Constants
import com.bassamkhafgy.islamy.utill.Constants.DATABASE.ERROR_TAG_DATABASE
import com.bassamkhafgy.islamy.utill.Constants.Location.CAIRO_LAT
import com.bassamkhafgy.islamy.utill.Constants.Location.CAIRO_LONG
import com.bassamkhafgy.islamy.utill.convertToApiDateFormat
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

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var altitued: Double = 0.0

    private var fagr = ""
    private var sunrise = ""
    private var duhr = ""
    private var asr = ""
    private var magribe = ""
    private var isha = ""

    private var remainingTimeForNextPray = ""

    private var currentHour = ""

    private var address = ""
    private var date = getSystemDate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission()
        viewModel.getLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        viewModel.getDate()
        viewModel.getCurrentHour()
        //layout Inflation and preparation
        binding = FragmentHomeBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isInternetConnected(requireContext())) {
            //InterNetAvailable

            //get Remote TodayTimings and handle Data
            lifecycleScope.launch(Dispatchers.Main) {
                //get Cairo TodayTimings
                if (latitude == 0.0 && longitude == 0.0) {
                    viewModel.getTodayRemoteTimings(
                        convertToApiDateFormat(date), CAIRO_LAT.toString(), CAIRO_LONG.toString()
                    )
                    viewModel.getAddress(CAIRO_LAT, CAIRO_LONG)
                } else {
                    //get Location TodayTimings
                    viewModel.getTodayRemoteTimings(
                        convertToApiDateFormat(date), latitude.toString(), longitude.toString()
                    )
                    viewModel.getAddress(latitude, longitude)
                }

            }

            //RemainingTimeLiveData
            lifecycleScope.launch {
                viewModel.remainingTimeLiveData.collect {
                    remainingTimeForNextPray = it
                }
            }

            //RemainingTimeLiveData
            lifecycleScope.launch {
                viewModel.addressLiveData.collect {
                    address = it.location
                }
            }


            //Prayer Times
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.remoteFagrLiveData.collect { newValue -> fagr = newValue }
            }
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.remoteSunriseLiveData.collect { newValue -> sunrise = newValue }
            }
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.remoteDuhrLiveData.collect { newValue -> duhr = newValue }
            }

            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.remoteAsrLiveData.collect { newValue -> asr = newValue }
            }

            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.remoteMagribeLiveData.collect { newValue -> magribe = newValue }
            }

            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.remoteIshaLiveData.collect { newValue -> isha = newValue }

            }

            //UpdateLocalTimings
            lifecycleScope.launch(Dispatchers.IO) {
                delay(2000)
                Log.d("LOCALTIMINGS", "$fagr:D")
                val timings = PrayerSchedule(0, fagr, sunrise, duhr, asr, magribe, isha, address)
                fagr = timings.fajr
                sunrise = timings.sunrise
                duhr = timings.dhuhr
                asr = timings.asr
                magribe = timings.maghrib
                isha = timings.isha
                Log.d("TTTTT", fagr)
            }

        } else {
            //NoInternetConnection GetLocalData

            //get Local TodayTimings and handle Data
            lifecycleScope.launch(Dispatchers.Main) {
                //get Cairo TodayTimings
                if (latitude == 0.0 && longitude == 0.0) {

                    viewModel.getAddress(CAIRO_LAT, CAIRO_LONG)
                } else {
                    viewModel.getAddress(latitude, longitude)
                }
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val timings = viewModel.getStoredTimings()
                fagr = timings.fajr
                sunrise = timings.sunrise
                duhr = timings.dhuhr
                asr = timings.asr
                magribe = timings.maghrib
                isha = timings.isha
            }

            //RemainingTimeLiveData
            lifecycleScope.launch {
                viewModel.remainingTimeLiveData.collect {
                    remainingTimeForNextPray = it
                }
            }

            //RemainingTimeLiveData
            lifecycleScope.launch {
                viewModel.addressLiveData.collect {
                    address = it.location
                }
            }

        }

        //locationLiveDate
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.locationLiveData.collect {
                latitude = it.latitude
                longitude = it.longitude
                altitued = it.latitude
            }
        }


        //currentHourLiveData
        lifecycleScope.launch {
            viewModel.currentHourLiveData.collect {
                currentHour = it
            }
        }


        //nextPrayTimeLiveData
        lifecycleScope.launch {
            delay(2000)
            val prayTime = viewModel.getNextPrayerTimeTitle(
                PrayerScheduleConverter(
                    fagr,
                    sunrise,
                    duhr,
                    asr,
                    magribe,
                    isha
                )
            )
            Log.d("TTTTT:Fragment", prayTime.second + prayTime.first)
            viewModel.getRemainingTimeToNextPrayer(currentHour, "12:5", "fagr")


        }

        //goto Qibla Fragment
        binding.qiblaBtn.setOnClickListener {

            if (latitude == 0.0 && longitude == 0.0) {
                val action = HomeFragmentDirections.actionHomeFragmentToQiblaFragment(
                    address, date, CAIRO_LAT.toFloat(), CAIRO_LONG.toFloat(), altitued.toFloat()
                )
                Navigation.findNavController(view).navigate(action)
            } else {
                val action = HomeFragmentDirections.actionHomeFragmentToQiblaFragment(
                    address, date, latitude.toFloat(), longitude.toFloat(), altitued.toFloat()
                )
                Navigation.findNavController(view).navigate(action)
            }

        }

        setViews()
        addCallback(view)
    }

    private fun setViews() {
        binding.apply {
            fagrTextView.text = fagr
            shroukTextView.text = sunrise
            zohrCardTextView.text = duhr
            asrCardTextView.text = asr
            magrebTextView.text = magribe
            ishaTextView.text = isha
            addressTV.text = address
            dateTV.text = date
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
        binding.topLocationBtn.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment(
                fagr,
                sunrise,
                duhr,
                asr,
                magribe,
                isha
            )
            Navigation.findNavController(view).navigate(action)
        }
    }
}
