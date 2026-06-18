package com.example.firsmadiun.viewModel.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firsmadiun.data.models.UserProfile
import com.example.firsmadiun.data.repository.AuthRepository
import com.example.firsmadiun.data.repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val userProfile: UserProfile = UserProfile(),
    val isLoading: Boolean = true,
    val isUpdating: Boolean = false,
    val isUpdatingPassword: Boolean = false,
    val message: String = "",
    val isDialogPasswordVisible: Boolean = false,
    val formNama: String = "",
    val formNoTelepon: String = "",

    // State form ubah password
    val passwordLama: String = "",
    val passwordBaru: String = "",
    val konfirmasiPasswordBaru: String = "",
    val passwordLamaError: String = "",
    val passwordBaruError: String = "",
    val konfirmasiError: String = ""
)

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val firestoreRepository = FirestoreRepository()
    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Memanggil fungsi getUser() dari FirestoreRepository milikmu
            firestoreRepository.getUser().fold(
                onSuccess = { profile ->
                    _uiState.update {
                        it.copy(
                            userProfile = profile,
                            formNama = profile.nama,
                            formNoTelepon = profile.noTelepon,
                            isLoading = false
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, message = e.message ?: "Gagal memuat profil") }
                }
            )
        }
    }

    fun onNamaChange(value: String) {
        _uiState.update { it.copy(formNama = value) }
    }

    fun onNoTeleponChange(value: String) {
        _uiState.update { it.copy(formNoTelepon = value) }
    }

    fun onSimpanPerubahan() {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, message = "") }

            val profileBaru = _uiState.value.userProfile.copy(
                nama = _uiState.value.formNama,
                noTelepon = _uiState.value.formNoTelepon
            )

            // Memanggil fungsi simpanUser() dari FirestoreRepository milikmu
            val result = firestoreRepository.simpanUser(profileBaru)

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isUpdating = false, userProfile = profileBaru, message = "Profil Berhasil Diperbarui!") }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isUpdating = false, message = e.message ?: "Gagal menyimpan perubahan") }
                }
            )
        }
    }

    // ── Dialog Password ──────────────────────────────────

    fun togglePasswordDialog(visible: Boolean) {
        _uiState.update {
            it.copy(
                isDialogPasswordVisible = visible,
                // Reset form saat dialog dibuka/ditutup
                passwordLama = "",
                passwordBaru = "",
                konfirmasiPasswordBaru = "",
                passwordLamaError = "",
                passwordBaruError = "",
                konfirmasiError = ""
            )
        }
    }

    fun onPasswordLamaChange(value: String) {
        _uiState.update { it.copy(passwordLama = value, passwordLamaError = "") }
    }

    fun onPasswordBaruChange(value: String) {
        _uiState.update { it.copy(passwordBaru = value, passwordBaruError = "") }
    }

    fun onKonfirmasiPasswordBaruChange(value: String) {
        _uiState.update { it.copy(konfirmasiPasswordBaru = value, konfirmasiError = "") }
    }

    fun onSimpanPassword() {
        if (!validasiPassword()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingPassword = true, message = "") }

            authRepository.updatePassword(
                passwordLama = _uiState.value.passwordLama,
                passwordBaru = _uiState.value.passwordBaru
            ).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isUpdatingPassword = false,
                            isDialogPasswordVisible = false,
                            message = "Kata sandi berhasil diperbarui!",
                            passwordLama = "",
                            passwordBaru = "",
                            konfirmasiPasswordBaru = ""
                        )
                    }
                },
                onFailure = { e ->
                    // Cek apakah error dari password lama salah
                    val pesanError = e.message ?: "Gagal memperbarui kata sandi"
                    if (pesanError.contains("saat ini salah", ignoreCase = true)) {
                        _uiState.update {
                            it.copy(
                                isUpdatingPassword = false,
                                passwordLamaError = "Kata sandi saat ini salah"
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(isUpdatingPassword = false, message = pesanError)
                        }
                    }
                }
            )
        }
    }

    private fun validasiPassword(): Boolean {
        var valid = true
        val s = _uiState.value

        if (s.passwordLama.isBlank()) {
            _uiState.update { it.copy(passwordLamaError = "Kata sandi saat ini wajib diisi") }
            valid = false
        }
        if (s.passwordBaru.isBlank()) {
            _uiState.update { it.copy(passwordBaruError = "Kata sandi baru wajib diisi") }
            valid = false
        } else if (s.passwordBaru.length < 6) {
            _uiState.update { it.copy(passwordBaruError = "Kata sandi minimal 6 karakter") }
            valid = false
        } else if (s.passwordBaru == s.passwordLama) {
            _uiState.update { it.copy(passwordBaruError = "Kata sandi baru tidak boleh sama dengan yang lama") }
            valid = false
        }
        if (s.konfirmasiPasswordBaru.isBlank()) {
            _uiState.update { it.copy(konfirmasiError = "Konfirmasi kata sandi wajib diisi") }
            valid = false
        } else if (s.passwordBaru != s.konfirmasiPasswordBaru) {
            _uiState.update { it.copy(konfirmasiError = "Kata sandi tidak cocok") }
            valid = false
        }
        return valid
    }

    fun onLogout() {
        authRepository.signOut()
    }
}