package com.example.chatps.ui.mascotas

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.chatps.databinding.FragmentMascotasBinding
import com.example.chatps.ui.chat.utilities.Constants
import com.example.chatps.ui.chat.utilities.PreferenceManager
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class MascotasFragment : Fragment(), PetListener {

    private lateinit var database: FirebaseFirestore
    private var _binding: FragmentMascotasBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var currentUserId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMascotasBinding.inflate(inflater, container, false)
        val view = binding.root

        database = FirebaseFirestore.getInstance()
        preferenceManager = PreferenceManager(requireContext())
        currentUserId = preferenceManager.getString(Constants.KEY_USER_ID) ?: ""

        binding.addPet.setOnClickListener {
            val intent = Intent(requireContext(), AddPetActivity::class.java)
            startActivity(intent)
        }

        getPets() // Method to fetch and display pets

        return view
    }

    private fun getPets() {
        loading(true)

        // Fetch pets where KEY_PET_OWNER_ID matches the current user's ID
        database.collection(Constants.KEY_COLLECTION_PET)
            .whereEqualTo(Constants.KEY_PET_OWNER_ID, currentUserId)
            .get()
            .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                loading(false)
                if (task.isSuccessful && task.result != null) {
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
                        binding.petView.adapter = petsAdapter
                        binding.petView.visibility = View.VISIBLE
                    } else {
                        showErrorMessage("No tienes mascotas")
                    }
                } else {
                    showErrorMessage("Error al cargar mascotas")
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

    override fun onPetClicked(pet: Pet?) {
        val intent = Intent(requireContext(), PetProfileActivity::class.java)
        intent.putExtra(Constants.KEY_PET_ID, pet?.petId) // Pass the pet ID
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
