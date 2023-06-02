package com.example.di.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.di.R
import com.example.di.databinding.ActivityMainBinding
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    @Inject
    @Named("First")
    lateinit var welcomeMessageFirst: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.te1.text = welcomeMessageFirst

        binding.te1.setOnClickListener {
            val intent= Intent(this@MainActivity,ActivityTwo::class.java)
            startActivity(intent)
        }
    }
}