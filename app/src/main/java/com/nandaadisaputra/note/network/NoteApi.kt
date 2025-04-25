package com.nandaadisaputra.note.network

import com.nandaadisaputra.note.utils.e

object NoteApi {

    // Fungsi untuk mendapatkan semua catatan (menggunakan GET)
    fun getNotes(): String = ApiRequest.get("getnotes")

    // Fungsi untuk mendapatkan detail catatan berdasarkan ID (menggunakan GET)
    fun getNoteById(id: String): String = ApiRequest.get("getNoteById&id=${id.e()}")

    // Fungsi untuk menampilkan catatan dengan token dan paginasi
    fun getNotesPaginatedWithToken(page: Int, limit: Int, token: String): String {
        return ApiRequest.get("getNotesPaginatedwithToken&page=$page&limit=$limit&token=${token.e()}")
    }

    // Fungsi untuk mencari catatan berdasarkan query tertentu (menggunakan GET)
    fun searchNotes(query: String): String = ApiRequest.get("searchNotes&query=${query.e()}")

    // Fungsi untuk menambahkan catatan baru (menggunakan POST)
    fun addNote(title: String, description: String): String {
        return ApiRequest.post("endpoint=add_note&title=${title.e()}&description=${description.e()}")
    }

    // Fungsi untuk mengupdate catatan berdasarkan ID (menggunakan POST)
    fun updateNote(id: String, title: String, description: String): String {
        return ApiRequest.post("endpoint=update_note&id=${id.e()}&title=${title.e()}&description=${description.e()}")
    }

    // Fungsi untuk menghapus catatan berdasarkan ID (menggunakan POST)
    fun deleteNote(id: String): String {
        return ApiRequest.post("endpoint=delete_note&id=${id.e()}")
    }
    //Fungsi untuk export CSV semua catatan
    fun exportNotesToCsv(): String = ApiRequest.get("exportcsv")

    //Fungsi untuk export PDF semua catatan
    fun exportNotesToPdf(): String = ApiRequest.get("exportpdf")
}
