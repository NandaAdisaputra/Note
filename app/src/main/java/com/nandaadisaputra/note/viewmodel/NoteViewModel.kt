package com.nandaadisaputra.note.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nandaadisaputra.note.model.Note
import com.nandaadisaputra.note.repository.NoteRepository
import kotlin.concurrent.thread

// ViewModel untuk mengelola data catatan dan berinteraksi dengan UI
class NoteViewModel : ViewModel() {

    // Inisialisasi repository untuk mengakses data catatan
    private val repo = NoteRepository()

    // MutableLiveData untuk menyimpan data catatan secara internal
    private val _notes = MutableLiveData<List<Note>>()

    // LiveData yang akan dibaca oleh UI (hanya getter yang disediakan untuk mencegah perubahan langsung)
    val notes: LiveData<List<Note>> get() = _notes

    // Fungsi untuk memuat semua catatan dari repository
    fun loadNotes() = thread {
        // Memuat catatan dan mengupdate _notes dengan data yang diambil
        _notes.postValue(repo.getAllNote())
    }

    // Fungsi untuk menambah catatan baru
    fun addNote(t: String, d: String, cb: (Boolean) -> Unit) = thread {
        // Menambahkan catatan baru dan memeriksa hasilnya
        val r = repo.addNote(t, d)
        // Memanggil callback dengan hasil success/failure
        cb(r)

        // Jika berhasil, memuat ulang semua catatan
        if (r) _notes.postValue(repo.getAllNote())
    }

    // Fungsi untuk mencari catatan berdasarkan query
    fun searchNotes(q: String) = thread {
        // Mengambil hasil pencarian dan mengupdate _notes
        _notes.postValue(repo.searchNotes(q))
    }

    // Fungsi untuk memperbarui catatan berdasarkan ID
    fun updateNote(id: Int, t: String, d: String, cb: (Boolean) -> Unit) = thread {
        // Mengupdate catatan dan memeriksa hasilnya
        val r = repo.updateNote(id, t, d)
        // Memanggil callback dengan hasil success/failure
        cb(r)

        // Jika berhasil, memuat ulang semua catatan
        if (r) _notes.postValue(repo.getAllNote())
    }

    // Fungsi untuk menghapus catatan berdasarkan ID
    fun deleteNote(id: Int, cb: (Boolean) -> Unit) = thread {
        // Menghapus catatan dan memeriksa hasilnya
        val r = repo.deleteNote(id)
        // Memanggil callback dengan hasil success/failure
        cb(r)

        // Jika berhasil, memuat ulang semua catatan
        if (r) _notes.postValue(repo.getAllNote())
    }
}


// Tips Hafalan Cepat:
// Semua fungsi pakai thread { ... } biar tidak blocking.
// _notes.postValue(...) = update tampilan.
// cb(r) = callback untuk hasil (berhasil/gagal).
// Nama variabel dipendekkan: t, d, r, cb, q → lebih cepat ditulis dan diingat.
