package com.example.chatps.ui

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.example.chatps.R
import com.example.chatps.databinding.ActivityMainResidenteBinding
import com.example.chatps.ui.chat.activities.SignInActivity
import com.example.chatps.ui.chat.utilities.Constants
import com.example.chatps.ui.chat.utilities.PreferenceManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.makeramen.roundedimageview.RoundedImageView

class MainResidenteActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainResidenteBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager = PreferenceManager(this)
        binding = ActivityMainResidenteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayoutResidente
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_pago_administrador, R.id.nav_Control_Visitantes, R.id.nav_mascotas
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        updateUserDetails()

        binding.appBarMain.signOut.setOnClickListener {
            signOut()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun updateUserDetails() {
        val headerView = binding.navView.getHeaderView(0)
        val nameTextView = headerView.findViewById<TextView>(R.id.textName)
        val rolTextView = headerView.findViewById<TextView>(R.id.textRol)
        val profileImageView = headerView.findViewById<RoundedImageView>(R.id.imageProfile)

        val name = preferenceManager.getString(Constants.KEY_NAME)
        val rol = preferenceManager.getString(Constants.KEY_ROL)
        val imageString = preferenceManager.getString(Constants.KEY_IMAGE)

        nameTextView.text = name
        rolTextView.text = rol

        if (!imageString.isNullOrEmpty()) {
            try {
                val decodedImage = Base64.decode(imageString, Base64.DEFAULT)
                Glide.with(this)
                    .asBitmap()
                    .load(decodedImage)
                    .apply(RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
                    .into(profileImageView)
            } catch (e: IllegalArgumentException) {
                profileImageView.setImageResource(R.drawable.default_profile_image)
                e.printStackTrace()
            }
        } else {
            profileImageView.setImageResource(R.drawable.default_profile_image)
        }
    }

    private fun signOut() {
        Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show()

        val database = FirebaseFirestore.getInstance()
        val userId = preferenceManager.getString(Constants.KEY_USER_ID)

        if (userId.isNullOrEmpty()) {
            Toast.makeText(this, "Error al obtener el ID del usuario", Toast.LENGTH_SHORT).show()
            return
        }

        val documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(userId)
        val updates = hashMapOf<String, Any>(Constants.KEY_FCM_TOKEN to FieldValue.delete())

        documentReference.update(updates)
            .addOnSuccessListener {
                preferenceManager.clear()
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "No se ha podido cerrar sesión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
