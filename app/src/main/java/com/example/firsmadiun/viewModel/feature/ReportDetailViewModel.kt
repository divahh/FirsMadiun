package com.example.firsmadiun.viewModel.feature

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firsmadiun.data.models.LaporanForm
import com.example.firsmadiun.data.models.LaporanModel
import com.example.firsmadiun.data.models.LokasiPeta
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
    val isUpdatingStatus: Boolean = false,

    val form: LaporanForm = LaporanForm(),
    val isPetaVisible: Boolean = false,
    val lokasiError: String = ""
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
                    _uiState.update {
                        it.copy(
                            laporan = laporan,
                            isLoading = false,
                            isAdmin = isAdmin,
                            form = it.form.copy(
                                latitude = laporan.latitude,
                                longitude = laporan.longitude,
                                alamatPeta = laporan.alamatPeta
                            )
                        )
                    }
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

    fun onLokasiDipilihDariPeta(lokasi: LokasiPeta) {
        _uiState.update {
            it.copy(
                form = it.form.copy(
                    latitude = lokasi.latitude,
                    longitude = lokasi.longitude,
                    alamatPeta = lokasi.alamat,
                    lokasi = lokasi.alamat.ifBlank { it.form.lokasi }
                ),
                isPetaVisible = false,
                lokasiError = ""
            )
        }
    }

    fun onTogglePeta() {
        _uiState.update { it.copy(isPetaVisible = !it.isPetaVisible) }
    }

    fun onDownloadGambar(context: Context) {
        val urlGambar = _uiState.value.laporan?.fotoBuktiUrl ?: ""
        val kategoriLaporan = _uiState.value.laporan?.kategori ?: "Kejadian"

        if (urlGambar.isBlank()) {
            Toast.makeText(context, "Tidak ada foto untuk diunduh", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // Menggunakan DownloadManager OS Android
            val request = DownloadManager.Request(Uri.parse(urlGambar)).apply {
                setTitle("Foto Bukti Laporan Firs Madiun")
                setDescription("Mengunduh foto bukti laporan $kategoriLaporan...")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

                // Menyimpan ke folder Publik 'Downloads' di HP
                setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "FIRS_Madiun_${kategoriLaporan}_${System.currentTimeMillis()}.jpg"
                )

                // Izinkan scanning oleh media provider agar masuk galeri
                allowScanningByMediaScanner()
            }

            val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)

            Toast.makeText(context, "Mengunduh gambar... Cek panel notifikasi", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal mengunduh: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}