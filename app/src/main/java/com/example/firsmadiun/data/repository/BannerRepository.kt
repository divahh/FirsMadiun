package com.example.firsmadiun.data.repository

import com.example.firsmadiun.data.models.NoPenting
import com.example.firsmadiun.data.models.TipsItem
import com.example.firsmadiun.data.models.TipsKategori
import com.example.firsmadiun.data.models.TipsType
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class BannerRepository {
    private val db = FirebaseFirestore.getInstance()

    /**
     * Ambil semua kategori tips/edukasi beserta item-itemnya.
     * Setiap kategori memiliki subkoleksi "items".
     *
     * @param type TipsType.TIPS atau TipsType.EDUKASI
     */

    suspend fun getKategoriDenganItems(type: TipsType): Result<List<TipsKategori>> {
        return try {
            // 1. Ambil semua dokumen kategori
            val snapshot = db.collection(type.koleksi)
                .orderBy("urutan")
                .get()
                .await()

            val kategoriList = snapshot.documents.map { doc ->
                val kategori = doc.toObject(TipsKategori::class.java) ?: TipsKategori()

                // 2. Untuk tiap kategori, ambil subkoleksi items
                val itemsSnapshot = db.collection(type.koleksi)
                    .document(doc.id)
                    .collection("item")
                    .orderBy("urutan")
                    .get()
                    .await()

                val items = itemsSnapshot.toObjects(TipsItem::class.java)

                kategori.copy(id = doc.id, item = items)
            }

            Result.success(kategoriList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNomor(type: TipsType): Result<List<NoPenting>> {
        return try {
            // Ambil semua dokumen Nomor
            val snapshot = db.collection(type.koleksi)
                .orderBy("urutan")
                .get()
                .await()

            val nomorList = snapshot.documents.map { doc ->
                val nomor = doc.toObject(NoPenting::class.java) ?: NoPenting()

                nomor.copy(id = doc.id)
            }

            Result.success(nomorList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}