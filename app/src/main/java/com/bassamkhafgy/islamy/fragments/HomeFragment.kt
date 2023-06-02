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
import com.bassamkhafgy.islamy.data.local.TimeShow
import com.bassamkhafgy.islamy.data.remote.TimeResponse
import com.bassamkhafgy.islamy.databinding.FragmentHomeBinding
import com.bassamkhafgy.islamy.utill.Constants
import com.bassamkhafgy.islamy.viewmodel.HomeViewModel

import com.bassamkhafgy.islamy.utill.Constants.Location.LOCATION_TAG
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val viewModel by viewModels<HomeViewModel>()

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var altitued: Double = 0.0

    var fagr = ""
    var sunrise = ""
    var duhr = ""
    var asr = ""
    var magribe = ""
    var isha = ""

    private var address = ""
    private var date = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //getLocation
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        //layout Inflation and preparation
        binding = FragmentHomeBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getLocationLongitude()
        viewModel.getDate()
        viewModel.getTimings()
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

        lifecycleScope.launch {
            viewModel.fagrLiveData.collect { newValue ->
                fagr = newValue
            }
        }
        lifecycleScope.launch {
            viewModel.sunriseLiveData.collect { newValue -> sunrise = newValue }
        }

        lifecycleScope.launch {
            viewModel.duhrLiveData.collect { newValue -> duhr = newValue }
        }
        lifecycleScope.launch {
            viewModel.asrLiveData.collect { newValue -> sunrise = newValue }
        }
        lifecycleScope.launch {
            viewModel.magribeLiveData.collect { newValue -> magribe = newValue }
        }
        lifecycleScope.launch {
            viewModel.ishaLiveData.collect { newValue -> magribe = newValue }
        }
        setTimes()
        addCallbacks(view)
    }

    private fun getLocationLongitude() {
        checkPermission()
        val location = fusedLocationProviderClient.lastLocation
        location.addOnSuccessListener {

            if (it != null) {
                latitude = it.latitude
                longitude = it.longitude
                altitued = it.altitude
                //getGeocoder
                viewModel.getAddress(it.latitude, it.longitude)
            }
        }.addOnFailureListener {
            Log.d(LOCATION_TAG, "${it.message}")
            Toast.makeText(requireContext(), "Error : ${it.message}", Toast.LENGTH_LONG).show()
        }
    }


    private fun addCallbacks(view: View) {
        binding.apply {

            qiblaBtn.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToQiblaFragment(
                    address,
                    date,
                    latitude.toFloat(),
                    longitude.toFloat(),
                    altitued.toFloat()
                )
                Navigation.findNavController(view).navigate(action)
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

