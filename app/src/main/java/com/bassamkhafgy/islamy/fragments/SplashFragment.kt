package com.bassamkhafgy.islamy.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bassamkhafgy.islamy.R
import com.bassamkhafgy.islamy.activities.MainActivity
import com.bassamkhafgy.islamy.databinding.FragmentSplashBinding
import com.bassamkhafgy.islamy.utill.Constants
import com.bassamkhafgy.islamy.utill.Constants.LANGUAGENAVIGATION.HOME_FRAGMENT
import com.bassamkhafgy.islamy.utill.Constants.Language.ARABIC_LANGUAGE
import com.bassamkhafgy.islamy.utill.Constants.Language.ENGLISH_LANGUAGE
import com.bassamkhafgy.islamy.utill.changeLanguage
import com.bassamkhafgy.islamy.viewmodel.LanguageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {
    private lateinit var binding: FragmentSplashBinding
    private val viewModel by viewModels<LanguageViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //
        checkPermission()
        //

        lifecycleScope.launchWhenCreated {
            viewModel.navigation.collect {
                when (it) {
                    HOME_FRAGMENT -> {
                       startMainActivity()
                    }
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.languageFlow.collect {
                changeLanguage(requireContext(), it)
            }
        }

        binding.arabicBtn.setOnClickListener {
            //   SetLang ARABIC
            changeLanguage(requireContext(), ARABIC_LANGUAGE)
            viewModel.isButtonClickedFunction(ARABIC_LANGUAGE)

            startMainActivity()
        }

        binding.englishBtn.setOnClickListener {
            changeLanguage(requireContext(), ENGLISH_LANGUAGE)
            //   SetLang ENGLISH_LANGUAGE
            viewModel.isButtonClickedFunction(ENGLISH_LANGUAGE)

            startMainActivity()
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
                Constants.Location.LOCATION_PERMISSION_CODE
            )
        }
    }

    private fun startMainActivity() {
        val intent = Intent(requireActivity(), MainActivity::class.java).also { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        startActivity(intent)
    }
}