package com.nandaadisaputra.note.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.nandaadisaputra.note.R

class SplashActivity : AppCompatActivity() {

    // Waktu tampil splash screen (misalnya 3 detik)
    private val splashTimeOut: Long = 3000 // 3 detik

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Handler untuk delay sebelum berpindah ke MainActivity
        Handler().postDelayed({
            // Intent untuk membuka MainActivity setelah waktu delay
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Menutup SplashActivity agar tidak kembali ke Splash setelah berpindah
        }, splashTimeOut)
    }
}