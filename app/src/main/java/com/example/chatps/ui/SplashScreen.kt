package com.example.chatps.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chatps.R
import com.example.chatps.ui.chat.activities.SignInActivity

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        splashScreen()
    }

    fun splashScreen() {
        object : CountDownTimer(5000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                //TODO("Not yet implemented")
            }

            override fun onFinish() {
                val intent = Intent(applicationContext, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }

        }.start()
    }
}