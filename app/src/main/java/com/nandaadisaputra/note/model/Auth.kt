package com.nandaadisaputra.note.model

data class LoginResponse(
    val code: Int,
    val status: String,
    val message: String,
    val data: UserData
)

data class UserData(
    val id: Int,
    val username: String,
    val email: String,
    val token: String
)