package com.example.firsmadiun.viewModel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firsmadiun.data.models.UserProfile
import com.example.firsmadiun.data.repository.AuthRepository
import com.example.firsmadiun.data.repository.FirestoreRepository
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val nama: String = "",
    val email: String = "",
    val noTelepon: String = "",
    val password: String = "",
    val konfirmasiPassword: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,

    // Error per field
    val namaError: String = "",
    val emailError: String = "",
    val teleponError: String = "",
    val passwordError: String = "",
    val konfirmasiError: String = "",
    val registerError: String = ""
)

class RegisterViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    private val firestoreRepository = FirestoreRepository()

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNama(value: String) {
        _uiState.update { it.copy(nama = value, namaError = "") }
    }

    fun onEmail(value: String) {
        _uiState.update { it.copy(email = value, emailError = "") }
    }

    fun onNoTelepon(value: String) {
        _uiState.update { it.copy(noTelepon = value, teleponError = "") }
    }

    fun onPassword(value: String) {
        _uiState.update { it.copy(password = value, passwordError = "") }
    }

    fun onKonfirmasiPassword(value: String) {
        _uiState.update { it.copy(konfirmasiPassword = value, konfirmasiError = "") }
    }

    fun onDaftar() {
        if (!validasi()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, registerError = "") }

            val s = _uiState.value

            // 1. Buat akun Firebase Auth
            val authResult = authRepository.signUp(
                email = s.email.trim(),
                password = s.password
            )

            authResult.fold(
                onSuccess = { user ->
                    // 2. Simpan profil ke Firestore
                    firestoreRepository.simpanUser(
                        UserProfile(
                            uid = user.uid,
                            nama = s.nama.trim(),
                            email = s.email.trim(),
                            noTelepon = s.noTelepon.trim()
                        )
                    )
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                },
                onFailure = { e ->
                    val pesan = when (e) {
                        is FirebaseAuthUserCollisionException ->
                            "Email sudah terdaftar. Gunakan email lain."
                        is FirebaseAuthWeakPasswordException ->
                            "Kata sandi terlalu lemah. Minimal 6 karakter."
                        else -> "Pendaftaran gagal. Coba lagi."
                    }
                    _uiState.update { it.copy(isLoading = false, registerError = pesan) }
                }
            )
        }
    }

    private fun validasi(): Boolean {
        var valid = true
        val s = _uiState.value

        if (s.nama.isBlank()) {
            _uiState.update { it.copy(namaError = "Nama lengkap wajib diisi") }
            valid = false
        }
        if (s.email.isBlank()) {
            _uiState.update { it.copy(emailError = "Email wajib diisi") }
            valid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.email.trim()).matches()) {
            _uiState.update { it.copy(emailError = "Format email tidak valid") }
            valid = false
        }
        if (s.noTelepon.isBlank()) {
            _uiState.update { it.copy(teleponError = "No. telepon wajib diisi") }
            valid = false
        } else if (s.noTelepon.length < 9) {
            _uiState.update { it.copy(teleponError = "No. telepon tidak valid") }
            valid = false
        }
        if (s.password.isBlank()) {
            _uiState.update { it.copy(passwordError = "Kata sandi wajib diisi") }
            valid = false
        } else if (s.password.length < 6) {
            _uiState.update { it.copy(passwordError = "Kata sandi minimal 6 karakter") }
            valid = false
        }
        if (s.konfirmasiPassword.isBlank()) {
            _uiState.update { it.copy(konfirmasiError = "Konfirmasi kata sandi wajib diisi") }
            valid = false
        } else if (s.password != s.konfirmasiPassword) {
            _uiState.update { it.copy(konfirmasiError = "Kata sandi tidak cocok") }
            valid = false
        }
        return valid
    }
}