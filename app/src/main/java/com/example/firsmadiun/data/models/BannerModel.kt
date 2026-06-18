package com.example.firsmadiun.data.models

import com.google.firebase.firestore.DocumentId

data class TipsKategori(
    @DocumentId
    val id: String = "",
    val judul: String = "",
    val subjudul: String = "",
    val icon: String = "",      // nama icon string, dipetakan ke ImageVector di UI
    val urutan: Int = 0,
    val item: List<TipsItem> = emptyList()   // diisi setelah fetch subkoleksi
)

/**
 * Model satu item tips di dalam kategori (subkoleksi items).
 */
data class TipsItem(
    @DocumentId
    val id: String = "",
    val judul: String = "",
    val deskripsi: String = "",
    val urutan: Int = 0
)

data class NoPenting(
    @DocumentId
    val id: String = "",
    val judul: String = "",
    val nomor: String = "",
    val deskripsi: String = "",
    val urutan: Int = 0
)

enum class TipsType(val koleksi: String, val judul: String, val warna: String) {
    TIPSEDU("tips&edu", "Tips & Edukasi", "orange"),
    NOPENTING("no_penting", "No. Penting", "black")
}