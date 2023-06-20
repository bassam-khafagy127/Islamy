package com.bassamkhafgy.islamy.utill

import com.bassamkhafgy.islamy.R

object Constants {
    object Location {
        const val LOCATION_TAG = "LocationSA"
        const val LOCATION_PERMISSION_CODE = 121

        const val KAABA_LAT = 21.422487
        const val KAABA_LONG = 39.826206
        const val CAIRO_LAT = 30.033333
        const val CAIRO_LONG = 31.233334
    }

    const val ERROR_TAG = "ERROR_TAG"

    object Language {
        const val ARABIC_LANGUAGE = "ar"
        const val ENGLISH_LANGUAGE = "en"
    }

    object DATABASE {
        const val PRAYER_TABLE_NAME = "PRAYER TIMES"
        const val LOCATION_TABLE_NAME = "LAST LOCATION"
        const val TIMINGS_DATABASE_NAME = "PRAYER TIMES DATABASE"

    }

    const val STORED_ADDRESS = "ADDRESS_STORED"

    object LANGUAGENAVIGATION {
        const val LANGUAGE_SHARED_PREFERENCE = "LANGUAGE_SHARED_PREFERENCE"


        const val IS_FIRST_LAUNCH_BUTTON_CLICKED =
            "IS_FIRST_LAUNCH_BUTTON_CLICKED"

        const val WHICH_LANGUAGE_BUTTON =
            "WHICH_LANGUAGE_BUTTON"


        val HOME_FRAGMENT = R.id.action_splashFragment_to_homeFragment
    }
}