package com.example.firsmadiun.viewModel.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firsmadiun.data.models.LaporanModel
import com.example.firsmadiun.data.repository.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReportDetailUiState(
    val laporan: LaporanModel? = null,
    val isLoading: Boolean = true,
    val error: String = "",
    val isAdmin: Boolean = false,
    val isUpdatingStatus: Boolean = false
)

class ReportDetailViewModel : ViewModel() {

    private val firestoreRepository = FirestoreRepository()

    private val _uiState = MutableStateFlow(ReportDetailUiState())
    val uiState: StateFlow<ReportDetailUiState> = _uiState.asStateFlow()

    fun loadLaporan(laporanId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = "") }

            val result = firestoreRepository.getLaporanById(laporanId)
            val isAdmin = firestoreRepository.isAdmin()

            result.fold(
                onSuccess = { laporan ->
                    _uiState.update { it.copy(laporan = laporan, isLoading = false, isAdmin = isAdmin) }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Gagal memuat detail laporan"
                        )
                    }
                }
            )
        }
    }

    fun onUbahStatus(status: String) {
        val laporanId = _uiState.value.laporan?.id ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingStatus = true) }

            firestoreRepository.updateStatusLaporan(laporanId, status)

            // Update lokal langsung tanpa reload
            _uiState.update {
                it.copy(
                    laporan = it.laporan?.copy(status = status),
                    isUpdatingStatus = false
                )
            }
        }
    }
}