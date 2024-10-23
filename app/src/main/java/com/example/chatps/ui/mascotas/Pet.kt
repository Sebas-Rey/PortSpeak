package com.example.chatps.ui.mascotas

import java.io.Serializable

data class Pet(
    var petName: String? = null,
    var petImage: String? = null,
    var petRaza: String? = null,
    var petSexo: String? = null,
    var petClass: String? = null,
    var petVacunas: String? = null,
    var petId: String? = null,
    var petOwnerId: String? = null
) : Serializable
