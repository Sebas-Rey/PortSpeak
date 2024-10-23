package com.example.chatps.ui.slideshow

import android.os.Bundle
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.example.chatps.R
import com.example.chatps.databinding.ActivityVehicleProfileBinding
import com.example.chatps.ui.chat.utilities.Constants
import com.example.chatps.ui.chat.utilities.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore

class VehicleProfileActivity : AppCompatActivity() {

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var binding: ActivityVehicleProfileBinding
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehicleProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseFirestore.getInstance()
        preferenceManager = PreferenceManager(applicationContext)

        val vehicleId = intent.getStringExtra(Constants.KEY_VEHICLE_ID)
        if (vehicleId != null) {
            loadVehicleDetails(vehicleId)
        }

        binding.imageBack.setOnClickListener{
            finish()
        }
    }

    private fun loadVehicleDetails(vehicleId: String) {
        database.collection(Constants.KEY_COLLECTION_VEHICLE).document(vehicleId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val tipo = document.getString(Constants.KEY_VEHICLE_TYPE)
                    val marca = document.getString(Constants.KEY_VEHICLE_MARCA)
                    val placa = document.getString(Constants.KEY_VEHICLE_PLACA)
                    val modelo = document.getString(Constants.KEY_VEHICLE_MODEL)
                    val color = document.getString(Constants.KEY_VEHICLE_COLOR)
                    val parqueadero = document.getString(Constants.KEY_VEHICLE_PARKING)
                    val imageString = document.getString(Constants.KEY_VEHICLE_IMAGE)

                    binding.tipoVehiculo.text = tipo ?: "No Registra"
                    binding.marcaVehiculo.text = marca ?: "No Registra"
                    binding.placaVehiculo.text = placa ?: "No Registra"
                    binding.modeloVehiculo.text = modelo ?: "No Registra"
                    binding.colorVehiculo.text = color ?: "No Registra"
                    binding.parqueaderoVehiculo.text = parqueadero ?: "No Registra"

                    if (imageString != null) {
                        try {
                            Glide.with(this)
                                .asBitmap()
                                .load(Base64.decode(imageString, Base64.DEFAULT))
                                .apply(RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
                                .into(binding.imageProfile)
                        } catch (e: IllegalArgumentException) {
                            e.printStackTrace()
                            binding.imageProfile.setImageResource(R.drawable.default_profile_image)
                        }
                    } else {
                        binding.imageProfile.setImageResource(R.drawable.default_profile_image)
                    }
                }
            }
            .addOnFailureListener {
                binding.imageProfile.setImageResource(R.drawable.default_profile_image)
            }
    }
}
