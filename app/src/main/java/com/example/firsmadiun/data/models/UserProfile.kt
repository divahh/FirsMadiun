package com.example.firsmadiun.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class UserProfile(
    @DocumentId
    val uid: String = "",
    val nama: String = "",
    val email: String = "",
    val noTelepon: String ="",
    val fotoUrl: String = "",
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    val admin: Boolean = false
)