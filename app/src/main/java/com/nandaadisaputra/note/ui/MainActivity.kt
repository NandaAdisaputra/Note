package com.nandaadisaputra.note.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nandaadisaputra.note.R
import com.nandaadisaputra.note.adapter.NoteAdapter
import com.nandaadisaputra.note.model.Note
import com.nandaadisaputra.note.viewmodel.NoteViewModel

class MainActivity : AppCompatActivity() {

    // Variabel untuk menyimpan catatan yang sedang dipilih (untuk keperluan edit)
    private var selectNote: Note? = null

    // ViewModel untuk mengelola data catatan
    private val vm: NoteViewModel by viewModels()

    // Adapter untuk RecyclerView
    private lateinit var adapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi elemen-elemen UI
        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etDesc = findViewById<EditText>(R.id.etDescription)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val rvNotes = findViewById<RecyclerView>(R.id.rvNotes)

        // Mengamati perubahan data pada ViewModel dan memperbarui RecyclerView
        vm.notes.observe(this, Observer {
            // Memperbarui data di adapter dengan data terbaru dari ViewModel
            adapter.updateData(it)
        })

        // Inisialisasi adapter dengan callback untuk edit dan delete catatan
        adapter = NoteAdapter(emptyList(),
            onEditClick = {
                // Ketika tombol edit diklik, set nilai selectNote untuk edit
                selectNote = it
                // Isi data yang ada di catatan ke dalam EditText untuk diubah
                etTitle.setText(it.title)
                etDesc.setText(it.description)
                // Ubah teks tombol Save menjadi "Update"
                btnSave.text = "Update"
            },
            onDeleteClick = {
                // Ketika tombol delete diklik, tampilkan dialog konfirmasi
                AlertDialog.Builder(this).apply {
                    setTitle("Hapus Catatan")
                    setMessage("Yakin ingin hapus?")
                    setPositiveButton("Hapus") { _, _ ->
                        // Panggil fungsi untuk menghapus catatan dari ViewModel
                        vm.deleteNote(it.id) {
                            runOnUiThread {
                                // Tampilkan Toast jika berhasil menghapus
                                Toast.makeText(this@MainActivity, "Berhasil dihapus", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    setNegativeButton("Batal", null)
                    show()
                }
            }
        )

        // Set RecyclerView dengan layout manager dan adapter
        rvNotes.layoutManager = LinearLayoutManager(this)
        rvNotes.adapter = adapter

        // Ketika tombol Save diklik
        btnSave.setOnClickListener {
            val title = etTitle.text.toString()
            val desc = etDesc.text.toString()

            // Jika tidak ada catatan yang dipilih, maka tambahkan catatan baru
            if (selectNote == null) {
                vm.addNote(title, desc) {
                    runOnUiThread {
                        // Tampilkan Toast sesuai hasil operasi
                        Toast.makeText(this, if (it) "Berhasil" else "Gagal", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // Jika ada catatan yang dipilih, lakukan update pada catatan tersebut
                vm.updateNote(selectNote!!.id, title, desc) {
                    runOnUiThread {
                        // Tampilkan Toast sesuai hasil operasi
                        Toast.makeText(this, if (it) "Update Berhasil" else "Update Gagal", Toast.LENGTH_SHORT).show()
                        selectNote = null
                        btnSave.text = "Simpan"  // Ubah kembali teks tombol Save
                    }
                }
            }

            // Kosongkan EditText setelah selesai
            etTitle.text.clear()
            etDesc.text.clear()
        }

        // Cari catatan berdasarkan input pencarian
        val etSearch = findViewById<EditText>(R.id.etSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val q = s.toString()
                // Jika query pencarian tidak kosong, cari catatan berdasarkan query
                if (q.isNotEmpty()) vm.searchNotes(q) else vm.loadNotes()
            }
        })

        // Memuat catatan pada saat aplikasi pertama kali dijalankan
        vm.loadNotes()
    }
}


//Tips Cepat Hafal:
//ViewModel → by viewModels()
//Gunakan Observer untuk update RecyclerView
//Tambah/Edit: deteksi dengan selectNote == null
//AlertDialog wajib buat hapus
//Cari data: TextWatcher + viewModel.searchNotes()