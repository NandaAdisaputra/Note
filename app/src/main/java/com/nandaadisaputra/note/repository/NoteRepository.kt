package com.nandaadisaputra.note.repository

import android.app.DownloadManager.Query
import android.media.audiofx.AudioEffect.Descriptor
import android.util.Log
import com.nandaadisaputra.note.model.Note
import com.nandaadisaputra.note.network.ApiRequest
import org.json.JSONObject

class NoteRepository {
    //Fungsi untuk mengambil semua catatan dari API
    fun getAllNote():List<Note>{
        //Panggil fungsi getNotes() dari ApiRequest untuk mendapatkan data mentahnya
        val response = ApiRequest.getNotes()
        //Buat list kosong untuk menyimpan hasil parsingnya
        val noteList = mutableListOf<Note>()
        //Ubah response ( String ) menjadi JSONObject
        val jsonObject = org.json.JSONObject(response)
        //Ambil array dari field "data" dalam JSON
        val dataArray = jsonObject.getJSONArray("data")
        //Loop setiap item dalam array dan ubah ke objek Note
        for ( i in 0 until dataArray.length()){
            val obj = dataArray.getJSONObject(i)
            noteList.add(
                Note(
                    id = obj.getInt("id"),
                    title = obj.getString("title"),
                    description = obj.getString("description")
                )
            )

            }
        //Kembalikan daftar catatan
        return noteList
        }

    // Fungsi untuk menambahkan catatan baru
    fun addNote(title: String, description: String): Boolean {
        return try {
            // Kirim data ke API dan simpan responnya
            val response = ApiRequest.addNote(title, description)

            // Tampilkan response di log untuk debugging
            Log.d("API Response", response)

            // Ubah response menjadi JSONObject
            val jsonResponse = JSONObject(response)

            // Periksa apakah status dari response bernilai "success"
            jsonResponse.getString("status").equals("success", ignoreCase = true)
        } catch (e: Exception) {
            // Jika terjadi error saat parsing, tampilkan di log dan kembalikan false
            Log.e("AddNoteError", "Error parsing response: ${e.message}")
            false
        }
    }

    //Fungsi Pencarian Data
    fun searchNotes(query: String):List<Note> {
        //Kirim query ke API dan simpan responsenya
        val response = ApiRequest.searchNotes(query)
        val noteList = mutableListOf<Note>()

        //Ubah response menjadi JSONObject
        val jsonObject = JSONObject(response)
        val dataArray = jsonObject.getJSONArray("data")

        //Loop setiap item dalam Array dan ubah ke objek Note
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

        //Kembalikan hasil pencarian dalam bentuk list
        return noteList
    }

}