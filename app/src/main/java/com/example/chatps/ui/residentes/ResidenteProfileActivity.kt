package com.example.chatps.ui.residentes

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.example.chatps.R
import com.example.chatps.databinding.ActivityResidenteProfileBinding
import com.example.chatps.ui.chat.models.User
import com.example.chatps.ui.chat.utilities.Constants
import com.example.chatps.ui.chat.utilities.PreferenceManager
import com.example.chatps.ui.mascotas.Pet
import com.example.chatps.ui.mascotas.PetListener
import com.example.chatps.ui.mascotas.PetProfileActivity
import com.example.chatps.ui.mascotas.PetsAdapter
import com.example.chatps.ui.slideshow.Vehicle
import com.example.chatps.ui.slideshow.VehicleListener
import com.example.chatps.ui.slideshow.VehicleProfileActivity
import com.example.chatps.ui.slideshow.VehiclesAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class ResidenteProfileActivity : AppCompatActivity(), PetListener, VehicleListener {

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var binding: ActivityResidenteProfileBinding
    private lateinit var database: FirebaseFirestore
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResidenteProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura LayoutManagers para RecyclerView
        binding.mascotasView.layoutManager = LinearLayoutManager(this)
        binding.vehiclesView.layoutManager = LinearLayoutManager(this)

        // Inicializa otros componentes
        database = FirebaseFirestore.getInstance()
        preferenceManager = PreferenceManager(applicationContext)
        user = intent.getParcelableExtra(Constants.KEY_USER) ?: User()

        val userId = intent.getStringExtra(Constants.KEY_USER_ID) ?: run {
            Log.e("ResidenteProfileActivity", "User ID is null")
            return
        }

        getUser(userId)
        getPets(userId)
        getVehicles(userId)

        binding.imageBack.setOnClickListener {
            finish()
        }
    }

    private fun getUser(userId: String) {
        database.collection(Constants.KEY_COLLECTION_USERS).document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString(Constants.KEY_NAME)
                    val phone = document.getString(Constants.KEY_PHONE)
                    val email = document.getString(Constants.KEY_EMAIL)
                    val cedula = document.getString(Constants.KEY_CEDULA)
                    val vivienda = document.getString(Constants.KEY_VIVIENDA)
                    val imageString = document.getString(Constants.KEY_IMAGE)

                    binding.textName.text = name ?: "No Registra"
                    binding.textPhone.text = phone ?: "No Registra"
                    binding.textEmail.text = email ?: "No Registra"
                    binding.textCedula.text = cedula ?: "No Registra"
                    binding.textVivienda.text = vivienda ?: "No Registra"

                    if (imageString != null) {
                        try {
                            Glide.with(this)
                                .asBitmap()
                                .load(Base64.decode(imageString, Base64.DEFAULT))
                                .apply(RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
                                .into(binding.imageProfile)
                        } catch (e: IllegalArgumentException) {
                            Log.e("ResidenteProfileActivity", "Error decoding image", e)
                            binding.imageProfile.setImageResource(R.drawable.default_profile_image)
                        }
                    } else {
                        binding.imageProfile.setImageResource(R.drawable.default_profile_image)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("ResidenteProfileActivity", "Error getting user", e)
                binding.imageProfile.setImageResource(R.drawable.default_profile_image)
            }
    }

    private fun getPets(userId: String) {
        loading(true)

        // Fetch pets where KEY_PET_OWNER_ID matches the user ID from the Intent
        database.collection(Constants.KEY_COLLECTION_PET)
            .whereEqualTo(Constants.KEY_PET_OWNER_ID, userId)
            .get()
            .addOnCompleteListener { task ->
                loading(false)
                if (task.isSuccessful && task.result != null) {
                    Log.d("ResidenteProfileActivity", "Pets fetched successfully")
                    val pets: MutableList<Pet> = ArrayList()
                    for (queryDocumentSnapshot in task.result!!) {
                        val pet = Pet().apply {
                            petName = queryDocumentSnapshot.getString(Constants.KEY_PET_NAME)
                            petRaza = queryDocumentSnapshot.getString(Constants.KEY_PET_RAZA)
                            petImage = queryDocumentSnapshot.getString(Constants.KEY_PET_IMAGE)
                            petId = queryDocumentSnapshot.id
                        }
                        pets.add(pet)
                    }
                    if (pets.isNotEmpty()) {
                        val petsAdapter = PetsAdapter(pets, this)
                        binding.mascotasView.adapter = petsAdapter
                        binding.mascotasView.visibility = View.VISIBLE
                    } else {
                        showErrorMessagePet("No tienes mascotas")
                    }
                } else {
                    Log.e("ResidenteProfileActivity", "Error fetching pets", task.exception)
                    showErrorMessagePet("Error al cargar mascotas")
                }
            }
    }

    private fun getVehicles(userId: String) {
        loading(true)
        // Fetch vehicles where KEY_VEHICLE_OWNER_ID matches the user ID from the Intent
        database.collection(Constants.KEY_COLLECTION_VEHICLE)
            .whereEqualTo(Constants.KEY_VEHICLE_OWNER_ID, userId)
            .get()
            .addOnCompleteListener { task ->
                loading(false)
                if (task.isSuccessful && task.result != null) {
                    Log.d("ResidenteProfileActivity", "Vehicles fetched successfully")
                    val vehicles: MutableList<Vehicle> = ArrayList()
                    for (queryDocumentSnapshot in task.result!!) {
                        val vehicle = Vehicle().apply {
                            vehicleMarca = queryDocumentSnapshot.getString(Constants.KEY_VEHICLE_MARCA)
                            vehiclePlaca = queryDocumentSnapshot.getString(Constants.KEY_VEHICLE_PLACA)
                            vehicleImage = queryDocumentSnapshot.getString(Constants.KEY_VEHICLE_IMAGE)
                            vehicleId = queryDocumentSnapshot.id
                        }
                        vehicles.add(vehicle)
                    }
                    if (vehicles.isNotEmpty()) {
                        val vehicleAdapter = VehiclesAdapter(vehicles, this)
                        binding.vehiclesView.adapter = vehicleAdapter
                        binding.vehiclesView.visibility = View.VISIBLE
                    } else {
                        showErrorMessageVehicle("No tienes vehículos")
                    }
                } else {
                    Log.e("ResidenteProfileActivity", "Error fetching vehicles", task.exception)
                    showErrorMessageVehicle("Error al cargar vehículos")
                }
            }
    }

    private fun showErrorMessagePet(message: String) {
        binding.textErrorMessagePet.text = message
        binding.textErrorMessagePet.visibility = View.VISIBLE
    }

    private fun showErrorMessageVehicle(message: String) {
        binding.textErrorMessageVehicle.text = message
        binding.textErrorMessageVehicle.visibility = View.VISIBLE
    }

    private fun loading(isLoading: Boolean) {
        binding.progressBarPet.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
        binding.progressBarVehicle.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    override fun onPetClicked(pet: Pet?) {
        /*pet?.let {
            val intent = Intent( this, PetProfileActivity::class.java)
            intent.putExtra(Constants.KEY_PET_ID, it.petId)
            startActivity(intent)
        }*/
    }

    override fun onVehicleClicked(vehicle: Vehicle?) {
        /*vehicle?.let {
            val intent = Intent(this, VehicleProfileActivity::class.java)
            intent.putExtra(Constants.KEY_VEHICLE_ID, it.vehicleId)
            startActivity(intent)
        }*/
    }
}

