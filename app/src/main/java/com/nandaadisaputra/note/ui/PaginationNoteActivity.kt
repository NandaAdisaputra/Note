package com.nandaadisaputra.note.ui

// Import bawaan Android & project
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nandaadisaputra.note.R
import com.nandaadisaputra.note.adapter.NoteAdapter
import com.nandaadisaputra.note.model.Note
import com.nandaadisaputra.note.session.SessionManager
import com.nandaadisaputra.note.ui.auth.LoginActivity
import com.nandaadisaputra.note.viewmodel.NoteViewModel

class PaginationNoteActivity : AppCompatActivity() {
    // Menyimpan catatan yang dipilih untuk proses update
    private var selectNote: Note? = null

    // Mengambil instance ViewModel
    private val vm: NoteViewModel by viewModels()

    // Adapter untuk RecyclerView
    private lateinit var adapter: NoteAdapter

    // RecyclerView untuk menampilkan daftar catatan
    private lateinit var rvNotes: RecyclerView

    // Variabel pagination
    private var page = 1
    private val pageSize = 10
    private var isLoading = false

    // Untuk manajemen sesi login
    private lateinit var sessionManager: SessionManager

    // ProgressBar yang ditampilkan saat memuat data
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi komponen UI
        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etDesc = findViewById<EditText>(R.id.etDescription)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val etSearch = findViewById<EditText>(R.id.etSearch)
        progressBar = findViewById(R.id.progressBar)
        rvNotes = findViewById(R.id.rvNotes)
        sessionManager = SessionManager(this)

        // Setup scroll listener untuk pagination
        setupScrollPagination()

        // Observe data catatan dari ViewModel
        observeNotes()

        // Panggil catatan awal halaman pertama
        vm.getPaginatedNotes(page, pageSize)

        // Inisialisasi adapter dengan callback klik item
        adapter = NoteAdapter(
            mutableListOf(),
            onEditClick = {
                // Mengisi form dengan data catatan yang dipilih
                selectNote = it
                etTitle.setText(it.title)
                etDesc.setText(it.description)
                btnSave.text = "Update"
            },
            onDeleteClick = {
                // Menampilkan konfirmasi hapus
                AlertDialog.Builder(this).apply {
                    setTitle("Hapus Catatan")
                    setMessage("Yakin ingin hapus?")
                    setPositiveButton("Hapus") { _, _ ->
                        vm.deleteNote(it.id) { success ->
                            runOnUiThread {
                                Toast.makeText(
                                    this@PaginationNoteActivity,
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
                // Pindah ke DetailActivity dan kirim data catatan
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("title", note.title)
                intent.putExtra("description", note.description)
                startActivity(intent)
            }
        )

        // Atur RecyclerView dan pasang adapter
        rvNotes.layoutManager = LinearLayoutManager(this)
        rvNotes.adapter = adapter

        // Aksi tombol simpan/update catatan
        btnSave.setOnClickListener {
            val title = etTitle.text.toString()
            val desc = etDesc.text.toString()

            if (selectNote == null) {
                // Tambah catatan baru
                vm.addNote(title, desc) {
                    runOnUiThread {
                        Toast.makeText(this, if (it) "Berhasil" else "Gagal", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // Update catatan yang dipilih
                vm.updateNote(selectNote!!.id, title, desc) {
                    runOnUiThread {
                        Toast.makeText(this, if (it) "Update Berhasil" else "Update Gagal", Toast.LENGTH_SHORT).show()
                        selectNote = null
                        btnSave.text = "Simpan"
                        // Reload data dari halaman pertama
                        page = 1
                        vm.getPaginatedNotes(page, pageSize)
                    }
                }
            }

            // Kosongkan input setelah simpan
            etTitle.text.clear()
            etDesc.text.clear()
        }

        // Fitur pencarian
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    vm.searchNotes(query)
                } else {
                    vm.loadNotes()
                }
            }
        })

        // Tombol logout
        btnLogout.setOnClickListener {
            sessionManager.clearSession()
            Toast.makeText(this, "Logout Berhasil", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    // Observe data catatan dan atur UI berdasarkan hasilnya
    private fun observeNotes() {
        vm.notes.observe(this) { newNotes ->
            adapter.updateData(newNotes)
            isLoading = false
            progressBar.visibility = View.GONE // Sembunyikan loading
        }
    }

    // Konfigurasi scroll listener untuk fitur pagination
    private fun setupScrollPagination() {
        rvNotes.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                val layoutManager = rv.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisible = layoutManager.findLastVisibleItemPosition()

                // Cek apakah posisi scroll sudah mendekati akhir dan masih bisa load halaman baru
                if (lastVisible >= totalItemCount - 1 && !isLoading && !vm.isLastPage.value!!) {
                    isLoading = true
                    progressBar.visibility = View.VISIBLE // Tampilkan loading saat ambil data
                    page++
                    vm.getPaginatedNotes(page, pageSize)
                }
            }
        })
    }
}

//Tips Cepat Hafal:
//ViewModel → by viewModels()
//Gunakan Observer untuk update RecyclerView
//Tambah/Edit: deteksi dengan selectNote == null
//AlertDialog wajib buat hapus
//Cari data: TextWatcher + viewModel.searchNotes()