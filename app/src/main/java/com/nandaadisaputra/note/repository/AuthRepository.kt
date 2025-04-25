package com.nandaadisaputra.note.repository

import android.util.Log
import com.nandaadisaputra.note.model.LoginResponse
import com.nandaadisaputra.note.model.UserData
import com.nandaadisaputra.note.network.UserApi
import org.json.JSONObject

class AuthRepository {

    // Fungsi untuk register user
    fun registerUser(username: String, password: String, email: String): Boolean = try {
        // Memanggil API register dan mengubah response string menjadi JSONObject
        val response = JSONObject(UserApi.registerUser(username, password, email))

        // Mengambil nilai 'status' dari JSON dan membandingkan apakah 'success'
        response.getString("status").equals("success", true)
    } catch (e: Exception) {
        // Menangkap error jika parsing gagal atau response tidak sesuai
        Log.e("RegisterUser", e.message.toString())
        false
    }

    // Fungsi untuk login user, mengembalikan Pair<Boolean, String?>
    fun loginUser(username: String, password: String): Pair<Boolean, String?> {
        return try {
            // Memanggil API login dan parsing JSON response
            val response = JSONObject(UserApi.loginUser(username, password))

            // Mengambil nilai-nilai utama dari response JSON
            val status = response.getString("status")    // Contoh: "success"
            val code = response.getInt("code")           // Contoh: 200
            val message = response.getString("message")  // Contoh: "Login berhasil"

            // Jika status login sukses
            return if (status.equals("success", true)) {
                // Ambil objek "data" dari response
                val dataObj = response.getJSONObject("data")

                // Parsing manual dari dataObj ke dalam data class UserData
                val userData = UserData(
                    id = dataObj.getInt("id"),
                    username = dataObj.getString("username"),
                    email = dataObj.getString("email"),
                    token = dataObj.getString("token")
                )

                // Bungkus semua ke dalam LoginResponse
                val loginResponse = LoginResponse(
                    code = code,
                    status = status,
                    message = message,
                    data = userData
                )

                // Kembalikan Pair true (login berhasil) dan token-nya
                Pair(true, loginResponse.data.token)

            } else {
                // Jika login gagal, kembalikan Pair false dan null
                Pair(false, null)
            }

        } catch (e: Exception) {
            // Tangani error saat parsing JSON atau network gagal
            Log.e("LoginUser", e.message.toString())
            Pair(false, null)
        }
    }
}
