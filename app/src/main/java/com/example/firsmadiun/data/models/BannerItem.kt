package com.example.firsmadiun.data.models

import androidx.compose.ui.graphics.vector.ImageVector

data class BannerItem(
    val id: String,
    val judul: String,
    val subjudul: String,
    val icon: ImageVector,
    val isPrimary: Boolean  // true = merah (Tips), false = gelap (Edukasi)
)