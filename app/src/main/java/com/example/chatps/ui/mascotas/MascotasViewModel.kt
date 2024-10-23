package com.example.chatps.ui.mascotas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MascotasViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
    }
    val text: LiveData<String> = _text
}