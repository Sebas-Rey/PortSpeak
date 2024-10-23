package com.example.chatps.ui.visitantes

import java.io.Serializable

data class Visit(
    var visitName: String? = null,
    var visitApartment: String? = null,
    var visitTimeEnter: String? = null,
    var visitTimeExit: String? = null,
    var visitPhone: String? = null,
    var visitVehicle: String? = null,
    var visitCaracterVehicle: String? = null,
    var visitUserId: String? = null,
    var visitId: String? = null
) : Serializable