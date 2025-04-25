package com.nandaadisaputra.note.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
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

class ExportPdfActivity : AppCompatActivity() {

    // Deklarasi komponen UI
    private lateinit var btnExportPdf: Button
    private lateinit var tvResult: TextView
    private lateinit var progressBar: ProgressBar

    // ViewModel yang bertugas mengatur data & komunikasi dengan server
    private val viewModel: NoteViewModel by viewModels()

    // Variabel untuk menyimpan file PDF hasil download
    private var downloadedPdfFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Menghubungkan layout XML dengan activity
        setContentView(R.layout.activity_export_pdf)

        // Inisialisasi komponen UI dari XML
        btnExportPdf = findViewById(R.id.btnExportPdf)
        tvResult = findViewById(R.id.tvResult)
        progressBar = findViewById(R.id.exportPdfProgressBar)

        // Saat tombol diklik, mulai proses ekspor dan tampilkan progressBar
        btnExportPdf.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            viewModel.exportNotesToPdf()
        }

        // Observasi hasil ekspor dari ViewModel
        viewModel.exportPdfResult.observe(this) { (success, response) ->
            // Sembunyikan progressBar setelah dapat respon
            progressBar.visibility = View.GONE

            // Jika ekspor berhasil dan respons tersedia
            if (success && response != null) {
                val pdfName = response.data.pdfFileName  // Nama file dari response
                val pdfUrl = response.data.pdfFileUrl    // URL file PDF dari server

                // Tampilkan pesan berhasil di UI
                tvResult.text = "Berhasil diekspor sebagai $pdfName\nMengunduh file..."

                // Proses pengunduhan dilakukan di thread terpisah
                thread {
                    try {
                        val url = URL(pdfUrl)
                        val connection = url.openConnection() as HttpURLConnection
                        connection.connect()

                        // Jika response code dari server 200 OK
                        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                            val file = File(cacheDir, pdfName)  // Simpan di folder cache aplikasi
                            connection.inputStream.use { input ->
                                FileOutputStream(file).use { output ->
                                    input.copyTo(output) // Salin konten dari server ke file
                                }
                            }

                            // Simpan file yang telah diunduh
                            downloadedPdfFile = file

                            // Update UI di thread utama
                            runOnUiThread {
                                tvResult.append("\nFile berhasil diunduh.")
                            }
                        } else {
                            // Jika response code bukan 200
                            runOnUiThread {
                                tvResult.append("\nGagal mengunduh file.")
                            }
                        }

                        connection.disconnect()
                    } catch (e: Exception) {
                        // Jika terjadi error saat download
                        runOnUiThread {
                            tvResult.append("\nError: ${e.message}")
                        }
                    }
                }

            } else {
                // Jika gagal ekspor, tampilkan pesan gagal
                tvResult.text = "Gagal mengekspor catatan."
                Toast.makeText(this, "Export gagal! Coba lagi.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
