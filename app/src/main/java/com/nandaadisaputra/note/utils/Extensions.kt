
package com.nandaadisaputra.note.utils

import java.net.URLEncoder

// Fungsi ekstensi untuk melakukan encoding pada string agar aman dikirim melalui URL
fun String.e(): String = URLEncoder.encode(this, "UTF-8")
