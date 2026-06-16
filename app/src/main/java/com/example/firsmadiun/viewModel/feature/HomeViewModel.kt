package com.example.firsmadiun.viewModel.feature

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.lifecycle.ViewModel
import com.example.firsmadiun.data.models.BannerItem
import com.example.firsmadiun.data.models.KategoriLaporan
import com.example.firsmadiun.data.models.PelaporanItem
import com.example.firsmadiun.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HomeUiState(
    val namaUser: String = "sss@gmail",
    val kategoriList: List<KategoriLaporan> = emptyList(),
    val bannerList: List<BannerItem> = emptyList(),
    val pelaporanList: List<PelaporanItem> = emptyList(),
    val isLoading: Boolean = false
)

class HomeViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        // Ambil nama dari email di firebase auth
        val user = authRepository.currentUser
        val namaUser = user?.displayName?.ifBlank { user.email } ?: user?.email ?: "Pengguna"

        _uiState.value = HomeUiState(
            namaUser = namaUser,
            kategoriList = listOf(
                KategoriLaporan("kebakaran", "Kebakaran", Icons.Outlined.LocalFireDepartment),
                KategoriLaporan("penyelamatan", "Penyelamatan", Icons.Outlined.HealthAndSafety),
                KategoriLaporan("hewan", "Hewan", Icons.Outlined.Pets),
                KategoriLaporan("pohon", "Pohon Tumbang", Icons.Outlined.Park),
                KategoriLaporan("lainnya", "Lainnya", Icons.Outlined.MoreHoriz)
            ),
            bannerList = listOf(
                BannerItem(
                    id = "tips",
                    judul = "Tips & Edukasi",
                    subjudul = "Cegah kebakaran di rumah",
                    icon = Icons.Outlined.Lightbulb,
                    isPrimary = true
                ),
                BannerItem(
                    id = "nomor",
                    judul = "Nomor Penting",
                    subjudul = "Hubungi nomor darurat ",
                    icon = Icons.Outlined.CallEnd,
                    isPrimary = false
                )
            ),
            pelaporanList = listOf(
                PelaporanItem(
                    id = "daftar",
                    judul = "Daftar Laporan",
                    subjudul = "Pantau riwayat & status",
                    icon = Icons.Outlined.Assignment
                )
            )
        )
    }

    fun onLogout() {
        authRepository.signOut()
    }

    fun onKategoriClick(id: String) {
        // TODO: navigasi ke form laporan sesuai kategori
    }

    fun onBannerClick(id: String) {
        // TODO: navigasi ke halaman tips / edukasi
    }

    fun onPelaporanClick(id: String) {
        // TODO: navigasi ke daftar laporan
    }

    fun onLaporClick() {
        // Alihkan langsung ke fungsi onKategoriClick dengan ID default
        onKategoriClick("lainnya")
    }
}