package com.nandaadisaputra.note.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nandaadisaputra.note.R
import com.nandaadisaputra.note.model.Note

class NoteAdapter(private var notes:List<Note>): RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {


    class NoteViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDesc)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }
    //mengambalikan jumlah item yang ada di list catatan
    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.tvTitle.text = note.title
        holder.tvDescription.text = note.description
    }

    fun updateData(newNote:List<Note>){
        notes = newNote
        notifyDataSetChanged()
    }
}