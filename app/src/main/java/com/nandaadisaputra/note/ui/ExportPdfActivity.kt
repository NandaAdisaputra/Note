package com.nandaadisaputra.note.ui

import android.os.Bundle
import android.os.Environment
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
    //komponen UI
    private lateinit var btnExportPdf : Button
    private lateinit var tvResult: TextView
    private lateinit var  progressBar: ProgressBar
    //ViewModel untuk ekspor PDF
    private val viewModel: NoteViewModel by viewModels()

    //File PDF yang telah diunduh
    private  var downloadedPdfFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_export_pdf)

        //Inisialisasi komponen dari layout
        btnExportPdf = findViewById(R.id.btnExportPdf)
        tvResult = findViewById(R.id.tvResult)
        progressBar = findViewById(R.id.exportPdfProgressBar)

        //Ketika tombol diklik , mulai menampilkan proses progressbar
        btnExportPdf.setOnClickListener {
            progressBar.visibility = View.VISIBLE //Tampilkan loading
            viewModel.exportNotesToPdf() //Panggil fungsi ekspor dari  ViewModel
        }
        //Observasi hasil ekspor dari ViewModel
        viewModel.exportPdfResult.observe(this){(success, response) ->
            progressBar.visibility = View.GONE //Sembuyikan loading

            if (success && response != null) {
                val pdfName = response.data .pdfFileName // Ambil nama file dari server
                val pdfUrl = response.data.pdfFileUrl // Ambil URL file PDF dari Server

                //Tampilkan pesan sukses ke user
                tvResult.text ="Berhasil di ekspor sebagai $pdfName\nMengunduh file...."
                //Proses download dijalankan di thread terpisah agar UI tidak freeze
                thread {
                    try {
                        //Koneksi ke URL PDF
                        val url = URL (pdfUrl)
                        val connection = url.openConnection() as HttpURLConnection
                        connection.connect()
                        //Jika server merespons dengan status OK ( 200 )
                        if (connection.responseCode == HttpURLConnection.HTTP_OK){
                            //ambil direktori Download ( penyimpanan publik )
                            val downloadsDir = Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS)
                            //Buat folder jika belum ada
                            if (!downloadsDir.exists()) downloadsDir.mkdirs()
                            //Buat file tujuan di folder Download
                            val file = File (downloadsDir, pdfName)
                            //Salin data dari koneksi ke file
                            connection.inputStream.use { input ->
                                FileOutputStream(file).use { output ->
                                    input.copyTo(output)
                                }
                            }
                            //Simpan referensi ke file
                            downloadedPdfFile = file

                            //Update tampilan UI di thread utama
                            runOnUiThread{
                                tvResult.append("\nFile berhasil diunduh ke folder Download.")
                                Toast.makeText(this,"File disimpan: ${file.name}", Toast.LENGTH_LONG).show()
                            }
                        } else{
                            //Jika response server bukan 200
                            runOnUiThread {
                                tvResult.append("\nGagal mengunduh file dari server.")
                            }
                        }
                        connection.disconnect() //Tutup koneksi
                    }catch (e: Exception){
                        //Tangani error download ( misal koneksi gagal )
                        runOnUiThread {
                            tvResult.append("\nError saat mengunduh: ${e.message}")
                        }
                    }

                }

            } else{
                //jika ekspor gagal
                tvResult.text ="Gagal Mengekspor Catatan"
                Toast.makeText(this,"Export gagal! Silahkan coba lagi.", Toast.LENGTH_SHORT).show()
            }

        }
    }
}