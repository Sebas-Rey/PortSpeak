package com.example.chatps.ui.mascotas

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Base64
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.example.chatps.R
import com.example.chatps.databinding.ActivityPetProfileBinding
import com.example.chatps.ui.chat.utilities.Constants
import com.example.chatps.ui.chat.utilities.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore

class PetProfileActivity : AppCompatActivity() {

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var binding: ActivityPetProfileBinding
    private lateinit var database: FirebaseFirestore

    private val canalNombre = "dev.xcheko51x"
    private val canalId = "canalId"
    private val notificationId = 0
    private val PERMISSION_REQUEST_CODE = 101

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPetProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseFirestore.getInstance()
        preferenceManager = PreferenceManager(applicationContext)

        val petId = intent.getStringExtra(Constants.KEY_PET_ID) // Obtener ID de mascota del Intent
        if (petId != null) {
            loadPetDetails(petId)
        }

        // Verificar y solicitar el permiso de notificaciones si es necesario
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), PERMISSION_REQUEST_CODE)
        } else {
            setupNotificationButton()
        }

        binding.imageBack.setOnClickListener {
            finish()
        }
    }

    private fun loadPetDetails(petId: String) {
        database.collection(Constants.KEY_COLLECTION_PET).document(petId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString(Constants.KEY_PET_NAME)
                    val raza = document.getString(Constants.KEY_PET_RAZA)
                    val tipo = document.getString(Constants.KEY_PET_CLASS)
                    val sexo = document.getString(Constants.KEY_PET_SEXO)
                    val vacunas = document.getString(Constants.KEY_PET_VACUNAS)
                    val imageString = document.getString(Constants.KEY_PET_IMAGE)

                    binding.petName.text = name ?: "No Registra"
                    binding.petRaza.text = raza ?: "No Registra"
                    binding.petClass.text = tipo ?: "No Registra"
                    binding.petSexo.text = sexo ?: "No Registra"
                    binding.petVacunas.text = vacunas ?: "No Registra"

                    if (imageString != null) {
                        try {
                            Glide.with(this)
                                .asBitmap()
                                .load(Base64.decode(imageString, Base64.DEFAULT))
                                .apply(RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
                                .into(binding.imagePet)
                        } catch (e: IllegalArgumentException) {
                            e.printStackTrace()
                            binding.imagePet.setImageResource(R.drawable.default_profile_image)
                        }
                    } else {
                        binding.imagePet.setImageResource(R.drawable.default_profile_image)
                    }
                }
            }
            .addOnFailureListener {
                binding.imagePet.setImageResource(R.drawable.default_profile_image)
            }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupNotificationButton()
            } else {
                // Notify the user that notification permission is required
                Toast.makeText(this, "Notification permission is required to send alerts.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupNotificationButton() {
        binding.alertButton.setOnClickListener {
            createNotificationChannel()
            createNotification()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channelImportance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(canalId, canalNombre, channelImportance)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private fun createNotification() {
        val resultIntent = Intent(applicationContext, PetProfileActivity::class.java)

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, pendingIntentFlags)
        }

        val notification = NotificationCompat.Builder(this, canalId).apply {
            setContentTitle("Mascotas")
            setContentText("He perdido mi mascota, ayúdame a encontrarla")
            setSmallIcon(R.drawable.ic_notification)
            setContentIntent(resultPendingIntent)
            priority = NotificationCompat.PRIORITY_HIGH
            setAutoCancel(true)
        }.build()

        val notificationManager = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        notificationManager.notify(notificationId, notification)
    }
}
