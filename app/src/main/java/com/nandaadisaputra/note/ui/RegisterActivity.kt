package com.nandaadisaputra.note.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.nandaadisaputra.note.R
import com.nandaadisaputra.note.viewmodel.NoteViewModel

class RegisterActivity : AppCompatActivity() {

    private val vm: NoteViewModel by viewModels()  // Menggunakan ViewModel untuk mengelola data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inisialisasi elemen UI yang digunakan dalam registrasi
        val etUsername = findViewById<EditText>(R.id.etUsername)  // EditText untuk memasukkan username
        val etPassword = findViewById<EditText>(R.id.etPassword)  // EditText untuk memasukkan password
        val etEmail = findViewById<EditText>(R.id.etEmail)  // EditText untuk memasukkan email
        val btnRegister = findViewById<Button>(R.id.btnRegister)  // Button untuk tombol registrasi
        val tvLogin = findViewById<TextView>(R.id.tvLoginLink)  // TextView untuk link ke halaman login

        // Set listener untuk tombol Register
        btnRegister.setOnClickListener {
            // Mendapatkan nilai dari inputan form registrasi
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            val email = etEmail.text.toString()

            // Panggil fungsi registerUser pada ViewModel untuk proses registrasi
            vm.registerUser(username, password, email) { success ->
                runOnUiThread {
                    // Menangani hasil dari registrasi
                    if (success) {
                        // Jika registrasi berhasil, arahkan ke halaman login
                        startActivity(Intent(this, LoginActivity::class.java))
                        Toast.makeText(this, "Registrasi Berhasil", Toast.LENGTH_SHORT).show()  // Menampilkan pesan sukses
                    } else {
                        // Jika registrasi gagal, tampilkan pesan error
                        Toast.makeText(this, "Registrasi Gagal", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Set listener untuk link "Login" untuk navigasi ke halaman login
        tvLogin.setOnClickListener {
            // Arahkan ke halaman Login
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
