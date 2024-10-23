package com.example.chatps.ui.pago_administracion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PagoAdministradorViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
    }
    val text: LiveData<String> = _text

}