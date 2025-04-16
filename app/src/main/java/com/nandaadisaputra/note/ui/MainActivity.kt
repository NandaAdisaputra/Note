package com.nandaadisaputra.note.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nandaadisaputra.note.R
import com.nandaadisaputra.note.adapter.NoteAdapter
import com.nandaadisaputra.note.viewmodel.NoteViewModel

class MainActivity : AppCompatActivity() {
    //Buat instance dari ViewModel menggunakan viewModels()
    private val viewModel:NoteViewModel by viewModels()
    //Adapter untuk menampilkan data di RecyclerView
    private lateinit var adapter: NoteAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ambil referensi komponen dari layout XML
        val titleInput = findViewById<EditText>(R.id.etTitle)
        val descInput = findViewById<EditText>(R.id.etDescription)
        val btnAdd = findViewById<Button>(R.id.btnSave)
        val rvNotes = findViewById<RecyclerView>(R.id.rvNotes)

        //Tampilkan jumlah catatan menggunakan toast setiap kali data berubah
        viewModel.notes.observe(this, Observer {
            Toast.makeText(this,"Jumlah Catatan: ${it.size}", Toast.LENGTH_SHORT).show()
        })

        //Inisialisasi adapter dengan  data kosong
        adapter = NoteAdapter(emptyList())

        //Atur RecyclerView pakai layout vertical
        rvNotes.layoutManager = LinearLayoutManager(this)
        rvNotes.adapter = adapter

        //Perbarui isi adapter setiap kali data di LiveData berubah
        viewModel.notes.observe(this){
            adapter.updateData(it)
        }

        //Aksi ketika tombol simpan di tekan
        btnAdd.setOnClickListener {
            val title = titleInput.text.toString()
            val desc = descInput.text.toString()

            //Kirim data ke viewmodel untuk ditambahkan
            viewModel.addNote(title, desc){ success->
                runOnUiThread {
                    Toast.makeText(this, if(success) "Berhasil" else "Gagal!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        //Input Pencarian
        val searchInput = findViewById<EditText>(R.id.etSearch)
        //Tambahkan listener untuk mendeteksi perubahan teks
        searchInput.addTextChangedListener(object : TextWatcher {
            //Fungsi ini tidak digunakan tapi tetap wajib diisi
            override fun beforeTextChanged(s: CharSequence?,start:Int, count:Int, after:Int) {}

            override fun onTextChanged(s: CharSequence?, start:Int, before:Int,count:Int) {}

            override fun afterTextChanged(s: Editable?) {
               val query = s.toString()
                //Kalau query tidak kosong, cari catatan
                if (query.isNotEmpty()){
                    viewModel.searchNotes(query)
                } else{
                    //kalau kosong, tampilkan semua catatan
                    viewModel.loadNotes()
                }
            }
        })

        //saat pertama kali dibuka, langsung tampilkan semua catatan
        viewModel.loadNotes()

    }
}