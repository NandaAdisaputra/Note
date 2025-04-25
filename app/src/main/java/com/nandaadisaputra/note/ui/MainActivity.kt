package com.nandaadisaputra.note.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nandaadisaputra.note.R
import com.nandaadisaputra.note.adapter.NoteAdapter
import com.nandaadisaputra.note.model.Note
import com.nandaadisaputra.note.session.SessionManager
import com.nandaadisaputra.note.ui.auth.LoginActivity
import com.nandaadisaputra.note.viewmodel.NoteViewModel

class MainActivity : AppCompatActivity() {

    private var selectNote: Note? = null
    private val vm: NoteViewModel by viewModels()
    private lateinit var adapter: NoteAdapter
    private lateinit var sessionManager: SessionManager
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Inisialisasi
        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etDesc = findViewById<EditText>(R.id.etDescription)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val rvNotes = findViewById<RecyclerView>(R.id.rvNotes)
        val etSearch = findViewById<EditText>(R.id.etSearch)
        progressBar = findViewById(R.id.progressBar)
        sessionManager = SessionManager(this)

        // Observasi perubahan status loading
        vm.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                progressBar.visibility = View.VISIBLE // Menampilkan ProgressBar
            } else {
                progressBar.visibility = View.GONE // Menyembunyikan ProgressBar
            }
        })

        adapter = NoteAdapter(
            mutableListOf(),
            onEditClick = {
                selectNote = it
                etTitle.setText(it.title)
                etDesc.setText(it.description)
                btnSave.text = "Update"
            },
            onDeleteClick = {
                AlertDialog.Builder(this).apply {
                    setTitle("Hapus Catatan")
                    setMessage("Yakin ingin hapus?")
                    setPositiveButton("Hapus") { _, _ ->
                        vm.deleteNote(it.id) { success ->
                            runOnUiThread {
                                Toast.makeText(
                                    this@MainActivity,
                                    if (success) "Berhasil dihapus" else "Gagal menghapus",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    setNegativeButton("Batal", null)
                    show()
                }
            },
            onItemClick = { note ->
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("title", note.title)
                intent.putExtra("description", note.description)
                startActivity(intent)
            }
        )

        rvNotes.layoutManager = LinearLayoutManager(this)
        rvNotes.adapter = adapter

        vm.notes.observe(this, Observer {
            adapter.updateData(it)
        })

        btnSave.setOnClickListener {
            val title = etTitle.text.toString()
            val desc = etDesc.text.toString()

            if (selectNote == null) {
                vm.addNote(title, desc) {
                    runOnUiThread {
                        Toast.makeText(this, if (it) "Berhasil" else "Gagal", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                vm.updateNote(selectNote!!.id, title, desc) {
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            if (it) "Update Berhasil" else "Update Gagal",
                            Toast.LENGTH_SHORT
                        ).show()
                        selectNote = null
                        btnSave.text = "Simpan"
                    }
                }
            }

            etTitle.text.clear()
            etDesc.text.clear()
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val q = s.toString()
                if (q.isNotEmpty()) vm.searchNotes(q) else vm.loadNotes()
            }
        })

        vm.loadNotes()

        btnLogout.setOnClickListener {
            sessionManager.clearSession()
            Toast.makeText(this, "Logout Berhasil", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}


//Tips Cepat Hafal:
//ViewModel → by viewModels()
//Gunakan Observer untuk update RecyclerView
//Tambah/Edit: deteksi dengan selectNote == null
//AlertDialog wajib buat hapus
//Cari data: TextWatcher + viewModel.searchNotes()