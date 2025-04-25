package com.nandaadisaputra.note.repository

import android.util.Log
import com.nandaadisaputra.note.model.ExportCsvData
import com.nandaadisaputra.note.model.ExportCsvResponse
import com.nandaadisaputra.note.model.ExportPdfData
import com.nandaadisaputra.note.model.ExportPdfResponse
import com.nandaadisaputra.note.model.Note
import com.nandaadisaputra.note.model.NoteResponse
import com.nandaadisaputra.note.model.SingleNoteResponse
import com.nandaadisaputra.note.network.NoteApi
import org.json.JSONObject

// Repository untuk mengelola data catatan, berinteraksi dengan API
class NoteRepository {

    // Fungsi untuk mengambil semua catatan dari server
    // Menggunakan NoteResponse karena mengembalikan daftar (list) catatan.
    fun getAllNotes(): NoteResponse? = try {
        val response = NoteApi.getNotes()  // Mengambil data dari API

        val jsonObject = JSONObject(response)  // Mengubah respons JSON menjadi objek
        val code = jsonObject.getInt("code")  // Mendapatkan status kode
        val status = jsonObject.getString("status")  // Mendapatkan status
        val message = jsonObject.getString("message")  // Mendapatkan pesan

        val dataArray = jsonObject.getJSONArray("data")  // Mendapatkan array data catatan
        val noteList = mutableListOf<Note>()  // Menyiapkan list untuk menampung catatan

        // Loop untuk memasukkan data catatan ke dalam list
        for (i in 0 until dataArray.length()) {
            val obj = dataArray.getJSONObject(i)
            val note = Note(
                id = obj.getInt("id"),
                title = obj.getString("title"),
                description = obj.getString("description")
            )
            noteList.add(note)
        }

        // Mengembalikan NoteResponse dengan data berupa daftar catatan
        NoteResponse(code, status, message, noteList)
    } catch (e: Exception) {
        Log.e("getAllNotes", e.message.toString())  // Menangani error jika ada
        null  // Mengembalikan null jika terjadi error
    }

    // Fungsi untuk mengambil catatan berdasarkan ID
    // Menggunakan SingleNoteResponse karena hanya mengembalikan satu catatan.
    fun getNoteById(id: Int): SingleNoteResponse? = try {
        val res = NoteApi.getNoteById(id.toString())  // Mengambil data catatan berdasarkan ID
        val jsonObject = JSONObject(res)  // Mengubah respons JSON menjadi objek

        val code = jsonObject.getInt("code")  // Mendapatkan status kode
        val status = jsonObject.getString("status")  // Mendapatkan status
        val message = jsonObject.getString("message")  // Mendapatkan pesan

        val data = jsonObject.getJSONObject("data")  // Mendapatkan data catatan
        val note = Note(
            id = data.getInt("id"),
            title = data.getString("title"),
            description = data.getString("description")
        )

        // Mengembalikan SingleNoteResponse dengan satu catatan
        SingleNoteResponse(code, status, message, note)
    } catch (e: Exception) {
        Log.e("getNoteById", e.message.toString())  // Menangani error jika ada
        null  // Mengembalikan null jika terjadi error
    }

    // Fungsi untuk mengambil catatan secara terpagini (paged) dengan token otentikasi
    // Menggunakan NoteResponse karena data yang dikembalikan berupa daftar catatan terpagini.
    fun getPaginatedNotes(page: Int, limit: Int, token: String): NoteResponse? = try {
        // Mengambil data catatan terpagini menggunakan API dengan halaman dan limit yang ditentukan
        val res = NoteApi.getNotesPaginatedWithToken(page, limit, token)

        // Mengonversi respons JSON menjadi objek
        val jsonObject = JSONObject(res)
        val code = jsonObject.getInt("code")  // Mendapatkan kode status respons
        val status = jsonObject.getString("status")  // Mendapatkan status respons
        val message = jsonObject.getString("message")  // Mendapatkan pesan respons

        // Mengambil data yang berisi catatan terpagini
        val dataObject = jsonObject.getJSONObject("data")
        val notesArray = dataObject.getJSONArray("notes")  // Mendapatkan array catatan
        val noteList = mutableListOf<Note>()  // Menyiapkan list untuk menampung catatan

        // Loop untuk memasukkan catatan dari array JSON ke dalam list
        for (i in 0 until notesArray.length()) {
            val noteObject = notesArray.getJSONObject(i)
            val note = Note(
                id = noteObject.getInt("id"),
                title = noteObject.getString("title"),
                description = noteObject.getString("description")
            )
            noteList.add(note)
        }

        // Mengembalikan NoteResponse dengan status, pesan, dan daftar catatan terpagini
        NoteResponse(code, status, message, noteList)
    } catch (e: Exception) {
        // Menangani error jika terjadi dan mencatat log error
        Log.e("PaginatedNotes", e.message.toString())
        null  // Mengembalikan null jika terjadi error
    }


    // Fungsi untuk menambahkan catatan baru
   // Menggunakan NoteResponse karena mengembalikan status dan pesan bersama dengan data catatan baru.
    fun addNote(t: String, d: String): NoteResponse? = try {
        val res = NoteApi.addNote(t, d)  // Mengirim permintaan untuk menambahkan catatan baru
        val jsonObject = JSONObject(res)  // Mengubah respons JSON menjadi objek
        val code = jsonObject.getInt("code")  // Mendapatkan status kode
        val status = jsonObject.getString("status")  // Mendapatkan status
        val message = jsonObject.getString("message")  // Mendapatkan pesan

        val dataObject =
            jsonObject.getJSONObject("data")  // Mendapatkan data catatan yang baru ditambahkan
        val newNote = Note(
            id = dataObject.getInt("id"),
            title = dataObject.getString("title"),
            description = dataObject.getString("description")
        )

        // Mengembalikan NoteResponse dengan data catatan yang baru
        NoteResponse(code, status, message, listOf(newNote))
    } catch (e: Exception) {
        Log.e("AddNote", e.message.toString())  // Menangani error jika ada
        null  // Mengembalikan null jika terjadi error
    }


    // Fungsi untuk mencari catatan berdasarkan kata kunci
   // Menggunakan NoteResponse karena hasil pencarian bisa mengembalikan banyak catatan.
    fun searchNotes(q: String): NoteResponse? = try {
        val res =
            NoteApi.searchNotes(q)  // Mengirim permintaan untuk mencari catatan berdasarkan kata kunci
        val jsonObject = JSONObject(res)  // Mengubah respons JSON menjadi objek
        val code = jsonObject.getInt("code")  // Mendapatkan status kode
        val status = jsonObject.getString("status")  // Mendapatkan status
        val message = jsonObject.getString("message")  // Mendapatkan pesan

        val dataArray = jsonObject.getJSONArray("data")  // Mendapatkan array data catatan
        val noteList = mutableListOf<Note>()  // Menyiapkan list untuk menampung hasil pencarian

        // Loop untuk memasukkan data catatan yang ditemukan ke dalam list
        for (i in 0 until dataArray.length()) {
            val obj = dataArray.getJSONObject(i)
            noteList.add(
                Note(
                    id = obj.getInt("id"),
                    title = obj.getString("title"),
                    description = obj.getString("description")
                )
            )
        }

        // Mengembalikan NoteResponse dengan hasil pencarian berupa daftar catatan
        NoteResponse(code, status, message, noteList)
    } catch (e: Exception) {
        Log.e("SearchNotes", e.message.toString())  // Menangani error jika ada
        null  // Mengembalikan null jika terjadi error
    }


    // Fungsi untuk mengupdate catatan
   // Menggunakan NoteResponse karena hanya status dan pesan yang dikembalikan, tanpa data catatan.
    fun updateNote(id: Int, t: String, d: String): NoteResponse? = try {
        val response = NoteApi.updateNote(
            id.toString(),
            t,
            d
        )  // Mengirim permintaan untuk memperbarui catatan
        val jsonObject = JSONObject(response)  // Mengubah respons JSON menjadi objek
        val code = jsonObject.getInt("code")  // Mendapatkan status kode
        val status = jsonObject.getString("status")  // Mendapatkan status
        val message = jsonObject.getString("message")  // Mendapatkan pesan

        // Mengembalikan NoteResponse dengan status dan pesan
        NoteResponse(code, status, message, listOf())  // Tidak ada data catatan yang dikembalikan
    } catch (e: Exception) {
        Log.e("UpdateNote", e.message.toString())  // Menangani error jika ada
        null  // Mengembalikan null jika terjadi error
    }


    // Fungsi untuk menghapus catatan
   // Menggunakan NoteResponse karena hanya status dan pesan yang dikembalikan, tanpa data catatan.
    fun deleteNote(id: Int): NoteResponse? = try {
        val response =
            NoteApi.deleteNote(id.toString())  // Mengirim permintaan untuk menghapus catatan
        val jsonObject = JSONObject(response)  // Mengubah respons JSON menjadi objek
        val code = jsonObject.getInt("code")  // Mendapatkan status kode
        val status = jsonObject.getString("status")  // Mendapatkan status
        val message = jsonObject.getString("message")  // Mendapatkan pesan

        // Mengembalikan NoteResponse dengan status dan pesan
        NoteResponse(code, status, message, listOf())  // Tidak ada data catatan yang dikembalikan
    } catch (e: Exception) {
        Log.e("DeleteNote", e.message.toString())  // Menangani error jika ada
        null  // Mengembalikan null jika terjadi error
    }

    // Fungsi untuk export semua catatan menjadi file PDF
    fun exportNotesToPdf(): Pair<Boolean, ExportPdfResponse?> = try {
        val res = NoteApi.exportNotesToPdf()
        val json = JSONObject(res)

        val code = json.getInt("code")
        val status = json.getString("status")
        val message = json.getString("message")
        val data = json.getJSONObject("data")

        val pdfFileName = data.getString("pdfFileName")
        val pdfFileUrl = data.getString("pdfFileUrl")

        val exportPdfData = ExportPdfData(pdfFileName, pdfFileUrl)
        val exportPdfResponse = ExportPdfResponse(code, status, message, exportPdfData)

        if (status.equals("success", true)) {
            Pair(true, exportPdfResponse)
        } else {
            Pair(false, exportPdfResponse)
        }
    } catch (e: Exception) {
        Log.e("ExportNotes", e.message.toString())
        Pair(false, null)
    }

    // Tambahkan fungsi untuk mengekspor catatan ke CSV
    fun exportNotesToCsv(): Pair<Boolean, ExportCsvResponse?> = try {
        val res = NoteApi.exportNotesToCsv()  // Panggil API untuk ekspor CSV
        val json = JSONObject(res)

        val code = json.getInt("code")
        val status = json.getString("status")
        val message = json.getString("message")
        val data = json.getJSONObject("data")

        val fileName = data.getString("fileName")
        val fileUrl = data.getString("fileUrl")

        val exportCsvData = ExportCsvData(fileName, fileUrl)
        val exportCsvResponse = ExportCsvResponse(code, status, message, exportCsvData)

        if (status.equals("success", true)) {
            Pair(true, exportCsvResponse)
        } else {
            Pair(false, exportCsvResponse)
        }
    } catch (e: Exception) {
        Log.e("ExportNotes", e.message.toString())
        Pair(false, null)
    }
}


//Tips Hafalan Cepat:
//getAllNote, searchNotes = selalu parsing JSONArray.
//addNote, updateNote, deleteNote = parsing status.
//Gunakan s.equals("success", true) untuk hasil.
//Tangani error langsung pakai try-catch.