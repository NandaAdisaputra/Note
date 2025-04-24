package com.nandaadisaputra.note.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nandaadisaputra.note.model.Note
import com.nandaadisaputra.note.repository.NoteRepository
import com.nandaadisaputra.note.session.SessionManager
import kotlin.concurrent.thread

// ViewModel untuk mengelola data catatan dan berinteraksi dengan UI
class NoteViewModel(application: Application) : AndroidViewModel(application) {
    // MutableLiveData untuk mengetahui apakah sudah mencapai halaman terakhir
    private val _isLastPage = MutableLiveData<Boolean>(false)
    val isLastPage: LiveData<Boolean> get() = _isLastPage

    // Tambahkan LiveData tambahan untuk menangani loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Inisialisasi sessionManager untuk mengelola sesi pengguna
    private val sessionManager = SessionManager(application.applicationContext)

    // Inisialisasi repository untuk mengakses data catatan
    private val repo = NoteRepository()

    // MutableLiveData untuk menyimpan data catatan secara internal
    private val _notes = MutableLiveData<List<Note>>()

    // LiveData yang akan dibaca oleh UI (hanya getter yang disediakan untuk mencegah perubahan langsung)
    val notes: LiveData<List<Note>> get() = _notes

    // Fungsi untuk memuat semua catatan dari repository
    fun loadNotes() = thread {
        _notes.postValue(repo.getAllNote())  // Memuat semua catatan dan update UI
    }

    // Fungsi untuk registrasi pengguna baru
    fun registerUser(username: String, password: String, email: String, cb: (Boolean) -> Unit) =
        thread {
            val result = repo.registerUser(username, password, email)  // Registrasi pengguna
            cb(result)  // Panggil callback dengan hasil registrasi
        }

    // Fungsi untuk login pengguna
    fun loginUser(username: String, password: String, cb: (Boolean, String?) -> Unit) = thread {
        val result = repo.loginUser(username, password)  // Melakukan login
        cb(result.first, result.second)  // Panggil callback dengan status login dan pesan error
    }

    // Fungsi untuk mengambil catatan berdasarkan ID
    fun getNoteById(id: Int, cb: (Note?) -> Unit) = thread {
        val note = repo.getNoteById(id)  // Ambil catatan berdasarkan ID
        cb(note)  // Panggil callback dengan data catatan
    }

    // Fungsi untuk menambah catatan baru
    fun addNote(t: String, d: String, cb: (Boolean) -> Unit) = thread {
        val r = repo.addNote(t, d)  // Menambah catatan baru
        cb(r)  // Panggil callback dengan hasil (sukses/gagal)

        // Jika berhasil menambah catatan, muat ulang daftar catatan
        if (r) _notes.postValue(repo.getAllNote())
    }

    // Fungsi untuk mencari catatan berdasarkan query
    fun searchNotes(q: String) = thread {
        _notes.postValue(repo.searchNotes(q))  // Menampilkan hasil pencarian
    }

    // Fungsi untuk memperbarui catatan berdasarkan ID
    fun updateNote(id: Int, t: String, d: String, cb: (Boolean) -> Unit) = thread {
        val r = repo.updateNote(id, t, d)
        cb(r)
        if (r) {
            _notes.postValue(repo.getAllNote())
            getPaginatedNotes(1, 10)
        }
    }

    // Fungsi untuk menghapus catatan berdasarkan ID
    fun deleteNote(id: Int, cb: (Boolean) -> Unit) = thread {
        val r = repo.deleteNote(id)  // Menghapus catatan berdasarkan ID
        cb(r)  // Panggil callback dengan hasil (sukses/gagal)

        // Jika berhasil menghapus, muat ulang daftar catatan
        if (r) _notes.postValue(repo.getAllNote())
    }

    // Fungsi untuk mengambil catatan dengan pagination (limit dan offset)
    fun getPaginatedNotes(p: Int, s: Int, cb: (Boolean) -> Unit = {}) {
        thread {
            _isLoading.postValue(true) // Tampilkan loading indicator di UI

            try {
                val t = sessionManager.getToken() // Ambil token dari session
                if (t.isNullOrEmpty()) {
                    // Jika token kosong, log error dan kirim callback gagal
                    Log.e("VM", "Token kosong. Gagal ambil data.")
                    cb(false)
                    return@thread
                }

                val d = repo.getPaginatedNotes(p, s, t) // Ambil data catatan dari repo (API)

                if (d.size < s) _isLastPage.postValue(true) // Kalau data kurang dari ukuran page, berarti ini halaman terakhir

                val r = (_notes.value ?: emptyList()) + d // Gabungkan data lama + data baru
                _notes.postValue(r) // Update tampilan

                cb(true) // Kirim callback sukses

            } catch (e: Exception) {
                // Tangani error dan kirim callback gagal
                Log.e("VM", "Err ambil data: ${e.message}")
                cb(false)
            } finally {
                _isLoading.postValue(false) // Sembunyikan loading indicator
            }
        }
    }
}


// Tips Hafalan Cepat:
// Semua fungsi pakai thread { ... } biar tidak blocking.
// _notes.postValue(...) = update tampilan.
// cb(r) = callback untuk hasil (berhasil/gagal).
// Nama variabel dipendekkan: t, d, r, cb, q → lebih cepat ditulis dan diingat.