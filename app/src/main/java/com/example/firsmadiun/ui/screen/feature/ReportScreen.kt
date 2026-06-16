package com.example.firsmadiun.ui.screen.feature

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.firsmadiun.data.models.LaporanForm
import com.example.firsmadiun.data.models.LaporanResult
import com.example.firsmadiun.data.models.LokasiPeta
import com.example.firsmadiun.data.models.daftarKategori
import com.example.firsmadiun.ui.components.*
import com.example.firsmadiun.ui.components.button.PrimaryButton
import com.example.firsmadiun.ui.components.img.ImageInputField
import com.example.firsmadiun.ui.components.section.KategoriChipSection
import com.example.firsmadiun.ui.components.text.TextArea
import com.example.firsmadiun.ui.components.text.TextField
import com.example.firsmadiun.ui.theme.*
import com.example.firsmadiun.viewModel.feature.LaporanUiState
import com.example.firsmadiun.viewModel.feature.ReportViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

/**
 * Screen halaman Lapor Kejadian DAMKAR.
 *
 * @param onBack Callback tombol kembali
 * @param onLaporanTerkirim Callback setelah laporan berhasil dikirim
 * @param selectedKategori Kategori yang dipilih dari halaman home (opsional)
 * @param viewModel ViewModel laporan
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReportScreen(
    onBack: () -> Unit = {},
    onLaporanTerkirim: (String) -> Unit = {},
    selectedKategori: String = "Kebakaran",
    viewModel: ReportViewModel = hiltViewModel()
) {
    // Handle permission map
    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        locationPermissionState.launchMultiplePermissionRequest()
    }

    // Set kategori awal dari navigasi
    LaunchedEffect(selectedKategori) {
        viewModel.onKategoriSelected(selectedKategori)
    }

    // Handle sukses
    LaunchedEffect(uiState.result) {
        when (val currentResult = uiState.result) {
            is LaporanResult.Success -> {
                Toast.makeText(
                    context,
                    "Laporan berhasil terkirim!",
                    Toast.LENGTH_LONG
                ).show()
                onBack()
            }

            is LaporanResult.Error -> {
                Toast.makeText(
                    context,
                    "Gagal mengirim laporan: $${currentResult.pesan}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {}
        }
    }

    LaporanContent(
        uiState = uiState,
        allPermissionsGranted = locationPermissionState.allPermissionsGranted,
        onTriggerPermissionRequest = { locationPermissionState.launchMultiplePermissionRequest() },
        onBack = onBack,
        onKategoriSelected = viewModel::onKategoriSelected,
        onFotoBuktiChange = viewModel::onFotoBuktiChange,
        onFotoErrorChange = {},
        onNamaPelapor = viewModel::onNamaPelapor,
        onNoTelepon = viewModel::onNoTelepon,
        onLokasi = viewModel::onLokasi,
        onDeskripsi = viewModel::onDeskripsi,
        onTogglePeta = viewModel::onTogglePeta,
        onLokasiDipilihDariPeta = viewModel::onLokasiDipilihDariPeta,
        onKirimLaporan = viewModel::onKirimLaporan,
        onDismissError = viewModel::resetResult
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporanContent(
    uiState: LaporanUiState,
    allPermissionsGranted: Boolean,
    onTriggerPermissionRequest: () -> Unit,
    onBack: () -> Unit,
    onKategoriSelected: (String) -> Unit,
    onFotoBuktiChange: (Uri?) -> Unit,
    onFotoErrorChange: (String) -> Unit,
    onNamaPelapor: (String) -> Unit,
    onNoTelepon: (String) -> Unit,
    onLokasi: (String) -> Unit,
    onDeskripsi: (String) -> Unit,
    onTogglePeta: () -> Unit,
    onLokasiDipilihDariPeta: (LokasiPeta) -> Unit,
    onKirimLaporan: () -> Unit,
    onDismissError: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isLoading = uiState.result is LaporanResult.Loading
    val localView = LocalView.current

    Scaffold(
        containerColor = Color(0xFFF4F6FA),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(DamkarBlue, Color(0xFF1E4DB7))
                        )
                    )
                    .padding(horizontal = 4.dp)
                    .padding(top = 48.dp, bottom = 20.dp)
            ) {
                // Tombol Kembali
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Kembali",
                        tint = White
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(start = 56.dp, end = 16.dp)
                        .align(Alignment.CenterStart)
                ) {
                    Text(
                        text = "Lapor Kejadian",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = White
                    )
                    Text(
                        text = "Isi data dengan lengkap dan benar.",
                        style = MaterialTheme.typography.bodySmall,
                        color = White.copy(alpha = 0.75f)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF4F6FA))
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {

                        // Chip Kategori
                        KategoriChipSection(
                            kategoriList = uiState.kategoriList,
                            selected = uiState.form.kategori,
                            onSelected = onKategoriSelected
                        )

                        HorizontalDivider(color = DividerColor, thickness = 1.dp)

                        // Dokumentasi Kejadian
                        ImageInputField(
                            label = "Foto Bukti Laporan",
                            value = uiState.form.fotoBukti,          // Berupa Uri?
                            onValueChange = onFotoBuktiChange,       // Callback ke ViewModel
                            isError = uiState.fotoError.isNotEmpty(),
                            errorMessage = uiState.fotoError
                        )

                        // Nama Pelapor
                        TextField(
                            value = uiState.form.namaPelapor,
                            onValueChange = onNamaPelapor,
                            label = "Nama pelapor",
                            placeholder = "Masukkan nama lengkap",
                            isError = uiState.namaError.isNotEmpty(),
                            errorMessage = uiState.namaError
                        )

                        // No. Telepon
                        TextField(
                            value = uiState.form.noTelepon,
                            onValueChange = onNoTelepon,
                            label = "No. telepon",
                            placeholder = "Contoh: 08123456789",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            isError = uiState.teleponError.isNotEmpty(),
                            errorMessage = uiState.teleponError
                        )

                        // Lokasi Kejadian (teks manual)
                        TextField(
                            value = uiState.form.lokasi,
                            onValueChange = onLokasi,
                            label = "Lokasi kejadian",
                            placeholder = "Pilih lokasi dibawah terlebih dahulu",
                            isError = uiState.lokasiError.isNotEmpty(),
                            errorMessage = uiState.lokasiError,
                            isEnabled = false
                        )

                        // Peta OpenStreetMap
                        if (allPermissionsGranted) {
                            @OptIn(ExperimentalComposeUiApi::class)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .pointerInteropFilter { motionEvent ->
                                        when (motionEvent.action) {
                                            android.view.MotionEvent.ACTION_DOWN,
                                            android.view.MotionEvent.ACTION_MOVE -> {
                                                localView.parent?.requestDisallowInterceptTouchEvent(true)
                                                true // <--- Ubah jadi true saat disentuh/digeser
                                            }
                                            android.view.MotionEvent.ACTION_UP,
                                            android.view.MotionEvent.ACTION_CANCEL -> {
                                                localView.parent?.requestDisallowInterceptTouchEvent(false)
                                                true // <--- Ubah jadi true saat dilepas
                                            }
                                            else -> false
                                        }
                                    }
                            ) {
                                GoogleMapPicker(
                                    isVisible = uiState.isPetaVisible,
                                    onLokasiDipilih = onLokasiDipilihDariPeta,
                                    onToggle = onTogglePeta,
                                    lokasiTerpilih = if (uiState.form.latitude != null)
                                        LokasiPeta(
                                            uiState.form.latitude!!,
                                            uiState.form.longitude!!,
                                            uiState.form.alamatPeta
                                        ) else null
                                )
                            }
                        } else {
                            // UI Peringatan jika Izin belum diberikan
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Aplikasi memerlukan izin lokasi untuk menampilkan peta kejadian.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    OutlinedButton(
                                        onClick = onTriggerPermissionRequest,
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.LocationOn,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Berikan Izin Lokasi",
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }
                                }
                            }
                        }

                        // Deskripsi
                        TextArea(
                            value = uiState.form.deskripsi,
                            onValueChange = onDeskripsi,
                            label = "Deskripsi",
                            placeholder = "Ceritakan kondisi kejadian secara singkat...",
                            isError = uiState.deskripsiError.isNotEmpty(),
                            errorMessage = uiState.deskripsiError
                        )

                        // Error umum
                        AnimatedVisibility(
                            visible = uiState.result is LaporanResult.Error,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            val msg = (uiState.result as? LaporanResult.Error)?.pesan ?: ""
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        EmergencyRed.copy(alpha = 0.08f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Text(text = msg, color = EmergencyRed,
                                    style = MaterialTheme.typography.bodyMedium)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Tombol Kirim
                        PrimaryButton(
                            text = "Kirim Laporan",
                            onClick = onKirimLaporan,
                            isLoading = isLoading,
                            enabled = !isLoading
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// ============================================================
// PREVIEW
// ============================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LaporanScreenPreview() {
    DamkarTheme {
        LaporanContent(
            uiState = LaporanUiState(
                kategoriList = daftarKategori,
                form = LaporanForm(kategori = "Kebakaran")
            ),
            allPermissionsGranted = true,
            onTriggerPermissionRequest = {},
            onBack = {},
            onKategoriSelected = {},
            onFotoBuktiChange = {},
            onFotoErrorChange = {},
            onNamaPelapor = {},
            onNoTelepon = {},
            onLokasi = {},
            onDeskripsi = {},
            onTogglePeta = {},
            onLokasiDipilihDariPeta = {},
            onKirimLaporan = {},
            onDismissError = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Laporan - Peta Terbuka")
@Composable
fun LaporanScreenPetaPreview() {
    DamkarTheme {
        LaporanContent(
            uiState = LaporanUiState(
                kategoriList = daftarKategori,
                form = LaporanForm(kategori = "Kebakaran"),
                isPetaVisible = true
            ),
            allPermissionsGranted = true,
            onTriggerPermissionRequest = {},
            onBack = {},
            onKategoriSelected = {},
            onFotoBuktiChange = {},
            onFotoErrorChange = {},
            onNamaPelapor = {},
            onNoTelepon = {},
            onLokasi = {},
            onDeskripsi = {},
            onTogglePeta = {},
            onLokasiDipilihDariPeta = {},
            onKirimLaporan = {},
            onDismissError = {}
        )
    }
}