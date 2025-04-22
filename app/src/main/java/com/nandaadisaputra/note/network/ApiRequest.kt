package com.nandaadisaputra.note.network

import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.net.URLEncoder

object ApiRequest {
    // URL dasar API Google Apps Script
    private const val BASE = "https://script.google.com/macros/s/AKfycbxbC0CrCD_w8wvl5XAXUKW2gObUdb5AzYDEZ1n7j01xJSCI-Fl2SpqfTEh2X0qJ3KAcAA/exec"

    // Fungsi ekstensi untuk melakukan encoding pada string agar aman dikirim melalui URL
    // Contoh: "My Note" akan menjadi "My%20Note"
    private fun String.e() = URLEncoder.encode(this, "UTF-8")

    // Fungsi untuk melakukan request GET ke server, mengambil data dari server
    private fun get(end: String) = URL("$BASE?endpoint=$end") // Menyusun URL lengkap dengan endpoint
        .openConnection().getInputStreamText() // Mengambil data dari URL dengan fungsi getInputStreamText()

    // Fungsi untuk membaca response dari server, baik itu hasil GET atau POST
    private fun URLConnection.getInputStreamText() = getInputStream().bufferedReader().readText()

    // Fungsi untuk mengirim request POST dengan data tertentu
    private fun post(data: String): String {
        // Membuat koneksi HTTP dengan metode POST
        val conn = URL(BASE).openConnection() as HttpURLConnection
        conn.requestMethod = "POST" // Menggunakan metode POST
        conn.doOutput = true // Mengaktifkan output stream untuk mengirim data
        conn.outputStream.write(data.toByteArray()) // Menulis data ke dalam body POST request
        return conn.inputStream.bufferedReader().readText() // Membaca dan mengembalikan response server
    }

    // Fungsi untuk mendapatkan semua catatan (menggunakan GET)
    fun getNotes() = get("getNotes")

    // Fungsi untuk mencari catatan berdasarkan query tertentu (menggunakan GET)
    fun searchNotes(q: String) = get("searchNotes&query=${q.e()}") // Mengencode query untuk aman di URL

    // Fungsi untuk menambahkan catatan baru (menggunakan POST)
    fun addNote(t: String, d: String) = post("endpoint=add_note&title=${t.e()}&description=${d.e()}")

    // Fungsi untuk mengupdate catatan berdasarkan ID (menggunakan POST)
    fun updateNote(i: String, t: String, d: String) =
        post("endpoint=update_note&id=${i.e()}&title=${t.e()}&description=${d.e()}")

    // Fungsi untuk menghapus catatan berdasarkan ID (menggunakan POST)
    fun deleteNote(i: String) = post("endpoint=delete_note&id=${i.e()}")
}

//Tips Hafalan Cepat LKS:
// Semua GET → get("endpoint")
// Semua POST → post("endpoint=data...")
// .e() → encode aman URL
// getNotes(), addNote(), updateNote(), deleteNote() → nama fungsi = sesuai aksi