package com.example.di.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.di.databinding.ActivityTwoBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class ActivityTwo : AppCompatActivity() {
    lateinit var binding: ActivityTwoBinding
    @Inject
    @Named("Second")
    lateinit var welcomeMessageTwo: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.te2.text = welcomeMessageTwo

    }
}