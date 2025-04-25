package com.nandaadisaputra.note.network

import com.nandaadisaputra.note.utils.e

object UserApi {

    fun registerUser(username: String, password: String, email: String): String {
        return ApiRequest.post("endpoint=register&username=${username.e()}&password=${password.e()}&email=${email.e()}")
    }

    fun loginUser(username: String, password: String): String {
        return ApiRequest.post("endpoint=login&username=${username.e()}&password=${password.e()}")
    }
}
