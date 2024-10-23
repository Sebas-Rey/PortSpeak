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
import com.example.chatps.databinding.ActivityMainVigilanteBinding
import com.example.chatps.ui.chat.activities.SignInActivity
import com.example.chatps.ui.chat.utilities.Constants
import com.example.chatps.ui.chat.utilities.PreferenceManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.makeramen.roundedimageview.RoundedImageView

class MainVigilanteActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainVigilanteBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager = PreferenceManager(this)
        binding = ActivityMainVigilanteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayoutVigilante
        val navView: NavigationView = binding.navView
        //val headerView: View = navigationView.getHeaderView(0)
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_residentes, R.id.nav_Control_Visitantes
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        userDetails()

        binding.appBarMain.signOut.setOnClickListener{
            signOut()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun userDetails() {
        val name = preferenceManager.getString(Constants.KEY_NAME)
        binding.navView.getHeaderView(0).findViewById<TextView>(R.id.textName).text = name

        val rol = preferenceManager.getString(Constants.KEY_ROL)
        binding.navView.getHeaderView(0).findViewById<TextView>(R.id.textRol).text = rol

        val imageString = preferenceManager.getString(Constants.KEY_IMAGE)
        if (imageString != null) {
            try {
                Glide.with(this)
                    .asBitmap()
                    .load(Base64.decode(imageString, Base64.DEFAULT))
                    .apply(RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
                    .into(binding.navView.getHeaderView(0).findViewById<RoundedImageView>(R.id.imageProfile))
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                binding.navView.getHeaderView(0).findViewById<ImageView>(R.id.imageProfile).setImageResource(
                    R.drawable.default_profile_image)
            }
        } else {
            binding.navView.getHeaderView(0).findViewById<ImageView>(R.id.imageProfile).setImageResource(
                R.drawable.default_profile_image)
        }
    }

    fun signOut() {
        Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show()

        val database = FirebaseFirestore.getInstance()
        val userId = preferenceManager.getString(Constants.KEY_USER_ID)

        if (userId.isNullOrEmpty()) {
            // Handle the case where userId is null or empty
            Toast.makeText(this, "Error al obtener el ID del usuario", Toast.LENGTH_SHORT).show()
            return
        }

        val documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
            .document(userId)

        val updates = hashMapOf<String, Any>(Constants.KEY_FCM_TOKEN to FieldValue.delete())

        documentReference.update(updates)
            .addOnSuccessListener {
                // Clear user preferences and navigate to SignInActivity
                preferenceManager.clear()
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                // Handle failure to sign out
                Toast.makeText(this, "No se ha podido cerrar sesión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}