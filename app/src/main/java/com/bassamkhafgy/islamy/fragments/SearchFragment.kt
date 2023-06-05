package com.bassamkhafgy.islamy.fragments

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bassamkhafgy.islamy.R
import com.bassamkhafgy.islamy.databinding.FragmentSearchBinding
import com.bassamkhafgy.islamy.viewmodel.SearchViewModel
import com.bassamkhafgy.islamy.viewmodel.addressLiveData
import com.bassamkhafgy.islamy.viewmodel.remoteAsrLiveData
import com.bassamkhafgy.islamy.viewmodel.remoteDuhrLiveData
import com.bassamkhafgy.islamy.viewmodel.remoteFagrLiveData
import com.bassamkhafgy.islamy.viewmodel.remoteIshaLiveData
import com.bassamkhafgy.islamy.viewmodel.remoteMagribeLiveData
import com.bassamkhafgy.islamy.viewmodel.remoteSunriseLiveData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel by viewModels<SearchViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchBTN.setOnClickListener {
            getTimings()
        }

        lifecycleScope.launch {
            remoteFagrLiveData.collect {
                binding.fagrTextView.text = it
            }

        }
        lifecycleScope.launch {
            remoteSunriseLiveData.collect {
                binding.shroukTextView.text = it
            }
        }
        lifecycleScope.launch {
            remoteDuhrLiveData.collect {
                binding.zohrCardTextView.text = it
            }
        }
        lifecycleScope.launch {
            remoteAsrLiveData.collect {
                binding.asrCardTextView.text = it
            }
        }
        lifecycleScope.launch {
            remoteMagribeLiveData.collect {
                binding.magrebTextView.text = it
            }
        }
        lifecycleScope.launch {
            remoteIshaLiveData.collect {
                binding.ishaTextView.text = it
            }
        }
        lifecycleScope.launch {
            addressLiveData.collect {
                binding.addressTV.text = it.location
            }
        }
        lifecycleScope.launch {
            binding.miladyDate.text = viewModel.getDay()
        }

    }

    private fun getTimings() {
        val currentDay = viewModel.getDay()
        val geoCoder = Geocoder(requireContext())
        val address = binding.AddressTypingEd.text
        if (address != null) {
            val addressList: List<Address>?
            try {
                addressList =
                    geoCoder.getFromLocationName(binding.AddressTypingEd?.text.toString(), 4)
                if (addressList != null) {
                    val latitude = addressList[0].latitude
                    val longitude = addressList[0].longitude
                    viewModel.getTodayRemoteTimings(
                        currentDay,
                        latitude.toString(),
                        longitude.toString()
                    )
                }
            } catch (t: Throwable) {

            }

        }
    }


}

