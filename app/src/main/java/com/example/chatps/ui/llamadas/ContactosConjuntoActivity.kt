package com.example.chatps.ui.llamadas

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.chatps.databinding.ActivityContactosConjuntoBinding
import com.example.chatps.ui.chat.activities.BaseActivity
import com.example.chatps.ui.chat.listeners.UserListener
import com.example.chatps.ui.chat.models.User
import com.example.chatps.ui.chat.utilities.Constants
import com.example.chatps.ui.chat.utilities.PreferenceManager
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class ContactosConjuntoActivity : BaseActivity(), UserListener {

    companion object {
        internal const val PERMISSION_CALL_PHONE = 1
    }

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var binding: ActivityContactosConjuntoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactosConjuntoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(applicationContext)
        setListeners()
        getUsers()
    }

    private fun setListeners() {
        binding.imageBack.setOnClickListener { onBackPressed() }
    }

    private fun getUsers() {
        loading(true)
        val database = FirebaseFirestore.getInstance()
        database.collection(Constants.KEY_COLLECTION_USERS)
            .whereEqualTo(Constants.KEY_ROL, "Residente")
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
        binding.textErrorMessage.setText(String.format("%s", "Usuario no disponible"))
        binding.textErrorMessage.setVisibility(View.VISIBLE)
    }

    private fun loading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    override fun onUserClicked(user: User?) {
        user?.let {
            // Handle user click event
            makePhoneCall(it.phone) // Ensure User class has a `phone` property or equivalent
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
            // Handle permission granted, if needed
            Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }
}
