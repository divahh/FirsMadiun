package com.example.firsmadiun.viewModel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firsmadiun.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Data class yang merepresentasikan state halaman login.
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val namaError: String = "",
    val passwordError: String = "",
    val loginError: String = "",
    val isLoginSuccess: Boolean = false
)

/**
 * ViewModel untuk halaman Login.
 * Mengelola state dan logika validasi login.
 */
class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, namaError = "") }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = "") }
    }

    fun onLoginClick() {
        if (!validateInput()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loginError = "") }

            val result = authRepository.signIn(
                email = _uiState.value.email.trim(),
                password = _uiState.value.password
            )

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isLoginSuccess = true) }
                },
                onFailure = { exception ->
                    val pesan = when (exception) {
                        is FirebaseAuthInvalidUserException ->
                            "Akun tidak ditemukan. Silakan daftar terlebih dahulu."
                        is FirebaseAuthInvalidCredentialsException ->
                            "Email atau kata sandi salah, silakan coba lagi."
                        else ->
                            "Terjadi kesalahan saat login. Silakan coba lagi."
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loginError = pesan
                        )
                    }
                }
            )
        }
    }

    private fun validateInput(): Boolean {
        var isValid = true

        if (_uiState.value.email.isBlank()) {
            _uiState.update { it.copy(namaError = "Email tidak boleh kosong") }
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(_uiState.value.email.trim()).matches()) {
            _uiState.update { it.copy(namaError = "Format email tidak valid") }
            isValid = false
        }

        if (_uiState.value.password.isBlank()) {
            _uiState.update { it.copy(passwordError = "Kata sandi tidak boleh kosong") }
            isValid = false
        } else if (_uiState.value.password.length < 6) {
            _uiState.update { it.copy(passwordError = "Kata sandi minimal 6 karakter") }
            isValid = false
        }

        return isValid
    }

    fun clearLoginError() {
        _uiState.update { it.copy(loginError = "") }
    }
}