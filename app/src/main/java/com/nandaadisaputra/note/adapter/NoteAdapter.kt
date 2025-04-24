package com.nandaadisaputra.note.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nandaadisaputra.note.R
import com.nandaadisaputra.note.model.Note

// Adapter untuk menampilkan data note pada RecyclerView
class NoteAdapter(
    // Daftar catatan yang akan ditampilkan di RecyclerView
    private val notes: MutableList<Note> = mutableListOf(),

    // Callback untuk aksi edit, jika ada
    private val onEditClick: ((Note) -> Unit)? = null,

    // Callback untuk aksi hapus, jika ada
    private val onDeleteClick: ((Note) -> Unit)? = null,

    // Callback untuk aksi klik item, jika ada
    private val onItemClick: ((Note) -> Unit)? = null
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    // ViewHolder yang memegang tampilan item catatan
    inner class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Menghubungkan komponen UI dalam item layout (TextView, Button)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvDesc: TextView = view.findViewById(R.id.tvDesc)
        val btnEdit: Button? = view.findViewById(R.id.btnEdit)
        val btnDelete: Button? = view.findViewById(R.id.btnDelete)
    }

    // Membuat ViewHolder dan menghubungkannya dengan layout item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        // Menginflate layout item_note untuk setiap item dalam RecyclerView
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    // Mengikat data catatan ke dalam ViewHolder untuk ditampilkan
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]

        // Mengatur teks pada TextView untuk judul dan deskripsi catatan
        holder.tvTitle.text = note.title
        holder.tvDesc.text = note.description

        // Menangani klik pada item untuk membuka detail atau aksi lainnya
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(note) // Memanggil callback onItemClick jika ada
        }

        // Menangani klik pada tombol Edit untuk memodifikasi catatan
        holder.btnEdit?.setOnClickListener {
            onEditClick?.invoke(note) // Memanggil callback onEditClick jika ada
        }

        // Menangani klik pada tombol Delete untuk menghapus catatan
        holder.btnDelete?.setOnClickListener {
            onDeleteClick?.invoke(note) // Memanggil callback onDeleteClick jika ada
        }
    }

    // Mengembalikan jumlah item yang ada dalam daftar catatan
    override fun getItemCount(): Int = notes.size

    // Fungsi untuk memperbarui data catatan
    // Parameter reverse menentukan apakah data baru dibalik urutannya atau tidak
    fun updateData(newNotes: List<Note>, reverse: Boolean = true) {
        // Menyimpan posisi awal data lama
        val startPos = notes.size

        // Menghapus seluruh data catatan yang lama
        notes.clear()

        // Menambahkan data catatan baru, dibalik urutannya jika reverse true
        notes.addAll(if (reverse) newNotes.reversed() else newNotes)

        // Notifikasi bahwa item lama telah dihapus
        notifyItemRangeRemoved(0, startPos)

        // Notifikasi bahwa item baru telah ditambahkan
        notifyItemRangeInserted(0, newNotes.size)
    }
}


// Tips Hafalan Cepat:
// Gunakan huruf pendek untuk parameter (p, vt, h, n) biar hemat ingatan.
// 3 fungsi utama:
// onCreateViewHolder → buat tampilan
// onBindViewHolder → isi data
// getItemCount → jumlah item
// updateData() penting buat refresh data!