package com.example.chatps.ui.slideshow

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.chatps.databinding.FragmentSlideshowBinding
import com.example.chatps.ui.chat.utilities.Constants
import com.example.chatps.ui.chat.utilities.PreferenceManager
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class SlideshowFragment : Fragment(), VehicleListener {

    private lateinit var database: FirebaseFirestore
    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var currentUserId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        database = FirebaseFirestore.getInstance()
        preferenceManager = PreferenceManager(requireContext())
        currentUserId = preferenceManager.getString(Constants.KEY_USER_ID) ?: ""

        binding.addVehicle.setOnClickListener {
            val intent = Intent(requireContext(), AddVehicleActivity::class.java)
            startActivity(intent)
        }

        getVehicles()

        return root
    }

    private fun getVehicles() {
        loading(true)

        // Fetch pets where KEY_PET_OWNER_ID matches the current user's ID
        database.collection(Constants.KEY_COLLECTION_VEHICLE)
            .whereEqualTo(Constants.KEY_VEHICLE_OWNER_ID, currentUserId)
            .get()
            .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                loading(false)
                if (task.isSuccessful && task.result != null) {
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
                        binding.vehiculoView.adapter = vehicleAdapter
                        binding.vehiculoView.visibility = View.VISIBLE
                    } else {
                        showErrorMessage("No tienes vehículos")
                    }
                } else {
                    showErrorMessage("Error al cargar vehículos")
                }
            }
    }

    private fun showErrorMessage(message: String) {
        binding.textErrorMessage.text = message
        binding.textErrorMessage.visibility = View.VISIBLE
    }

    private fun loading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    override fun onVehicleClicked(vehicle: Vehicle?) {
        val intent = Intent(requireContext(), VehicleProfileActivity::class.java)
        intent.putExtra(Constants.KEY_VEHICLE_ID, vehicle?.vehicleId) // Pasar ID del vehículo
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}