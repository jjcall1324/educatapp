package com.example.educatapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    private val _heroTitle = MutableLiveData<String>().apply {
        value = "Welcome to our Educational Platform"
    }

    val heroTitle: LiveData<String> = _heroTitle

    fun setHeroTitle(title: String) {
        _heroTitle.value = title
    }
}
