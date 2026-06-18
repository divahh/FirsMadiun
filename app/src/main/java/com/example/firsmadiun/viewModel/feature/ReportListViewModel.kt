package com.example.firsmadiun.viewModel.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firsmadiun.data.models.LaporanModel
import com.example.firsmadiun.data.repository.AuthRepository
import com.example.firsmadiun.data.repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReportListUiState(
    val laporanList: List<LaporanModel> = emptyList(),
    val isLoading: Boolean = true,
    val error: String = ""
)

/**
 * ViewModel untuk halaman Daftar Laporan.
 * Realtime — list otomatis update saat data di Firestore berubah.
 */
@HiltViewModel
class ReportListViewModel @Inject constructor() : ViewModel() {

    private val firestoreRepository = FirestoreRepository()
    private val authRepository = AuthRepository() // Pastikan kamu punya repositori Auth untuk ambil currentUser

    private val _uiState = MutableStateFlow(ReportListUiState())
    val uiState: StateFlow<ReportListUiState> = _uiState.asStateFlow()

    init {
        checkRoleAndObserveLaporan()
    }

    private fun checkRoleAndObserveLaporan() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val currentUser = authRepository.currentUser
            val currentUid = currentUser?.uid ?: ""

            // Memanggil fungsi checkIsAdmin dari langkah 3
            firestoreRepository.checkIsAdmin(currentUid).collect { isAdmin ->
                if (isAdmin) {
                    // JIKA ADMIN: Ambil seluruh data laporan yang masuk
                    observeSemuaLaporan()
                } else {
                    // JIKA USER BIASA: Ambil laporan miliknya sendiri saja
                    observeLaporanMilikUser()
                }
            }
        }
    }

    private suspend fun observeSemuaLaporan() {
        firestoreRepository.getSemuaLaporanForAdmin().collect { result ->
            result.fold(
                onSuccess = { list ->
                    _uiState.update { it.copy(laporanList = list, isLoading = false, error = "") }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Gagal memuat semua laporan") }
                }
            )
        }
    }

    private suspend fun observeLaporanMilikUser() {
        firestoreRepository.getLaporanUser().collect { result ->
            result.fold(
                onSuccess = { list ->
                    _uiState.update { it.copy(laporanList = list, isLoading = false, error = "") }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Gagal memuat laporan Anda") }
                }
            )
        }
    }

    fun onDeleteLaporan(laporanId: String) {
        viewModelScope.launch {
            // Opsional: Kamu bisa menambahkan state pemuat/loading jika diperlukan
            firestoreRepository.hapusLaporan(laporanId).fold(
                onSuccess = {
                    // Berhasil terhapus. Karena realtime flow aktif,
                    // list di UI otomatis otomatis langsung terupdate.
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(error = e.message ?: "Gagal menghapus laporan")
                    }
                }
            )
        }
    }
}