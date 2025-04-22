package com.nandaadisaputra.note.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nandaadisaputra.note.R
import com.nandaadisaputra.note.model.Note

// Adapter untuk RecyclerView yang menampilkan daftar catatan
class NoteAdapter(
    private var notes: List<Note>, // Daftar catatan yang akan ditampilkan
    private val onEditClick: (Note) -> Unit, // Aksi saat tombol edit diklik
    private val onDeleteClick: (Note) -> Unit // Aksi saat tombol hapus diklik
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    // ViewHolder untuk merepresentasikan satu item tampilan catatan
    class NoteViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvTitle: TextView = v.findViewById(R.id.tvTitle) // Teks judul
        val tvDesc: TextView = v.findViewById(R.id.tvDesc)   // Teks deskripsi
        val btnEdit: Button = v.findViewById(R.id.btnEdit)   // Tombol edit
        val btnDelete: Button = v.findViewById(R.id.btnDelete) // Tombol hapus
    }

    // Membuat ViewHolder baru dari layout item_note.xml
    override fun onCreateViewHolder(p: ViewGroup, vt: Int): NoteViewHolder {
        val v = LayoutInflater.from(p.context).inflate(R.layout.item_note, p, false)
        return NoteViewHolder(v)
    }

    // Mengembalikan jumlah item dalam daftar
    override fun getItemCount(): Int = notes.size

    // Menghubungkan data catatan dengan tampilan item
    override fun onBindViewHolder(h: NoteViewHolder, pos: Int) {
        val n = notes[pos] // Ambil data catatan berdasarkan posisi
        h.tvTitle.text = n.title // Set judul catatan
        h.tvDesc.text = n.description // Set deskripsi catatan

        // Atur aksi tombol edit saat diklik
        h.btnEdit.setOnClickListener { onEditClick(n) }

        // Atur aksi tombol hapus saat diklik
        h.btnDelete.setOnClickListener { onDeleteClick(n) }
    }

    // Memperbarui daftar data dan membalik urutan agar yang terbaru di atas
    fun updateData(newNote: List<Note>) {
        notes = newNote.reversed() // Balik urutan catatan
        notifyDataSetChanged()     // Beritahu adapter bahwa data berubah
    }
}

// Tips Hafalan Cepat:
// Gunakan huruf pendek untuk parameter (p, vt, h, n) biar hemat ingatan.
// 3 fungsi utama:
// onCreateViewHolder → buat tampilan
// onBindViewHolder → isi data
// getItemCount → jumlah item
// updateData() penting buat refresh data!