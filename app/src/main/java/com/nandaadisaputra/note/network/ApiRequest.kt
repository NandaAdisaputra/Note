package com.nandaadisaputra.note.network

import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object ApiRequest {
    private const val BASE_URL ="https://script.google.com/macros/s/AKfycbxbC0CrCD_w8wvl5XAXUKW2gObUdb5AzYDEZ1n7j01xJSCI-Fl2SpqfTEh2X0qJ3KAcAA/exec"
 //Mengambil Semua Data Catatan
    fun getNotes(): String{
        val url = URL("$BASE_URL?endpoint=getNotes")
        val connection = url.openConnection() as HttpURLConnection
        //Membaca dan mengembalikan hasil response dari API sebagai teks
        return connection.inputStream.bufferedReader().readText()
    }
    // Fungsi untuk menambahkan catatan baru ke API
    fun addNote(title: String, description: String): String {
        // Encode title dan description agar aman dikirim melalui internet
        val postData = "endpoint=add_note&title=${URLEncoder.encode(title, "UTF-8")}&description=${URLEncoder.encode(description, "UTF-8")}"
        // Membuat URL tujuan untuk mengirim data
        val url = URL(BASE_URL)
        // Membuka koneksi dan mengubahnya ke mode POST
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.doOutput = true
        // Menuliskan data yang ingin dikirim ke body request
        conn.outputStream.write(postData.toByteArray())
        // Membaca dan mengembalikan hasil response dari API
        return conn.inputStream.bufferedReader().readText()
    }

    //Melakukan Search Data
    fun searchNotes(query:String): String{
        //Encode query agar aman digunakan dalam URL
        val encodedQuery = URLEncoder.encode(query,"UTF-8")
        val url = URL("$BASE_URL?endpoint=searchNotes&query=$encodedQuery")
        //Membuka koneksi ke URL
        val connection = url.openConnection() as HttpURLConnection
        //Membaca dan mengembalikan hasil response dari API sebagai teks
        return connection.inputStream.bufferedReader().readText()
    }
}