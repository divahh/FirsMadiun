package com.example.firsmadiun.viewModel.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firsmadiun.data.models.NoPenting
import com.example.firsmadiun.data.models.TipsKategori
import com.example.firsmadiun.data.models.TipsType
import com.example.firsmadiun.data.repository.BannerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TipsUiState(
    val kategoriList: List<TipsKategori> = emptyList(),
    val expandedId: String? = null,   // ID kategori yang sedang dibuka accordion-nya
    val isLoading: Boolean = true,
    val error: String = "",
    val type: TipsType = TipsType.TIPSEDU
)

data class NomorUiState(
    val nomorList: List<NoPenting> = emptyList(),
    val isLoading: Boolean = true,
    val error: String = "",
    val type: TipsType = TipsType.NOPENTING
)

class TipsViewModel: ViewModel() {
    private val bannerRepository = BannerRepository()

    private val _uiState = MutableStateFlow(TipsUiState())
    val uiState: StateFlow<TipsUiState> = _uiState.asStateFlow()

    fun load(type: TipsType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = "", type = type) }

            bannerRepository.getKategoriDenganItems(type).fold(
                onSuccess = { list ->
                    _uiState.update {
                        it.copy(
                            kategoriList = list,
                            isLoading = false,
                            // akan expand untuk kategori pertama
                            expandedId = list.firstOrNull()?.id
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Gagal memuat data"
                        )
                    }
                }
            )
        }
    }

    // Toggle buka tutup akordion kategori
    fun onToggleKategori(id: String) {
        _uiState.update {
            it.copy(expandedId = if (it.expandedId == id) null else id)
        }
    }
}

class NomorViewModel: ViewModel() {
    private val bannerRepository = BannerRepository()

    private val _uiState = MutableStateFlow(NomorUiState())
    val uiState: StateFlow<NomorUiState> = _uiState.asStateFlow()

    fun load(type: TipsType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = "", type = type) }

            bannerRepository.getNomor(type).fold(
                onSuccess = { list ->
                    _uiState.update {
                        it.copy(
                            nomorList = list,
                            isLoading = false
                        )
                    }
                },
                onFailure = { e->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Gagal memuat data"
                        )
                    }
                }
            )
        }
    }
}