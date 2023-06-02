package com.bassamkhafgy.islamy.utill

import android.content.Context
import android.os.Build
import java.util.Locale

fun changeLanguage(context: Context, languageCode: String) {
    val resources = context.resources
    val configuration = resources.configuration
    val locale = Locale(languageCode)

    Locale.setDefault(locale)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        configuration.setLocale(locale)
    } else {
        @Suppress("DEPRECATION")
        configuration.locale = locale
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        configuration.setLayoutDirection(locale)
    }

    @Suppress("DEPRECATION")
    resources.updateConfiguration(configuration, resources.displayMetrics)
}
