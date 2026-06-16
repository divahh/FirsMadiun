package com.example.firsmadiun.viewModel.feature

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firsmadiun.data.models.LaporanForm
import com.example.firsmadiun.data.models.LaporanModel
import com.example.firsmadiun.data.models.LaporanResult
import com.example.firsmadiun.data.models.LokasiPeta
import com.example.firsmadiun.data.models.daftarKategori
import com.example.firsmadiun.data.repository.AuthRepository
import com.example.firsmadiun.data.repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LaporanUiState(
    val form: LaporanForm = LaporanForm(),
    val result: LaporanResult = LaporanResult.Idle,
    val kategoriList: List<String> = daftarKategori,
    val isPetaVisible: Boolean = false,

    // Error per field
    val namaError: String = "",
    val teleponError: String = "",
    val lokasiError: String = "",
    val deskripsiError: String = "",
    val fotoError: String = ""
)

@HiltViewModel
class ReportViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val firestoreRepository = FirestoreRepository()
    private val authRepository = AuthRepository()
    private val _uiState = MutableStateFlow(LaporanUiState())
    val uiState: StateFlow<LaporanUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            val firebaseUser = authRepository.currentUser
            val currentUid = firebaseUser?.uid ?: ""

            if (currentUid.isNotBlank()) {
                firestoreRepository.getUser().fold(
                    onSuccess = { userProfile ->
                        // 3. Jika berhasil, isi form menggunakan data asli dari database
                        _uiState.update {
                            it.copy(
                                form = it.form.copy(
                                    namaPelapor = userProfile.nama.ifBlank { userProfile.nama },
                                    noTelepon = userProfile.noTelepon
                                )
                            )
                        }
                    },
                    onFailure = {
                        // Fallback jika database offline / profil belum lengkap, pakai nama email seadanya
                        val emailFallback = firebaseUser?.email?.substringBefore("@") ?: "Pelapor"
                        _uiState.update {
                            it.copy(
                                form = it.form.copy(
                                    namaPelapor = emailFallback,
                                    noTelepon = ""
                                )
                            )
                        }
                    }
                )
            }
        }
    }

    fun onFotoBuktiChange(value: Uri?) {
        _uiState.update { it.copy(form = it.form.copy(fotoBukti = value)) }
    }

    fun onKategoriSelected(kategori: String) {
        _uiState.update { it.copy(form = it.form.copy(kategori = kategori)) }
    }

    fun onNamaPelapor(value: String) {
        _uiState.update { it.copy(form = it.form.copy(namaPelapor = value), namaError = "") }
    }

    fun onNoTelepon(value: String) {
        _uiState.update { it.copy(form = it.form.copy(noTelepon = value), teleponError = "") }
    }

    fun onLokasi(value: String) {
        _uiState.update { it.copy(form = it.form.copy(lokasi = value), lokasiError = "") }
    }

    fun onDeskripsi(value: String) {
        _uiState.update { it.copy(form = it.form.copy(deskripsi = value), deskripsiError = "") }
    }

    /** Dipanggil saat user memilih titik di peta */
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

    fun onKirimLaporan() {
        if (!validasi()) return

        val form = _uiState.value.form
        val user = authRepository.currentUser

        val laporan = LaporanModel(
            userId = user?.uid ?: "",
            kategori = form.kategori,
            namaPelapor = form.namaPelapor,
            noTelepon = form.noTelepon,
            lokasi = form.lokasi,
            deskripsi = form.deskripsi,
            latitude = form.latitude,
            longitude = form.longitude,
            alamatPeta = form.alamatPeta,
        )

        viewModelScope.launch {
            _uiState.update { it.copy(result = LaporanResult.Loading) }

            val result = firestoreRepository.kirimLaporan(
                laporan = laporan,
                fotoBukti = form.fotoBukti,
                context = context
            )

            result.fold(
                onSuccess = { laporanId ->
                    _uiState.update {
                        it.copy(result = LaporanResult.Success(nomorLaporan = laporanId))
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(result = LaporanResult.Error(pesan = e.message ?: "Failed to send report, Please try again"))
                    }
                }
            )
        }
    }

    fun resetResult() {
        _uiState.update { it.copy(result = LaporanResult.Idle) }
    }

    private fun validasi(): Boolean {
        var valid = true
        val f = _uiState.value.form

        if (f.namaPelapor.isBlank()) {
            _uiState.update { it.copy(namaError = "Nama pelapor wajib diisi") }
            valid = false
        }
        if (f.noTelepon.isBlank()) {
            _uiState.update { it.copy(teleponError = "No. telepon wajib diisi") }
            valid = false

        } else if (f.noTelepon.length < 9) {
            _uiState.update { it.copy(teleponError = "No. telepon tidak valid") }
            valid = false
        }
        if (f.lokasi.isBlank() && f.latitude == null) {
            _uiState.update { it.copy(lokasiError = "Lokasi kejadian wajib diisi") }
            valid = false
        }
        if (f.deskripsi.isBlank()) {
            _uiState.update { it.copy(deskripsiError = "Deskripsi kejadian wajib diisi") }
            valid = false
        }
        if (f.fotoBukti == null) {
            _uiState.update { it.copy(fotoError = "Foto bukti wajib diisi") }
            valid = false
        }
        return valid
    }
}