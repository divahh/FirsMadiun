package com.example.firsmadiun.ui.screen.feature

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.firsmadiun.data.models.LaporanModel
import com.example.firsmadiun.data.models.StatusLaporan
import com.example.firsmadiun.ui.components.button.PrimaryButton
import com.example.firsmadiun.ui.components.card.CardReportItem
import com.example.firsmadiun.ui.theme.*
import com.example.firsmadiun.viewModel.feature.ReportListUiState
import com.example.firsmadiun.viewModel.feature.ReportListViewModel

/**
 * Screen Daftar Laporan — terhubung ke Firestore secara realtime.
 */
@Composable
fun ReportListScreen(
    onBack: () -> Unit = {},
    onTambahLaporan: () -> Unit = {},
    onItemClick: (String) -> Unit = {},
    viewModel: ReportListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ReportListContent(
        uiState = uiState,
        onBack = onBack,
        onTambahLaporan = onTambahLaporan,
        onItemClick = onItemClick
    )
}

@Composable
fun ReportListContent(
    uiState: ReportListUiState,
    onBack: () -> Unit,
    onTambahLaporan: () -> Unit,
    onItemClick: (String) -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Scaffold(
        containerColor = Color(0xFFF4F6FA),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(DamkarBlue, Color(0xFF1E4DB7)))
                    )
                    .padding(horizontal = 4.dp)
                    .padding(top = 48.dp, bottom = 20.dp)
            ) {
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
                        text = "Daftar Laporan",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = White
                    )
                    Text(
                        text = "${uiState.laporanList.size} laporan tercatat",
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
            when {
                // Loading
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        color = DamkarBlue,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Error
                uiState.error.isNotEmpty() -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "Gagal memuat laporan",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiState.error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }

                // Empty state
                uiState.laporanList.isEmpty() -> {
                    ReportListEmptyState(
                        onTambahLaporan = onTambahLaporan,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }

                // List
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 16.dp,
                            bottom = 100.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = uiState.laporanList,
                            key = { it.id }
                        ) { laporan ->
                            AnimatedVisibility(
                                visible = visible,
                                enter = fadeIn(tween(400)) +
                                        slideInVertically(tween(400)) { 20 }
                            ) {
                                CardReportItem(
                                    laporan = laporan,
                                    onClick = { onItemClick(laporan.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Empty state card ketika belum ada laporan.
 */
@Composable
private fun ReportListEmptyState(
    onTambahLaporan: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Inbox,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(56.dp)
            )
            Text(
                text = "Belum ada laporan",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = TextPrimary
            )
            Text(
                text = "Mulai dengan menambahkan laporan baru.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            PrimaryButton(
                text = "Tambah Laporan",
                onClick = onTambahLaporan,
                modifier = Modifier.fillMaxWidth(0.7f)
            )
        }
    }
}

// ============================================================
// PREVIEW
// ============================================================

@Preview(showBackground = true, showSystemUi = true, name = "Empty State")
@Composable
fun ReportListEmptyPreview() {
    DamkarTheme {
        ReportListContent(
            uiState = ReportListUiState(isLoading = false),
            onBack = {},
            onTambahLaporan = {},
            onItemClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "With Data")
@Composable
fun ReportListDataPreview() {
    DamkarTheme {
        ReportListContent(
            uiState = ReportListUiState(
                isLoading = false,
                laporanList = listOf(
                    LaporanModel(
                        id = "1",
                        kategori = "Kebakaran",
                        namaPelapor = "Budi Santoso",
                        deskripsi = "Kebakaran kecil di dapur rumah warga akibat tabung gas bocor.",
                        lokasi = "Jl. Merdeka No. 12, Jakarta Pusat",
                        status = StatusLaporan.SELESAI
                    ),
                    LaporanModel(
                        id = "2",
                        kategori = "Penyelamatan",
                        namaPelapor = "Ahmad Fauzi",
                        deskripsi = "Seseorang terjebak di dalam sumur sedalam 10 meter.",
                        lokasi = "Kompleks Permata Indah Blok C, Bekasi",
                        status = StatusLaporan.DIPROSES
                    ),
                    LaporanModel(
                        id = "3",
                        kategori = "Hewan",
                        namaPelapor = "Rina Wati",
                        deskripsi = "Kucing terjebak di atap gedung 3 lantai.",
                        lokasi = "Jl. Gatot Subroto No. 8",
                        status = StatusLaporan.MENUNGGU
                    )
                )
            ),
            onBack = {},
            onTambahLaporan = {},
            onItemClick = {}
        )
    }
}