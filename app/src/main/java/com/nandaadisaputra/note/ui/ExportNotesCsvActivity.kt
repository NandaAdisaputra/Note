package com.nandaadisaputra.note.ui

import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.nandaadisaputra.note.R
import com.nandaadisaputra.note.viewmodel.NoteViewModel
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

// Activity untuk mengekspor catatan ke dalam file CSV dan otomatis mendownload file-nya
class ExportNotesCsvActivity : AppCompatActivity() {

    // ViewModel untuk proses ekspor
    private val noteViewModel: NoteViewModel by viewModels()

    // Komponen UI
    private lateinit var exportButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_export_notes_csv)

        // Hubungkan komponen UI
        exportButton = findViewById(R.id.export_button)
        progressBar = findViewById(R.id.progress_bar)

        // Observasi hasil ekspor CSV dari ViewModel
        noteViewModel.exportCsvResult.observe(this) { result ->
            // Sembunyikan progress bar saat hasil sudah tersedia
            progressBar.visibility = View.GONE

            val (success, response) = result

            if (success && response != null) {
                val fileUrl = response.data.fileUrl     // URL file CSV dari server
                val fileName = response.data.fileName   // Nama file CSV

                Toast.makeText(this, "Export sukses! Mengunduh file CSV...", Toast.LENGTH_SHORT).show()

                // Mulai proses download CSV otomatis
                downloadCsv(fileUrl, fileName)

            } else {
                // Tampilkan pesan gagal
                Toast.makeText(this, "Export gagal! Coba lagi.", Toast.LENGTH_SHORT).show()
            }
        }

        // Klik tombol ekspor
        exportButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE // Tampilkan loading
            noteViewModel.exportNotesToCsv()      // Mulai ekspor CSV via ViewModel
        }
    }

    // Fungsi untuk mengunduh file CSV ke folder Download
    private fun downloadCsv(fileUrl: String, fileName: String) {
        thread {
            try {
                // Buat koneksi ke URL file CSV
                val url = URL(fileUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                // Jika server memberikan respon 200 (OK)
                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    // Dapatkan direktori Download
                    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    if (!downloadsDir.exists()) downloadsDir.mkdirs() // Buat folder jika belum ada

                    // Buat file tujuan di direktori Download
                    val file = File(downloadsDir, fileName)

                    // Simpan data ke file
                    connection.inputStream.use { input ->
                        FileOutputStream(file).use { output ->
                            input.copyTo(output)
                        }
                    }

                    // Update UI di thread utama
                    runOnUiThread {
                        Toast.makeText(this, "File CSV disimpan di folder Download: $fileName", Toast.LENGTH_LONG).show()
                    }
                } else {
                    // Jika respons bukan 200 OK
                    runOnUiThread {
                        Toast.makeText(this, "Gagal mengunduh file dari server.", Toast.LENGTH_SHORT).show()
                    }
                }

                connection.disconnect()
            } catch (e: Exception) {
                // Tangani error saat proses download
                runOnUiThread {
                    Toast.makeText(this, "Error download: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
