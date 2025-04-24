package com.nandaadisaputra.note.ui

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.nandaadisaputra.note.R
import com.nandaadisaputra.note.session.SessionManager
import com.nandaadisaputra.note.viewmodel.NoteViewModel

class LoginActivity : AppCompatActivity() {

    private val vm: NoteViewModel by viewModels()  // ViewModel untuk menangani logika bisnis terkait login
    private lateinit var sessionManager: SessionManager  // SessionManager untuk mengelola sesi pengguna

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inisialisasi elemen UI untuk login
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)
        sessionManager = SessionManager(this)  // Inisialisasi SessionManager

        // Cek apakah pengguna sudah login sebelumnya, jika sudah, langsung pindah ke MainActivity
        if (sessionManager.isLoggedIn()) {
            navigateToMainActivity()  // Pindah ke MainActivity jika sudah login
        }

        // Menambahkan animasi pada TextInputEditText untuk efek visual
        val usernameAnimation = AnimationUtils.loadAnimation(this, R.anim.login_text)  // Animasi untuk username
        val passwordAnimation = AnimationUtils.loadAnimation(this, R.anim.login_text)  // Animasi untuk password

        etUsername.startAnimation(usernameAnimation)  // Menjalankan animasi pada username
        etPassword.startAnimation(passwordAnimation)  // Menjalankan animasi pada password

        // Menambahkan animasi pada tombol login
        val buttonAnimation = AnimationUtils.loadAnimation(this, R.anim.login_button)  // Animasi untuk tombol login
        btnLogin.startAnimation(buttonAnimation)  // Menjalankan animasi pada tombol login

        // Menangani klik pada tombol login
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()  // Mengambil username yang dimasukkan
            val password = etPassword.text.toString().trim()  // Mengambil password yang dimasukkan

            // Validasi input username dan password
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username dan Password wajib diisi", Toast.LENGTH_SHORT).show()  // Menampilkan pesan error
                return@setOnClickListener
            }

            // Melakukan login dengan memanggil method dari ViewModel
            vm.loginUser(username, password) { success, token ->
                runOnUiThread {
                    // Mengecek apakah login berhasil
                    if (success) {
                        // Jika login berhasil, simpan token di SessionManager
                        sessionManager.saveToken(token.toString())
                        sessionManager.saveUserInfo(username, password)  // Simpan informasi login untuk sesi berikutnya

                        Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show()  // Tampilkan pesan berhasil
                        navigateToMainActivity()  // Pindah ke MainActivity setelah login berhasil
                        finish()  // Menutup halaman login agar tidak bisa kembali ke halaman login saat tombol back ditekan
                    } else {
                        Toast.makeText(this, "Login Gagal", Toast.LENGTH_SHORT).show()  // Tampilkan pesan error jika login gagal
                    }
                }
            }
        }

        // Tombol Daftar menggunakan TextView untuk navigasi ke halaman registrasi
        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))  // Pindah ke halaman registrasi
        }
    }

    // Fungsi untuk navigasi ke MainActivity setelah login berhasil
    private fun navigateToMainActivity() {
        startActivity(Intent(this, PaginationNoteActivity::class.java))  // Pindah ke PaginationNoteActivity
        finish()  // Menutup halaman login agar tidak bisa kembali ke halaman login
    }
}
