package com.example.chatps.ui.visitantes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chatps.R
import com.example.chatps.databinding.ActivityVisitanteDatesBinding
import com.example.chatps.ui.chat.utilities.Constants
import com.example.chatps.ui.chat.utilities.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore

class VisitanteDatesActivity : AppCompatActivity() {

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var binding: ActivityVisitanteDatesBinding
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisitanteDatesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseFirestore.getInstance()
        preferenceManager = PreferenceManager(applicationContext)

        val visitorId = intent.getStringExtra(Constants.KEY_VISITANTES_ID)
        if (visitorId != null) {
            loadVisitorsDetails(visitorId)
        }

        binding.imageBack.setOnClickListener{
            finish()
        }
    }

    private fun loadVisitorsDetails(visitorId: String) {
        database.collection(Constants.KEY_COLLECTION_VISITANTES).document(visitorId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val visitorName = document.getString(Constants.KEY_VISITANTES_NAME)
                    val timeEnter = document.getString(Constants.KEY_VISITANTES_TIME_ENTER)
                    val visitorApto = document.getString(Constants.KEY_VISITANTES_APARTMENT)
                    val timeExit = document.getString(Constants.KEY_VISITANTES_TIME_EXIT)
                    val visitorPhone = document.getString(Constants.KEY_VISITANTES_PHONE)
                    val visitorVehicle = document.getString(Constants.KEY_VISITANTES_TYPE_AUTOMOBILE)
                    val visitorVehicleCarac = document.getString(Constants.KEY_VISITANTES_CARACTER_AUTOMOBILE)

                    binding.visitorName.text = visitorName ?: "No Registra"

                    binding.timeEnter.text = if (timeEnter != null) "Hora de Llegada: $timeEnter" else "No Registra"
                    binding.timeEnter.setTextColor(getColor(R.color.green))

                    binding.visitorApto.text = visitorApto ?: "No Registra"

                    binding.timeExit.text = if (timeExit != null) "Hora de Salida: $timeExit" else "No Registra"
                    binding.timeExit.setTextColor(getColor(R.color.error))

                    binding.visitorPhone.text = visitorPhone ?: "No Registra"
                    binding.visitorVehicle.text = visitorVehicle ?: "No Registra"
                    binding.visitorVehicleCarac.text = visitorVehicleCarac ?: "No Registra"
                }
            }
    }

}