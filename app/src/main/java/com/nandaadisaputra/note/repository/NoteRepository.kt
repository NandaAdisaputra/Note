package com.nandaadisaputra.note.repository

import android.util.Log
import com.nandaadisaputra.note.model.Note
import com.nandaadisaputra.note.network.ApiRequest
import org.json.JSONObject

// Repository untuk mengelola data catatan, berinteraksi dengan API
class NoteRepository {

    // Fungsi untuk mengambil semua catatan dari server
    fun getAllNote(): List<Note> {
        // Mengambil data catatan dari API
        val res = ApiRequest.getNotes()

        // Mengonversi response JSON ke dalam bentuk array
        val arr = JSONObject(res).getJSONArray("data")

        // Mengubah array JSON menjadi list of Note object
        return List(arr.length()) { i ->
            val o = arr.getJSONObject(i) // Mendapatkan objek JSON pada index i
            Note(o.getInt("id"), o.getString("title"), o.getString("description")) // Membuat objek Note dari JSON
        }
    }
    // Fungsi untuk registrasi pengguna
    fun registerUser(username: String, password: String, email: String): Boolean = try {
        val response = JSONObject(ApiRequest.registerUser(username, password, email))
        val status = response.getString("status")
        status.equals("success", true)
    } catch (e: Exception) {
        Log.e("RegisterUser", e.message.toString())
        false
    }
    // Fungsi untuk login pengguna
    fun loginUser(username: String, password: String): Pair<Boolean, String?> = try {
        val response = JSONObject(ApiRequest.loginUser(username, password))
        val status = response.getString("status")
        if (status.equals("success", true)) {
            val token = response.getJSONObject("data").getString("token")
            Pair(true, token)
        } else {
            Pair(false, null)
        }
    } catch (e: Exception) {
        Log.e("LoginUser", e.message.toString())
        Pair(false, null)
    }
    // Ambil catatan berdasarkan ID
    fun getNoteById(id: Int): Note? = try {
        val res = ApiRequest.getNoteById(id.toString()) // Asumsikan fungsi ini ada di ApiRequest
        val data = JSONObject(res).getJSONObject("data")
        Note(data.getInt("id"), data.getString("title"), data.getString("description"))
    } catch (e: Exception) {
        Log.e("GetNoteById", e.message.toString())
        null
    }
    // Fungsi untuk mengambil catatan dengan token dan paginasi
    fun getPaginatedNotes(page: Int, limit: Int, token: String): List<Note> = try {
        // Mengambil data dari API dengan pagination dan token
        val res = ApiRequest.getNotesPaginatedWithToken(page, limit, token)

        // Parsing respons JSON
        val responseObject = JSONObject(res)
        val dataObject = responseObject.getJSONObject("data") // Ambil objek data
        val notesArray = dataObject.getJSONArray("notes") // Ambil array notes

        // Mengubah array JSON menjadi list of Note object
        List(notesArray.length()) { i ->
            val noteObject = notesArray.getJSONObject(i)
            Note(
                id = noteObject.getInt("id"),
                title = noteObject.getString("title"),
                description = noteObject.getString("description")
            )
        }
    } catch (e: Exception) {
        // Menangani error jika terjadi masalah dalam pengiriman atau response
        Log.e("PaginatedNotes", e.message.toString())
        emptyList() // Mengembalikan list kosong jika terjadi error
    }

    // Fungsi untuk menambahkan catatan baru
    fun addNote(t: String, d: String): Boolean = try {
        // Mengirim request untuk menambah catatan dan memeriksa statusnya
        val s = JSONObject(ApiRequest.addNote(t, d)).getString("status")
        // Mengembalikan true jika statusnya "success", jika tidak, false
        s.equals("success", true)
    } catch (e: Exception) {
        // Menangani error jika terjadi masalah dalam pengiriman atau response
        Log.e("AddNote", e.message.toString())
        false // Mengembalikan false jika terjadi error
    }

    // Fungsi untuk mencari catatan berdasarkan query
    fun searchNotes(q: String): List<Note> {
        // Mengambil data pencarian dari API
        val res = ApiRequest.searchNotes(q)

        // Mengonversi response JSON menjadi array
        val arr = JSONObject(res).getJSONArray("data")

        // Mengubah array JSON menjadi list of Note object
        return List(arr.length()) { i ->
            val o = arr.getJSONObject(i) // Mendapatkan objek JSON pada index i
            Note(o.getInt("id"), o.getString("title"), o.getString("description")) // Membuat objek Note dari JSON
        }
    }

    // Fungsi untuk memperbarui catatan berdasarkan ID
    fun updateNote(id: Int, t: String, d: String): Boolean = try {
        // Mengirim request untuk mengupdate catatan dan memeriksa statusnya
        val s = JSONObject(ApiRequest.updateNote(id.toString(), t, d)).getString("status")
        // Mengembalikan true jika statusnya "success", jika tidak, false
        s.equals("success", true)
    } catch (e: Exception) {
        // Menangani error jika terjadi masalah dalam pengiriman atau response
        false // Mengembalikan false jika terjadi error
    }

    // Fungsi untuk menghapus catatan berdasarkan ID
    fun deleteNote(id: Int): Boolean = try {
        // Mengirim request untuk menghapus catatan dan memeriksa statusnya
        val s = JSONObject(ApiRequest.deleteNote(id.toString())).getString("status")
        // Mengembalikan true jika statusnya "success", jika tidak, false
        s.equals("success", true)
    } catch (e: Exception) {
        // Menangani error jika terjadi masalah dalam pengiriman atau response
        false // Mengembalikan false jika terjadi error
    }
}


//Tips Hafalan Cepat:
//getAllNote, searchNotes = selalu parsing JSONArray.
//addNote, updateNote, deleteNote = parsing status.
//Gunakan s.equals("success", true) untuk hasil.
//Tangani error langsung pakai try-catch.