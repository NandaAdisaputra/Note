package com.nandaadisaputra.note.model


data class NoteResponse(
    val code: Int,
    val status: String,
    val message: String,
    val data: List<Note>
)
data class SingleNoteResponse(
    val code: Int,
    val status: String,
    val message: String,
    val data: Note
)

data class Note (
    val id : Int,
    val title: String,
    val description: String
)

data class ExportPdfResponse(
    val code: Int,
    val status: String,
    val message: String,
    val data: ExportPdfData
)

data class ExportPdfData(
    val pdfFileName: String,
    val pdfFileUrl: String
)
data class ExportCsvResponse(
    val code: Int,
    val status: String,
    val message: String,
    val data: ExportCsvData
)
data class ExportCsvData(
    val fileName: String,
    val fileUrl: String
)
