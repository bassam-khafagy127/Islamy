package com.bassamkhafgy.islamy.fragments

import android.content.Context
import android.content.Intent
import android.hardware.GeomagneticField
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bassamkhafgy.islamy.R
import com.bassamkhafgy.islamy.databinding.FragmentQiblaBinding
import com.bassamkhafgy.islamy.utill.Constants.Location.KAABA_LAT
import com.bassamkhafgy.islamy.utill.Constants.Location.KAABA_LONG
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale


@AndroidEntryPoint
class QiblaFragment : Fragment(R.layout.fragment_qibla), SensorEventListener {

    private lateinit var binding: FragmentQiblaBinding
    private val qiblaArgs: QiblaFragmentArgs by navArgs()

    private lateinit var sensorManager: SensorManager
    private lateinit var sensor: Sensor
    private var currentDegreeNeedle = 0f
    private lateinit var userLoc: Location

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)

        userLoc = Location(LocationManager.GPS_PROVIDER)

        userLoc.longitude = qiblaArgs.locationLongArgs.toDouble()
        userLoc.latitude = qiblaArgs.locationLateArgs.toDouble()
        userLoc.altitude = qiblaArgs.locationAltituedArgs.toDouble()

        binding = FragmentQiblaBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val address = qiblaArgs.addressGyocoderArgs
        val date = qiblaArgs.dateArgs
        val location = "Lat:${qiblaArgs.locationLateArgs}\nLong:${qiblaArgs.locationLongArgs}"


        binding.apply {
            //TextView Customization
            qiblaDateTv.text = date
            qiblaAddressTv.text = address
            qiblaLocationTV.text = location
        }

        binding.showOnMapBtn.setOnClickListener {
            openMap()
        }

        binding.searchForNewCityTv.setOnClickListener {
            val action =
                QiblaFragmentDirections.actionQiblaFragmentToSearchFragment(
                    qiblaArgs.addressGyocoderArgs,
                )
            Navigation.findNavController(view).navigate(action)
        }

    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)

    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        Math.round(event.values[0])
        var head = Math.round(event.values[0])
        var bearTo: Float

        val destinationLoc = Location(LocationManager.GPS_PROVIDER)
        destinationLoc.latitude = KAABA_LAT// Kaaba latitude setting
        destinationLoc.longitude = KAABA_LONG// Kaaba longitude setting
        bearTo = userLoc.bearingTo(destinationLoc)

        val geoField = GeomagneticField(
            userLoc.latitude.toFloat(),
            userLoc.longitude.toFloat(),
            userLoc.altitude.toFloat(),
            System.currentTimeMillis()
        )
        head -= geoField.declination.toInt()

        if (bearTo < 0) {
            bearTo += 360
        }

        var direction = bearTo - head

        if (direction < 0) {
            direction += 360
        }

        //   binding.text = "Heading: $degree degrees"

        val raQibla = RotateAnimation(
            currentDegreeNeedle,
            direction,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        ).apply {
            duration = 210
            fillAfter = true
        }
        binding.qiblaCompass.startAnimation(raQibla)
        currentDegreeNeedle = direction

        //north direction
//        val ra = RotateAnimation(
//            currentDegree,
//            -degree.toFloat(),
//            Animation.RELATIVE_TO_SELF,
//            0.5f,
//            Animation.RELATIVE_TO_SELF,
//            0.5f
//        ).apply {
//            duration = 210
//            fillAfter = true
//        }
//        binding.qiblaCompass.startAnimation(ra)
//        currentDegree = -degree.toFloat()

    }

    private fun openMap() {

        val uri = String.format(
            Locale.ENGLISH,
            "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)",
            userLoc.latitude,
            userLoc.longitude,
            resources.getString(R.string.yourLocation),
            KAABA_LAT,
            KAABA_LONG,
            resources.getString(R.string.kaaba)
        )
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setPackage("com.google.android.apps.maps")
        startActivity(intent)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do nothing
    }
}
