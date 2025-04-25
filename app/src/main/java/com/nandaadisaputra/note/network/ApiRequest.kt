package com.nandaadisaputra.note.network

import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import com.nandaadisaputra.note.utils.e

object ApiRequest {
    // URL dasar API Google Apps Script
    private const val BASE =
        "https://script.google.com/macros/s/AKfycbxbC0CrCD_w8wvl5XAXUKW2gObUdb5AzYDEZ1n7j01xJSCI-Fl2SpqfTEh2X0qJ3KAcAA/exec"

    // Fungsi untuk melakukan request GET ke server, mengambil data dari server
    fun get(end: String): String = URL("$BASE?endpoint=$end").openConnection().getInputStreamText()

    // Fungsi untuk request GET dengan token
    fun getWithToken(end: String, token: String): String =
        URL("$BASE?endpoint=$end&token=${token.e()}").openConnection().getInputStreamText()

    // Fungsi untuk membaca response dari server, baik itu hasil GET atau POST
    private fun URLConnection.getInputStreamText() = getInputStream().bufferedReader().readText()

    // Fungsi untuk mengirim request POST dengan data tertentu
    fun post(data: String): String {
        val conn = URL(BASE).openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.doOutput = true
        conn.outputStream.write(data.toByteArray())
        return conn.inputStream.bufferedReader().readText()
    }
}