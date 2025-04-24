package com.nandaadisaputra.note.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nandaadisaputra.note.R

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Inisialisasi TextView untuk menampilkan detail catatan
        val tvDetailTitle = findViewById<TextView>(R.id.tvDetailTitle)  // TextView untuk menampilkan judul
        val tvDetailDescription = findViewById<TextView>(R.id.tvDetailDescription)  // TextView untuk menampilkan deskripsi

        // Mengambil data 'title' dan 'description' dari Intent yang dikirim dari Activity sebelumnya
        val title = intent.getStringExtra("title")  // Mendapatkan nilai title dari Intent
        val description = intent.getStringExtra("description")  // Mendapatkan nilai description dari Intent

        // Jika data 'title' dan 'description' ada, set ke TextView. Jika tidak ada, tampilkan teks default.
        tvDetailTitle.text = title ?: "No Title"  // Menggunakan elvis operator untuk memberi nilai default jika title null
        tvDetailDescription.text = description ?: "No Description"  // Menggunakan elvis operator untuk memberi nilai default jika description null
    }
}
