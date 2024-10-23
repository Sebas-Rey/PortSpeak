package com.example.chatps.ui.visitantes

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatps.R
import com.example.chatps.databinding.ActivityAddVisitantesBinding
import com.example.chatps.ui.chat.utilities.Constants
import com.example.chatps.ui.chat.utilities.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class AddVisitantesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddVisitantesBinding
    private lateinit var preferenceManager: PreferenceManager

    private val itemsVehiculo: Array<String> = arrayOf("Automóvil", "Bicicleta", "Motocicleta", "Ninguno")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddVisitantesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = PreferenceManager(applicationContext)
        visitanteItemVehiculo()
        setListeners()
    }

    private fun visitanteItemVehiculo() {
        val adapterItems = ArrayAdapter(this, R.layout.list_item, itemsVehiculo)
        binding.tipoVehiculo.setAdapter(adapterItems)

        binding.tipoVehiculo.setOnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position).toString()
            Toast.makeText(this, "Tipo de Vehículo: $item", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setListeners() {
        binding.iconCalendarEnter.setOnClickListener {
            showTimePickerDialogEnter()
        }
        binding.iconCalendarExit.setOnClickListener {
            showTimePickerDialogExit()
        }
        binding.buttonAceptar.setOnClickListener {
            if (isValidSignUpDetails()) {
                addVisitanteToDatabase()
            }
        }
        binding.textCancel.setOnClickListener {
            finish()
        }
    }

    private fun showTimePickerDialogEnter() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this, // Usa `this` en lugar de `applicationContext`
            { _, selectedHour, selectedMinute ->
                val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                binding.horaEntrada.text = selectedTime
            },
            hour, minute, false // true para formato de 24 horas, false para formato de 12 horas
        )
        timePickerDialog.show()
    }

    private fun showTimePickerDialogExit() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this, // Usa `this` en lugar de `applicationContext`
            { _, selectedHour, selectedMinute ->
                val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                binding.horaSalida.text = selectedTime
            },
            hour, minute, false // true para formato de 24 horas, false para formato de 12 horas
        )
        timePickerDialog.show()
    }

    private fun addVisitanteToDatabase() {
        loading(true)
        val database = FirebaseFirestore.getInstance()
        val userId = preferenceManager.getString(Constants.KEY_USER_ID)
        val aptoUser = preferenceManager.getString(Constants.KEY_VIVIENDA)
        val visitante = hashMapOf(
            Constants.KEY_VISITANTES_NAME to binding.nombre.text.toString(),
            Constants.KEY_VISITANTES_APARTMENT to aptoUser,
            Constants.KEY_VISITANTES_TIME_ENTER to binding.horaEntrada.text.toString(),
            Constants.KEY_VISITANTES_TIME_EXIT to binding.horaSalida.text.toString(),
            Constants.KEY_VISITANTES_PHONE to binding.telefono.text.toString(),
            Constants.KEY_VISITANTES_TYPE_AUTOMOBILE to binding.tipoVehiculo.text.toString(),
            Constants.KEY_VISITANTES_CARACTER_AUTOMOBILE to binding.caracterisicas.text.toString(),
            Constants.KEY_VISITANTES_USER_ID to userId
        )
        database.collection(Constants.KEY_COLLECTION_VISITANTES)
            .add(visitante)
            .addOnSuccessListener { documentReference ->
                loading(false)
                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                preferenceManager.putString(Constants.KEY_VISITANTES_ID, documentReference.id)
                preferenceManager.putString(Constants.KEY_VISITANTES_NAME, binding.nombre.text.toString())
                preferenceManager.putString(Constants.KEY_VISITANTES_APARTMENT, aptoUser)
                preferenceManager.putString(Constants.KEY_VISITANTES_TIME_ENTER, binding.horaEntrada.text.toString())
                preferenceManager.putString(Constants.KEY_VISITANTES_TIME_EXIT, binding.horaSalida.text.toString())
                preferenceManager.putString(Constants.KEY_VISITANTES_PHONE, binding.telefono.text.toString())
                preferenceManager.putString(Constants.KEY_VISITANTES_TYPE_AUTOMOBILE, binding.tipoVehiculo.text.toString())
                preferenceManager.putString(Constants.KEY_VISITANTES_CARACTER_AUTOMOBILE, binding.caracterisicas.text.toString())
                preferenceManager.putString(Constants.KEY_VISITANTES_USER_ID, userId)
                Toast.makeText(this, "Visitante agregado exitosamente", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                loading(false)
                Toast.makeText(this, "Error al agregar visitante: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loading(isLoading: Boolean) {
        binding.buttonAceptar.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    private fun isValidSignUpDetails(): Boolean {
        return when {
            binding.nombre.text.toString().trim().isEmpty() -> {
                showToast("Introduzca el Nombre del Visitante")
                false
            }
            binding.horaEntrada.text.toString().trim().isEmpty() -> {
                showToast("Introduzca la Hora de Entrada de la Visita")
                false
            }
            binding.horaSalida.text.toString().trim().isEmpty() -> {
                showToast("Introduzca la Hora de Salida de la Visita")
                false
            }
            binding.telefono.text.toString().trim().isEmpty() -> {
                showToast("Introduzca el Número telefónico del Visitante")
                false
            }
            binding.tipoVehiculo.text.toString().trim().isEmpty() -> {
                showToast("Seleccione el Tipo de Vehículo en el que Ingresa la Visita")
                false
            }
            binding.caracterisicas.text.toString().trim().isEmpty() -> {
                showToast("Introduzca las Características del Vehículo del Visitante")
                false
            }
            else -> true
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
