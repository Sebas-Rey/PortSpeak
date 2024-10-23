package com.example.chatps.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.chatps.R
import com.example.chatps.databinding.ActivityLlamadasBinding
import com.example.chatps.ui.chat.adapters.UsersAdapter
import com.example.chatps.ui.chat.listeners.UserListener
import com.example.chatps.ui.chat.models.User
import com.example.chatps.ui.chat.utilities.Constants
import com.example.chatps.ui.chat.utilities.PreferenceManager
import com.example.chatps.ui.llamadas.ContactosConjuntoActivity
import com.example.chatps.ui.llamadas.LlamadasAdapter
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class LlamadasActivity : AppCompatActivity(), UserListener {

    companion object {
        internal const val PERMISSION_CALL_PHONE = 1
    }

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var binding: ActivityLlamadasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLlamadasBinding.inflate(layoutInflater)
        setContentView(binding.root) // Usa binding.root en lugar de R.layout.activity_llamadas
        preferenceManager = PreferenceManager(applicationContext)

        setListeners()
        getUsers()
    }

    private fun setListeners() {
        binding.imageHome.setOnClickListener {
            finish()
        }

        binding.otrasLlamadas.setOnClickListener {
            val intent = Intent(this, ContactosConjuntoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getUsers() {
        loading(true)
        val database = FirebaseFirestore.getInstance()
        val roles = listOf("Vigilante", "Administrador")
        database.collection(Constants.KEY_COLLECTION_USERS)
            .whereIn(Constants.KEY_ROL, roles)
            .get()
            .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                loading(false)
                val currentUserId = preferenceManager.getString(Constants.KEY_USER_ID)
                if (task.isSuccessful && task.result != null) {
                    val users: MutableList<User> = ArrayList()
                    for (queryDocumentSnapshot in task.result!!) {
                        if (currentUserId == queryDocumentSnapshot.id) {
                            continue
                        }
                        val user = User().apply {
                            name = queryDocumentSnapshot.getString(Constants.KEY_NAME)
                            phone = queryDocumentSnapshot.getString(Constants.KEY_PHONE)
                            image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE)
                            id = queryDocumentSnapshot.id
                        }
                        users.add(user)
                    }
                    if (users.isNotEmpty()) {
                        val llamadasAdapter = LlamadasAdapter(users, this)
                        binding.usersRecyclerView.adapter = llamadasAdapter
                        binding.usersRecyclerView.visibility = View.VISIBLE
                    } else {
                        showErrorMessage()
                    }
                } else {
                    showErrorMessage()
                }
            }
    }

    private fun showErrorMessage() {
        binding.textErrorMessage.text = "Usuario no disponible"
        binding.textErrorMessage.visibility = View.VISIBLE
    }

    private fun loading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    override fun onUserClicked(user: User?) {
        user?.let {
            // Handle user click event
            makePhoneCall(it.phone) // Asegúrate de que la clase User tenga una propiedad `phone`
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun makePhoneCall(number: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), PERMISSION_CALL_PHONE)
        } else {
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$number")
            }
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No se encontró una aplicación para realizar llamadas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CALL_PHONE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }
}