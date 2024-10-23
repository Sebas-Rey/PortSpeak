package com.example.chatps.ui.pagos

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.example.chatps.R
import com.example.chatps.databinding.ActivityActividadPago1Binding
import com.example.chatps.ui.MainResidenteActivity
import com.example.chatps.ui.MainVigilanteActivity
import com.example.chatps.ui.chat.utilities.Constants
import com.example.chatps.ui.chat.utilities.PreferenceManager
import com.example.chatps.ui.pago_administracion.PagoAdministracionFragment
import com.google.firebase.firestore.FirebaseFirestore

class actividad_pago1 : AppCompatActivity() {

    private lateinit var binding: ActivityActividadPago1Binding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityActividadPago1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseFirestore.getInstance()
        preferenceManager = PreferenceManager(applicationContext)

        /*binding.imageBack.setOnClickListener {
            val intent = Intent(this, MainResidenteActivity::class.java)
            startActivity(intent)
            finish()
        }*/

        binding.buttonPagar.setOnClickListener {
            Toast.makeText(this, "Pago exitoso", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainResidenteActivity::class.java)
            startActivity(intent)
            finish()
        }

        payDetails()
    }

    fun payDetails() {
        try {
            val user = preferenceManager.getString(Constants.KEY_ADMIN_USER)
            val apto = preferenceManager.getString(Constants.KEY_ADMIN_APARTMENT)
            val fecha = preferenceManager.getString(Constants.KEY_ADMIN_DATE)
            val cantidad = preferenceManager.getString(Constants.KEY_ADMIN_PAY)
            val payMethod = preferenceManager.getString(Constants.KEY_ADMIN_PAY_METHOD)

            binding.nombre.text = if (user != null) user else "No Registra"
            binding.apartamento.text = if (apto != null) apto else "No Registra"
            binding.fechaPago.text = if (fecha != null) fecha else "No Registra"
            binding.cantidadPago.text = if (cantidad != null) cantidad else "No Registra"
            binding.payMethod.text = if (payMethod != null) payMethod else "No Registra"
        } catch (e: Exception) {
            e.printStackTrace() // Imprime el error en Logcat
            Toast.makeText(this, "Error al cargar detalles de pago", Toast.LENGTH_SHORT).show()
        }
    }

}
