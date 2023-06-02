package com.bassamkhafgy.islamy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.bassamkhafgy.islamy.R
import com.bassamkhafgy.islamy.databinding.FragmentSplashBinding
import com.bassamkhafgy.islamy.utill.Constants
import com.bassamkhafgy.islamy.utill.changeLanguage
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {
    private lateinit var binding: FragmentSplashBinding
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
        binding.arabicBtn.setOnClickListener {
            changeLanguage(requireContext(), Constants.Language.ARABIC_LANGUAGE)
            val action = SplashFragmentDirections.actionSplashFragmentToHomeFragment()
            Navigation.findNavController(view).navigate(action)
        }
        binding.englishBtn.setOnClickListener {
            changeLanguage(requireContext(), Constants.Language.ENGLISH_LANGUAGE)
            val action = SplashFragmentDirections.actionSplashFragmentToHomeFragment()
            Navigation.findNavController(view).navigate(action)
        }
    }
}