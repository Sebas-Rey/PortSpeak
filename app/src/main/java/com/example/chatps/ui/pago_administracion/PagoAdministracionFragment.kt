package com.example.chatps.ui.pago_administracion

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.chatps.R
import com.example.chatps.databinding.FragmentPagoAdministracionBinding
import com.example.chatps.ui.chat.utilities.Constants
import com.example.chatps.ui.chat.utilities.PreferenceManager
import com.example.chatps.ui.pagos.actividad_pago1
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class PagoAdministracionFragment : Fragment() {

    private var _binding: FragmentPagoAdministracionBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPagoAdministracionBinding.inflate(inflater, container, false)
        preferenceManager = PreferenceManager(requireContext())

        setupListeners()
        loadDetails()

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.NEQUI -> {
                    Toast.makeText(requireContext(), "Nequi", Toast.LENGTH_SHORT).show()
                }
                R.id.PSE -> {
                    Toast.makeText(requireContext(), "PSE", Toast.LENGTH_SHORT).show()
                }
                R.id.BANCOLOMBIA -> {
                    Toast.makeText(requireContext(), "Bancolombia", Toast.LENGTH_SHORT).show()
                }
                // Agrega más casos según sea necesario
            }
        }

        return binding.root
    }

    private fun setupListeners() {
        binding.iconCalendar.setOnClickListener {
            showDatePickerDialog()
        }

        binding.buttonGuardar.setOnClickListener {
            if (validateFields()) {
                addPagoToDatabase()
            } else {
                Toast.makeText(requireContext(), "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.fechaPago.text = selectedDate
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun loadDetails() {
        binding.userName.text = preferenceManager.getString(Constants.KEY_NAME)
        binding.apartamentoUsuario.text = preferenceManager.getString(Constants.KEY_VIVIENDA)
    }

    private fun addPagoToDatabase() {
        loading(true)
        val database = FirebaseFirestore.getInstance()
        val userId = preferenceManager.getString(Constants.KEY_USER_ID)
        val userName = preferenceManager.getString(Constants.KEY_NAME)
        val userApto = preferenceManager.getString(Constants.KEY_VIVIENDA)

        // Get selected RadioButton text
        val selectedRadioButtonId = binding.radioGroup.checkedRadioButtonId
        val selectedRadioButton = binding.root.findViewById<RadioButton>(selectedRadioButtonId)
        val selectedPaymentMethod = selectedRadioButton?.text.toString()

        val visitante = hashMapOf(
            Constants.KEY_ADMIN_USER to userName,
            Constants.KEY_ADMIN_APARTMENT to userApto,
            Constants.KEY_ADMIN_DATE to binding.fechaPago.text.toString(),
            Constants.KEY_ADMIN_PAY to binding.cantidadPago.text.toString(),
            Constants.KEY_ADMIN_PAY_METHOD to selectedPaymentMethod, // Save selected payment method
            Constants.KEY_ADMIN_USER_ID to userId
        )

        database.collection(Constants.KEY_COLLECTION_ADMIN)
            .add(visitante)
            .addOnSuccessListener { documentReference ->
                loading(false)
                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                preferenceManager.putString(Constants.KEY_ADMIN_ID, documentReference.id)
                preferenceManager.putString(Constants.KEY_ADMIN_USER, userName)
                preferenceManager.putString(Constants.KEY_ADMIN_APARTMENT, userApto)
                preferenceManager.putString(Constants.KEY_ADMIN_DATE, binding.fechaPago.text.toString())
                preferenceManager.putString(Constants.KEY_ADMIN_PAY, binding.cantidadPago.text.toString())
                preferenceManager.putString(Constants.KEY_ADMIN_PAY_METHOD, selectedPaymentMethod)
                preferenceManager.putString(Constants.KEY_ADMIN_USER_ID, userId)
                Toast.makeText(requireContext(), "Pago guardado correctamente", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), actividad_pago1::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                loading(false)
                Toast.makeText(requireContext(), "Error al guardar el pago", Toast.LENGTH_SHORT).show()
            }
    }

    private fun validateFields(): Boolean {
        return binding.fechaPago.text.isNotEmpty() &&
                binding.cantidadPago.text.isNotEmpty()
    }

    private fun loading(isLoading: Boolean) {
        binding.buttonGuardar.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}