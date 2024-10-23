package com.example.chatps.ui.slideshow

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
import com.example.chatps.databinding.ActivityAddVehicleBinding
import com.example.chatps.ui.chat.utilities.Constants
import com.example.chatps.ui.chat.utilities.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream

class AddVehicleActivity: AppCompatActivity() {

    private lateinit var binding: ActivityAddVehicleBinding
    private lateinit var preferenceManager: PreferenceManager
    private var encodedImage: String? = null

    private val items: Array<String> = arrayOf("Automóvil", "Motocicleta", "Bicicleta")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddVehicleBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())

        preferenceManager = PreferenceManager(applicationContext)
        vehicleItemsClass()
        setListeners()
    }

    private fun vehicleItemsClass(){
        val adapterItems = ArrayAdapter(this, R.layout.list_item, items)
        binding.tipoVehiculo.setAdapter(adapterItems)

        binding.tipoVehiculo.setOnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position).toString()
            Toast.makeText(this, "Item: $item", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setListeners() {
        binding.buttonAceptar.setOnClickListener {
            if (isValidSignUpDetails()) {
                vehicleDates()
            }
        }
        binding.imageVehiculo.setOnClickListener {
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
                    binding.imageVehiculo.setImageBitmap(bitmap)
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

    private fun vehicleDates() {
        loading(true)
        val database = FirebaseFirestore.getInstance()
        val userId = preferenceManager.getString(Constants.KEY_USER_ID) // Retrieve user ID from PreferenceManager
        val vehicle = hashMapOf(
            Constants.KEY_VEHICLE_TYPE to binding.tipoVehiculo.text.toString(),
            Constants.KEY_VEHICLE_PLACA to binding.placaVehiculo.text.toString(),
            Constants.KEY_VEHICLE_MARCA to binding.marcaVehiculo.text.toString(),
            Constants.KEY_VEHICLE_MODEL to binding.modeloVehiculo.text.toString(),
            Constants.KEY_VEHICLE_COLOR to binding.colorVehiculo.text.toString(),
            Constants.KEY_VEHICLE_PARKING to binding.parqueaderoVehiculo.text.toString(),
            Constants.KEY_VEHICLE_IMAGE to encodedImage,
            Constants.KEY_VEHICLE_OWNER_ID to userId // Add the user ID here
        )
        database.collection(Constants.KEY_COLLECTION_VEHICLE)
            .add(vehicle)
            .addOnSuccessListener { documentReference ->
                loading(false)
                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                preferenceManager.putString(Constants.KEY_VEHICLE_ID, documentReference.id)
                preferenceManager.putString(Constants.KEY_VEHICLE_TYPE, binding.tipoVehiculo.text.toString())
                preferenceManager.putString(Constants.KEY_VEHICLE_PLACA, binding.placaVehiculo.text.toString())
                preferenceManager.putString(Constants.KEY_VEHICLE_MARCA, binding.marcaVehiculo.text.toString())
                preferenceManager.putString(Constants.KEY_VEHICLE_MODEL, binding.modeloVehiculo.text.toString())
                preferenceManager.putString(Constants.KEY_VEHICLE_COLOR, binding.colorVehiculo.text.toString())
                preferenceManager.putString(Constants.KEY_VEHICLE_PARKING, binding.parqueaderoVehiculo.text.toString())
                preferenceManager.putString(Constants.KEY_VEHICLE_OWNER_ID, userId) // Store user ID in PreferenceManager
                preferenceManager.putString(Constants.KEY_VEHICLE_IMAGE, encodedImage)
                /*val intent = Intent(applicationContext, SlideshowFragment::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)*/
                finish() // Cierra la actividad y regresa al anterior fragmento/actividad
            }
            .addOnFailureListener {
                loading(false)
                Toast.makeText(this, "Failed to add vehicle details", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loading(isLoading: Boolean) {
        binding.buttonAceptar.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    private fun isValidSignUpDetails(): Boolean {
        return when {
            encodedImage == null -> {
                Toast.makeText(this, "Seleccione una Imagen de su Vehículo", Toast.LENGTH_SHORT).show()
                false
            }
            binding.tipoVehiculo.text.toString().trim().isEmpty() -> {
                Toast.makeText(this, "Seleccione el Tipo de su Vehículo", Toast.LENGTH_SHORT).show()
                false
            }
            binding.placaVehiculo.text.toString().trim().isEmpty() -> {
                Toast.makeText(this, "Introduzca la Placa de su Vehículo", Toast.LENGTH_SHORT).show()
                false
            }
            binding.marcaVehiculo.text.toString().trim().isEmpty() -> {
                Toast.makeText(this, "Introduzca la Marca de su Vehículo", Toast.LENGTH_SHORT).show()
                false
            }
            binding.modeloVehiculo.text.toString().trim().isEmpty() -> {
                Toast.makeText(this, "Introduzca el Modelo de su Vehículo", Toast.LENGTH_SHORT).show()
                false
            }
            binding.colorVehiculo.text.toString().trim().isEmpty() -> {
                Toast.makeText(this, "Introduzca el Color de su Vehículo", Toast.LENGTH_SHORT).show()
                false
            }
            binding.parqueaderoVehiculo.text.toString().trim().isEmpty() -> {
                Toast.makeText(this, "Introduzca el Parqueadero de su Vehículo", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1000
    }
}
