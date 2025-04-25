package com.nandaadisaputra.note.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nandaadisaputra.note.model.ExportCsvResponse
import com.nandaadisaputra.note.model.ExportPdfResponse
import com.nandaadisaputra.note.model.Note
import com.nandaadisaputra.note.repository.NoteRepository
import com.nandaadisaputra.note.session.SessionManager
import kotlin.concurrent.thread

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    // LiveData for export results (PDF and CSV)
    private val _exportPdfResult = MutableLiveData<Pair<Boolean, ExportPdfResponse?>>()
    val exportPdfResult: LiveData<Pair<Boolean, ExportPdfResponse?>> get() = _exportPdfResult

    private val _exportCsvResult = MutableLiveData<Pair<Boolean, ExportCsvResponse?>>()
    val exportCsvResult: LiveData<Pair<Boolean, ExportCsvResponse?>> get() = _exportCsvResult

    // MutableLiveData for tracking pagination and loading state
    private val _isLastPage = MutableLiveData<Boolean>(false)
    val isLastPage: LiveData<Boolean> get() = _isLastPage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // SessionManager and repository instances
    private val sessionManager = SessionManager(application.applicationContext)
    private val repo = NoteRepository()

    // LiveData for notes
    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> get() = _notes
    // ViewModel
    fun loadNotes() = thread {
        _isLoading.postValue(true)  // Menyatakan loading dimulai
        val response = repo.getAllNotes()
        response?.let {
            _notes.postValue(it.data)
        }
        _isLoading.postValue(false)  // Menyatakan loading selesai
    }

    // Function to get a note by its ID
    fun getNoteById(id: Int, cb: (Note?) -> Unit) = thread {
        val response = repo.getNoteById(id)
        cb(response?.data)
    }

    // Function to add a new note
    fun addNote(t: String, d: String, cb: (Boolean) -> Unit) = thread {
        // Set loading ke true
        _isLoading.postValue(true)
        val response = repo.addNote(t, d)
        val success = response?.code in 200..299
        cb(success)
        // Setelah selesai, set loading ke false
        _isLoading.postValue(false)
        if (success) {
            val updated = repo.getAllNotes()
            updated?.let { _notes.postValue(it.data) }
            getPaginatedNotes(1, 10)
        }
    }

    // Function to update an existing note
    fun updateNote(id: Int, t: String, d: String, cb: (Boolean) -> Unit) = thread {
        // Set loading ke true
        _isLoading.postValue(true)
        val response = repo.updateNote(id, t, d) // ← response adalah NoteResponse?
        val success = response?.code == 200
        cb(success)
        // Setelah selesai, set loading ke false
        _isLoading.postValue(false)
        if (success) {
            val updated = repo.getAllNotes()
            updated?.let { _notes.postValue(it.data) }
            getPaginatedNotes(1, 10)
        }
    }
    // Function to search notes based on a query
    fun searchNotes(q: String) = thread {
        // Set loading ke true
        _isLoading.postValue(true)
        val result = repo.searchNotes(q)
        result?.let {
            // Setelah selesai, set loading ke false
            _isLoading.postValue(false)
            _notes.postValue(it.data)
        }
    }



    // Function to delete a note
    fun deleteNote(id: Int, cb: (Boolean) -> Unit) = thread {
        // Set loading ke true
        _isLoading.postValue(true)
        val response = repo.deleteNote(id)
        val success = response?.code == 200
        cb(success)
        // Setelah selesai, set loading ke false
        _isLoading.postValue(false)
        if (success) {
            val updated = repo.getAllNotes()
            updated?.let { _notes.postValue(it.data) }
        }
    }

    // Function to get paginated notes
    fun getPaginatedNotes(p: Int, s: Int, cb: (Boolean) -> Unit = {}) {
        thread {
            _isLoading.postValue(true)

            try {
                val token = sessionManager.getToken()
                if (token.isNullOrEmpty()) {
                    Log.e("VM", "Token is empty. Failed to fetch data.")
                    cb(false)
                    return@thread
                }

                val response = repo.getPaginatedNotes(p, s, token)
                if (response?.data?.size ?: 0 < s) _isLastPage.postValue(true)

                val currentNotes = _notes.value ?: emptyList()
                _notes.postValue(currentNotes + (response?.data ?: emptyList()))
                cb(true)

            } catch (e: Exception) {
                Log.e("VM", "Error fetching data: ${e.message}")
                cb(false)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
    //Function to export notes to PDF
    fun exportNotesToPdf() = thread {
     //set loading ke true
        _isLoading.postValue(true)
        try {
            //setelah selesai, set loading ke false
            _isLoading.postValue(false)
            val result = repo.exportNotesToPdf()
            _exportPdfResult.postValue(result)
        } catch (e:Exception){
            Log.e("VM","Error exporting PDF: ${e.message}")
            _exportPdfResult.postValue(Pair(false, null))
        }
    }
    // Function to export notes to CSV
    fun exportNotesToCsv() = thread {
        // Set loading ke true
        _isLoading.postValue(true)
        try {
            // Setelah selesai, set loading ke false
            _isLoading.postValue(false)
            val result = repo.exportNotesToCsv()
            _exportCsvResult.postValue(result)
        } catch (e: Exception) {
            Log.e("VM", "Error exporting CSV: ${e.message}")
            _exportCsvResult.postValue(Pair(false, null))
        }
    }
}