package com.example.chatps.ui.gallery

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.example.chatps.R
import com.example.chatps.databinding.FragmentGalleryBinding
import com.example.chatps.ui.chat.utilities.Constants
import com.example.chatps.ui.chat.utilities.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseStorage: FirebaseStorage
    private var imageUri: Uri? = null

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                imageUri = uri
                subirPhoto(imageUri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        preferenceManager = PreferenceManager(requireContext())

        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()

        userDetails()
        viviendaView()
        setListeners()

        return binding.root
    }

    private fun setListeners() {
        binding.addPhoto.setOnClickListener { uploadPhoto() }
        binding.buttonActualizar.setOnClickListener {
            // Implementar la lógica de actualización aquí
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun userDetails() {
        with(preferenceManager) {
            binding.textName.text = getString(Constants.KEY_NAME) ?: "Error"
            binding.textPhone.text = getString(Constants.KEY_PHONE) ?: "Error"
            binding.textEmail.text = getString(Constants.KEY_EMAIL) ?: "Error"
            binding.textCedula.text = getString(Constants.KEY_CEDULA) ?: "Error"
            binding.textVivienda.text = getString(Constants.KEY_VIVIENDA) ?: "Error"
            binding.textPassword.text = getString(Constants.KEY_PASSWORD) ?: "Error"
        }
        loadProfileImage()
    }

    private fun loadProfileImage() {
        val imageString = preferenceManager.getString(Constants.KEY_IMAGE)
        if (imageString != null) {
            try {
                val decodedBytes = Base64.decode(imageString, Base64.DEFAULT)
                Glide.with(this)
                    .asBitmap()
                    .load(decodedBytes)
                    .apply(RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
                    .into(binding.imageProfile)
            } catch (e: Exception) {
                e.printStackTrace()
                binding.imageProfile.setImageResource(R.drawable.default_profile_image)
            }
        } else {
            binding.imageProfile.setImageResource(R.drawable.default_profile_image)
        }
    }

    private fun viviendaView() {
        val rol = preferenceManager.getString(Constants.KEY_ROL)
        binding.textVivienda.visibility = if ("Residente" == rol) View.VISIBLE else View.GONE
    }

// Inicio Actualizar Foto de Perfil

    private fun uploadPhoto() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        imagePickerLauncher.launch(intent)
    }

    private fun subirPhoto(imageUri: Uri?) {
        if (imageUri == null) {
            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
            return
        }

        loading(true)
        val storageReference = firebaseStorage.reference.child("images/${UUID.randomUUID()}")

        val uploadTask = storageReference.putFile(imageUri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Obtén la URL de la imagen una vez que se suba con éxito
            taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUrl ->
                val userId = preferenceManager.getString(Constants.KEY_USER_ID)

                if (userId == null) {
                    loading(false)
                    Toast.makeText(requireContext(), "Error: usuario no encontrado", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val userUpdate = hashMapOf(
                    Constants.KEY_IMAGE to downloadUrl.toString()
                )

                // Guarda el usuario en Firestore
                firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                    .document(userId)
                    .set(userUpdate)
                    .addOnSuccessListener {
                        loading(false)
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                        preferenceManager.putString(Constants.KEY_IMAGE, downloadUrl.toString())
                        Toast.makeText(requireContext(), "Imagen subida y guardada correctamente", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        loading(false)
                        Toast.makeText(requireContext(), "Error al guardar información: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }.addOnFailureListener { e ->
                loading(false)
                Toast.makeText(requireContext(), "Error al obtener la URL de la imagen: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            loading(false)
            Toast.makeText(requireContext(), "Error al cargar imagen: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserPhoto(photoUrl: String) {
        val userId = preferenceManager.getString(Constants.KEY_USER_ID) ?: return
        val userUpdate = hashMapOf(Constants.KEY_IMAGE to photoUrl)

        firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
            .document(userId)
            .update(userUpdate as Map<String, Any>)
            .addOnSuccessListener {
                loading(false)
                preferenceManager.putString(Constants.KEY_IMAGE, photoUrl)
                loadProfileImage()
                Toast.makeText(requireContext(), "Foto actualizada correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                loading(false)
                Toast.makeText(requireContext(), "Error al actualizar la foto: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

// Fin Actualizar Foto de Perfil

    private fun loading(isLoading: Boolean) {
        binding.buttonActualizar.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
