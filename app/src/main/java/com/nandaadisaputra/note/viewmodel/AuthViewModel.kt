package com.nandaadisaputra.note.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nandaadisaputra.note.repository.AuthRepository
import kotlin.concurrent.thread

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    // Inisialisasi repository untuk mengakses data auth
    private val repoAuth = AuthRepository()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Fungsi untuk registrasi pengguna baru
    fun registerUser(username: String, password: String, email: String, cb: (Boolean) -> Unit) =
        thread {
            _isLoading.postValue(true)  // Menyatakan loading dimulai
            val result = repoAuth.registerUser(username, password, email)  // Registrasi pengguna
            _isLoading.postValue(false)  // Menyatakan loading selesai
            cb(result)  // Panggil callback dengan hasil registrasi
        }

    // Fungsi untuk login pengguna
    fun loginUser(username: String, password: String, cb: (Boolean, String?) -> Unit) = thread {
        _isLoading.postValue(true)  // Menyatakan loading dimulai
        val result = repoAuth.loginUser(username, password)  // Melakukan login
        _isLoading.postValue(false)  // Menyatakan loading selesai
        cb(result.first, result.second)  // Panggil callback dengan status login dan pesan error
    }

}