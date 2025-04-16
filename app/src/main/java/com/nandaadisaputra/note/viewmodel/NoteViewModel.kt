package com.nandaadisaputra.note.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nandaadisaputra.note.model.Note
import com.nandaadisaputra.note.repository.NoteRepository
import kotlin.concurrent.thread

class NoteViewModel:ViewModel() {
    //Objek repository untuk mengambil atau mengirim data catatan
    private val repository = NoteRepository()
    //LiveData untuk menyimpan list Catatan , hanya bisa diubah dari dalam ViewModel
    private val _notes = MutableLiveData<List<Note>>()
    //LiveData untuk dipantau ( observe ) oleh UI, misalnya untuk ditampilkan di RecyclerView
    val notes: LiveData<List<Note>>
        get() = _notes
     //Fungsi untuk memuat semua catatan dari repository secara asynchronous ( di thread terpisah )
    fun loadNotes(){
        thread {
            //Ambil data dari repository
            val data = repository.getAllNote()
            //Update nilai LiveData supaya UI ikut berubah
            _notes.postValue(data)
        }
    }
    //Fungsi untuk menambahkan catatan baru
    //onResult: callback untuk memberi tahu ke UI apakah berhasil atau tidak
    fun addNote(title:String, description: String, onResult: (Boolean)->Unit){
        thread {
            //Kirim data ke repository dan simpan hasilnya ( true / false )
            val result = repository.addNote(title,description)
            //Kirim hasilnya ke UI lewat Callback
            onResult(result)
            //Kalau berhasil, perbarui daftar catatan
            if (result){
                //ambil data terbaru dari repository setelah penambahan
                val updateNotes = repository.getAllNote()
                //Masukan catatan baru ke paling atas
                val newNote = Note(id=updateNotes.first().id, title=title, description= description)
                val newNoteList = mutableListOf(newNote) //Memasukkan catatan baru di posisi pertama
                newNoteList.addAll(updateNotes) //Tambahkan sisa catatan yang ada
                //Update LiveData dengan daftar catatan terbaru
                _notes.postValue(newNoteList)
            }
        }
    }

    // Fungsi untuk mencari catatan berdasarkan query (kata kunci)
    fun searchNotes(query: String) {
        thread {
            // Ambil data hasil pencarian dari repository
            val data = repository.searchNotes(query)
            // Update LiveData supaya hasil pencarian muncul di UI
            _notes.postValue(data)
        }
    }
}