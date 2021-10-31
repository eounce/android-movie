package com.example.cinema

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity: AppCompatActivity() {
    val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handler.postDelayed(Runnable {
            val intent = Intent(applicationContext, MainMovieActivity::class.java)
            startActivity(intent)

            finish()
        }, 1000)
    }
}