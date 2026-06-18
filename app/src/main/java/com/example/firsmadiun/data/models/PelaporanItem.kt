package com.example.firsmadiun.data.models

import androidx.compose.ui.graphics.vector.ImageVector

data class PelaporanItem(
    val id: String,
    val judul: String,
    val subjudul: String,
    val icon: ImageVector,
    val badge: Int = 0  // badge count, 0 = tidak tampil
)