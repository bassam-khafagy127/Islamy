package com.bassamkhafgy.islamy.fragments

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bassamkhafgy.islamy.R
import com.bassamkhafgy.islamy.databinding.FragmentSearchBinding
import com.bassamkhafgy.islamy.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel by viewModels<SearchViewModel>()

    private val searchArgs: SearchFragmentArgs by navArgs()

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
            viewModel.prayingTimingsFlow.collect {
                binding.fagrTextView.text = it.fajr
                binding.shroukTextView.text = it.sunrise
                binding.zohrCardTextView.text = it.dhuhr
                binding.asrCardTextView.text = it.asr
                binding.magrebTextView.text = it.maghrib
                binding.ishaTextView.text = it.isha
            }

        }
        lifecycleScope.launch {
            viewModel.addressLiveData.collect {
                binding.addressTV.text = it.location

            }
        }
        lifecycleScope.launch {
            binding.miladyDate.text = viewModel.getDay()
        }

    }

    private fun getTimings() {
        lifecycleScope.launch {
            val currentDay = viewModel.getDay()
            val geoCoder = Geocoder(requireContext())
            val address = binding.AddressTypingEd.text
            if (address != null) {
                val addressList: List<Address>?
                try {
                    addressList =
                        geoCoder.getFromLocationName(binding.AddressTypingEd.text.toString(), 4)
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
                    Toast.makeText(requireContext(),t.localizedMessage,Toast.LENGTH_LONG).show()
                }

            }
        }

    }


}

