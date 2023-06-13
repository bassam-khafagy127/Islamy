package com.bassamkhafgy.islamy.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bassamkhafgy.islamy.utill.Constants.LANGUAGE_NAVIGATION.HOME_FRAGMENT
import com.bassamkhafgy.islamy.utill.Constants.LANGUAGE_NAVIGATION.IS_BUTTON_CLICKED
import com.bassamkhafgy.islamy.utill.Constants.LANGUAGE_NAVIGATION.WHICH_LANGUAGE_BUTTON
import com.bassamkhafgy.islamy.utill.Constants.Language.ARABIC_LANGUAGE
import com.bassamkhafgy.islamy.utill.Constants.Language.ENGLISH_LANGUAGE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _navigation = MutableStateFlow(0)
    val navigation: StateFlow<Int> = _navigation

    private val _languageFlow = MutableStateFlow(ARABIC_LANGUAGE)
    val languageFlow: StateFlow<String> = _languageFlow


    init {
        val isButtonClicked = sharedPreferences.getBoolean(IS_BUTTON_CLICKED, false)

        val whichLanguage = sharedPreferences.getString(WHICH_LANGUAGE_BUTTON, ENGLISH_LANGUAGE)

        if (isButtonClicked) {
            viewModelScope.launch {
                _languageFlow.emit(whichLanguage!!)
                _navigation.emit(HOME_FRAGMENT)
            }
        } else {
            Unit
        }
    }

    fun isButtonClickedFunction(language: String) {
        sharedPreferences.edit().putBoolean(IS_BUTTON_CLICKED, true).apply()
        sharedPreferences.edit().putString(WHICH_LANGUAGE_BUTTON, language).apply()
    }

}