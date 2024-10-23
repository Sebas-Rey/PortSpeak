package com.example.chatps.ui.mascotas

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatps.R
import com.example.chatps.databinding.ActivityAddPetBinding
import com.example.chatps.ui.chat.utilities.Constants
import com.example.chatps.ui.chat.utilities.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream

class AddPetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPetBinding
    private lateinit var preferenceManager: PreferenceManager
    private var encodedImage: String? = null

    private val itemsClass: Array<String> = arrayOf("Perro", "Gato", "Ave")
    private val itemsVacunas: Array<String> = arrayOf("Sí", "No")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPetBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())

        preferenceManager = PreferenceManager(applicationContext)
        petItemsClass()
        petItemsVacunas()
        setListeners()
    }

    private fun petItemsClass(){
        val adapterItems = ArrayAdapter(this, R.layout.list_item, itemsClass)
        binding.petClass.setAdapter(adapterItems)

        binding.petClass.setOnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position).toString()
            Toast.makeText(this, "Item: $item", Toast.LENGTH_SHORT).show()
        }
    }

    private fun petItemsVacunas() {
        val adapterItems = ArrayAdapter(this, R.layout.list_item, itemsVacunas)
        binding.petVacunas.setAdapter(adapterItems)

        binding.petVacunas.setOnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position).toString()
            Toast.makeText(this, "Item: $item", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setListeners() {
        binding.buttonAceptar.setOnClickListener {
            if (isValidSignUpDetails()) {
                petDates()
            }
        }
        binding.imagePet.setOnClickListener {
            openGallery()
        }
        binding.textCancel.setOnClickListener {
            finish()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            if (selectedImageUri != null) {
                try {
                    val inputStream: InputStream? = contentResolver.openInputStream(selectedImageUri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    binding.imagePet.setImageBitmap(bitmap)
                    encodedImage = encodeImage(bitmap)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun encodeImage(bitmap: Bitmap): String {
        val previewWidth = 150
        val previewHeight = bitmap.height * previewWidth / bitmap.width
        val previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false)
        val byteArrayOutputStream = ByteArrayOutputStream()

        // Utilizar formato PNG para evitar la compresión
        previewBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        //previewBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

        val bytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private fun petDates() {
        loading(true)
        val database = FirebaseFirestore.getInstance()
        val userId = preferenceManager.getString(Constants.KEY_USER_ID) // Retrieve user ID from PreferenceManager
        val pet = hashMapOf(
            Constants.KEY_PET_NAME to binding.petName.text.toString(),
            Constants.KEY_PET_RAZA to binding.petRaza.text.toString(),
            Constants.KEY_PET_SEXO to binding.petSexo.text.toString(),
            Constants.KEY_PET_CLASS to binding.petClass.text.toString(),
            Constants.KEY_PET_VACUNAS to binding.petVacunas.text.toString(),
            Constants.KEY_PET_IMAGE to encodedImage,
            Constants.KEY_PET_OWNER_ID to userId // Add the user ID here
        )
        database.collection(Constants.KEY_COLLECTION_PET)
            .add(pet)
            .addOnSuccessListener { documentReference ->
                loading(false)
                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                preferenceManager.putString(Constants.KEY_PET_ID, documentReference.id)
                preferenceManager.putString(Constants.KEY_PET_NAME, binding.petName.text.toString())
                preferenceManager.putString(Constants.KEY_PET_RAZA, binding.petRaza.text.toString())
                preferenceManager.putString(Constants.KEY_PET_SEXO, binding.petSexo.text.toString())
                preferenceManager.putString(Constants.KEY_PET_CLASS, binding.petClass.text.toString())
                preferenceManager.putString(Constants.KEY_PET_VACUNAS, binding.petVacunas.text.toString())
                preferenceManager.putString(Constants.KEY_PET_OWNER_ID, userId) // Store user ID in PreferenceManager
                preferenceManager.putString(Constants.KEY_PET_IMAGE, encodedImage)
                /*val intent = Intent(applicationContext, MascotasFragment::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)*/
                finish() // Cierra la actividad y regresa al anterior fragmento/actividad
            }
            .addOnFailureListener {
                loading(false)
                Toast.makeText(this, "Failed to add pet details", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loading(isLoading: Boolean) {
        binding.buttonAceptar.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    private fun isValidSignUpDetails(): Boolean {
        return when {
            encodedImage == null -> {
                Toast.makeText(this, "Seleccione una Imagen de su Mascota", Toast.LENGTH_SHORT).show()
                false
            }
            binding.petName.text.toString().trim().isEmpty() -> {
                Toast.makeText(this, "Introduzca el Nombre de su Mascota", Toast.LENGTH_SHORT).show()
                false
            }
            binding.petClass.text.toString().trim().isEmpty() -> {
                Toast.makeText(this, "Seleccione el Tipo de Mascota", Toast.LENGTH_SHORT).show()
                false
            }
            binding.petRaza.text.toString().trim().isEmpty() -> {
                Toast.makeText(this, "Introduzca la Raza de su Mascota", Toast.LENGTH_SHORT).show()
                false
            }
            binding.petSexo.text.toString().trim().isEmpty() -> {
                Toast.makeText(this, "Introduzca el Sexo de su Mascota", Toast.LENGTH_SHORT).show()
                false
            }
            binding.petVacunas.text.toString().trim().isEmpty() -> {
                Toast.makeText(this, "Seleccione Sí o No", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1000
    }
}
