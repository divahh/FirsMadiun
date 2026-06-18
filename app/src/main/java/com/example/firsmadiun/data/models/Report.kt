package com.example.firsmadiun.data.models

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Model laporan kejadian untuk Firestore.
 * @DocumentId otomatis diisi dengan ID dokumen dari Firestore.
 */
data class LaporanModel(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val kategori: String = "Kebakaran",
    val namaPelapor: String = "",
    val noTelepon: String = "",
    val lokasi: String = "",
    val deskripsi: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val alamatPeta: String = "",
    val fotoBuktiUrl: String = "", // Menyimpan URL gambar
    val status: String = StatusLaporan.MENUNGGU,
    @ServerTimestamp
    val createdAt: Timestamp? = null
)

/** Status laporan */
object StatusLaporan {
    const val MENUNGGU  = "menunggu"
    const val DIPROSES  = "diproses"
    const val SELESAI   = "selesai"
}

/**
 * Model data untuk form laporan kejadian.
 */
data class LaporanForm(
    val kategori: String = "Kebakaran",
    val namaPelapor: String = "",
    val noTelepon: String = "",
    val lokasi: String = "",
    val deskripsi: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val alamatPeta: String = "",
    val fotoBukti: Uri? = null, // Menyimpan URI gambar
)

/**
 * Status pengiriman laporan.
 */
sealed class LaporanResult {
    object Idle : LaporanResult()
    object Loading : LaporanResult()
    data class Success(val nomorLaporan: String) : LaporanResult()
    data class Error(val pesan: String) : LaporanResult()
}

/**
 * Model titik lokasi dari peta.
 */
data class LokasiPeta(
    val latitude: Double,
    val longitude: Double,
    val alamat: String = ""
)

val daftarKategori = listOf(
    "Kebakaran",
    "Penyelamatan",
    "Hewan",
    "Pohon",
    "Lainnya"
)