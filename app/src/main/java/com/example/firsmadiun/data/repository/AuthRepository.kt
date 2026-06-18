package com.example.firsmadiun.data.repository

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // user yang sedang login, kalau belum maka null
    val currentUser: FirebaseUser? get() = auth.currentUser

    // mengecek user apakah sudah login
    val isLoggedIn: Boolean get() = auth.currentUser != null

    // function signIn email sama password, kalau success maka ke home
    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("User not found")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("Failed to create user")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePassword(
        passwordLama: String,
        passwordBaru: String
    ): Result<Unit> {
        return try {
            val user = auth.currentUser
                ?: return Result.failure(Exception("User belum login"))

            val email = user.email
                ?: return Result.failure(Exception("Email user tidak ditemukan"))

            // 1. Reauthenticate dulu dengan password lama
            val credential = EmailAuthProvider.getCredential(email, passwordLama)
            user.reauthenticate(credential).await()

            // 2. Baru update ke password baru
            user.updatePassword(passwordBaru).await()

            Result.success(Unit)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(Exception("Kata sandi saat ini salah"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // function signOut untuk keluar dari akun
    fun signOut() {
        auth.signOut()
    }
}