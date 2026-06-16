package com.example.firsmadiun.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.firsmadiun.data.models.LaporanModel
import com.example.firsmadiun.data.models.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import androidx.core.graphics.scale

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val currentUid get() = auth.currentUser?.uid
        ?: throw Exception("User not logged in")

    fun checkIsAdmin(uid: String): Flow<Boolean> = callbackFlow {
        if (uid.isBlank()) {
            trySend(false)
            close()
            return@callbackFlow
        }

        val docRef = db.collection("users").document(uid)

        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(false) // Kalau error maka bukan admin
                return@addSnapshotListener
            }

            // Mengambil nilai boolean dari field 'isAdmin', jika null/tidak ada maka fallback ke false
            val isAdmin = snapshot?.getBoolean("isAdmin") ?: false
            trySend(isAdmin)
        }

        // Otomatis menghapus listener saat coroutine selesai/cleared
        awaitClose { listener.remove() }
    }

    // ─────────────────────────────────────────────
    // ADMIN
    // ─────────────────────────────────────────────

    fun getSemuaLaporanForAdmin(): Flow<Result<List<LaporanModel>>> = callbackFlow {
        val query = db.collection("laporan")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Result.failure(error))
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val listLaporan = snapshot.toObjects(LaporanModel::class.java)
                trySend(Result.success(listLaporan))
            }
        }
        awaitClose { listener.remove() }
    }

    // ─────────────────────────────────────────────
    // USER
    // ─────────────────────────────────────────────

    /**
     * Simpan atau update profil user di Firestore.
     * Dipanggil setelah Sign Up atau update profil.
     */
    suspend fun simpanUser(user: UserProfile): Result<Unit> {
        return try {
            db.collection("users")
                .document(currentUid)
                .set(user)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Ambil profil user yang sedang login.
     */
    suspend fun getUser(): Result<UserProfile> {
        return try {
            val snapshot = db.collection("users")
                .document(currentUid)
                .get()
                .await()

            val user = snapshot.toObject(UserProfile::class.java)
                ?: throw Exception("Profile not found")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Cek apakah user yang login adalah admin */
    suspend fun isAdmin(): Boolean {
        return try {
            val snapshot = db.collection("users").document(currentUid).get().await()
            snapshot.getBoolean("isAdmin") ?: false
        } catch (e: Exception) {
            false
        }
    }

    // ─────────────────────────────────────────────
    // LAPORAN
    // ─────────────────────────────────────────────

    /**
     * Kirim laporan baru ke Firestore.
     * Jika ada foto, upload ke Storage dulu lalu simpan URL-nya
     */
    suspend fun kirimLaporan(
        context: Context,
        laporan: LaporanModel,
        fotoBukti: Uri? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            // Upload foto ke storage
            val fotoUrl = if (fotoBukti != null) {
                val bytesGambar = kompresGambar(context, fotoBukti)

                uploadFoto(bytesGambar).getOrThrow()
            } else ""

            // Menyimpan laporan ke firestore
            val laporanFoto = laporan.copy(
                userId = currentUid,
                fotoBuktiUrl = fotoUrl
            )

            val docRef = db.collection("laporan")
                .add(laporanFoto)
                .await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Ambil semua laporan milik user yang login.
     * Realtime — menggunakan Flow sehingga UI update otomatis.
     */

    fun getLaporanUser(): Flow<Result<List<LaporanModel>>> = callbackFlow {
        val listener = db.collection("laporan")
            .whereEqualTo("userId", currentUid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                val list = snapshot?.toObjects(LaporanModel::class.java) ?: emptyList()
                trySend(Result.success(list))
            }
        awaitClose { listener.remove() }
    }

    /**
     * Ambil detail satu laporan berdasarkan ID.
     */
    suspend fun getLaporanById(laporanId: String): Result<LaporanModel> {
        return try {
            val snapshot = db.collection("laporan")
                .document(laporanId)
                .get()
                .await()

            val laporan = snapshot.toObject(LaporanModel::class.java)
                ?: throw Exception("Laporan not found")

            Result.success(laporan)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Update status laporan — hanya admin yang bisa */
    suspend fun updateStatusLaporan(laporanId: String, status: String): Result<Unit> {
        return try {
            db.collection("laporan").document(laporanId)
                .update("status", status)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─────────────────────────────────────────────
    // STORAGE — Upload Foto
    // ─────────────────────────────────────────────

    /**
     * Upload foto bukti ke Firebase Storage.
     * Path: laporan/{uid}/{uuid}.jpg
     * @return URL download foto
     */
    val currentUserId = auth.currentUser?.uid ?: "anonymous"
    private suspend fun uploadFoto(bytesGambar: ByteArray): Result<String> {
        return try {
            val storageRef = storage.reference.child("laporan/$currentUserId/foto_${System.currentTimeMillis()}.jpg")

            // Gunakan putBytes untuk mengupload data byte hasil kompresi
            storageRef.putBytes(bytesGambar).await()

            val downloadUrl = storageRef.downloadUrl.await()
            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ─────────────────────────────────────────────
    // STORAGE — Kompres Image
    // ─────────────────────────────────────────────

    /**
     * Kompres gambar sebelum diupload ke Firebase Storage.
     */
    private fun kompresGambar(context: Context, imageUri: Uri): ByteArray {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        // Batas resolusi maksimal (1200px)
        val maxDimension = 1200
        val width = originalBitmap.width
        val height = originalBitmap.height
        val newWidth: Int
        val newHeight: Int

        if (width > height) {
            newWidth = maxDimension
            newHeight = (maxDimension * height) / width
        } else {
            newHeight = maxDimension
            newWidth = (maxDimension * width) / height
        }

        val resizedBitmap = originalBitmap.scale(newWidth, newHeight)

        // Kompres ke JPEG dengan kualitas 75%
        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)

        val byteArray = outputStream.toByteArray()

        // Recycle bitmap agar tidak memakan memori
        originalBitmap.recycle()
        resizedBitmap.recycle()
        outputStream.close()

        return  byteArray
    }
}