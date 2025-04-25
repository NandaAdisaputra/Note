package com.nandaadisaputra.note.ui

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.nandaadisaputra.note.R
import com.nandaadisaputra.note.viewmodel.NoteViewModel

// Activity untuk mengekspor catatan ke dalam file CSV
class ExportNotesCsvActivity : AppCompatActivity() {

    // Inisialisasi ViewModel menggunakan delegasi by viewModels()
    private val noteViewModel: NoteViewModel by viewModels()

    // Komponen UI
    private lateinit var exportButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Menghubungkan layout XML dengan activity ini
        setContentView(R.layout.activity_export_notes_csv)

        // Inisialisasi view dari layout
        exportButton = findViewById(R.id.export_button)
        progressBar = findViewById(R.id.progress_bar)

        // Observasi LiveData dari ViewModel untuk mengetahui hasil ekspor CSV
        noteViewModel.exportCsvResult.observe(this) { result ->
            // Setelah proses selesai, sembunyikan progressBar
            progressBar.visibility = ProgressBar.INVISIBLE

            // Destrukturisasi hasil menjadi success dan response
            val (success, response) = result

            if (success) {
                // Jika sukses, ambil URL file dari response dan tampilkan melalui Toast
                val fileUrl = response?.data?.fileUrl
                Toast.makeText(this, "Export sukses! File tersedia di: $fileUrl", Toast.LENGTH_LONG).show()
            } else {
                // Jika gagal, tampilkan pesan error
                Toast.makeText(this, "Export gagal! Coba lagi.", Toast.LENGTH_SHORT).show()
            }
        }

        // Ketika tombol export ditekan
        exportButton.setOnClickListener {
            // Tampilkan progressBar saat proses ekspor berlangsung
            progressBar.visibility = ProgressBar.VISIBLE

            // Panggil fungsi ViewModel untuk mengekspor catatan ke CSV
            noteViewModel.exportNotesToCsv()
        }
    }
}
