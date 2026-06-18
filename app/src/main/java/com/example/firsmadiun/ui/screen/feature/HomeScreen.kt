package com.example.firsmadiun.ui.screen.feature

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Park
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.firsmadiun.data.models.BannerItem
import com.example.firsmadiun.data.models.KategoriLaporan
import com.example.firsmadiun.data.models.PelaporanItem
import com.example.firsmadiun.ui.components.BannerRow
import com.example.firsmadiun.ui.components.card.CardEmergencyCall
import com.example.firsmadiun.ui.components.header.HeaderHome
import com.example.firsmadiun.ui.components.section.KategoriLaporanSection
import com.example.firsmadiun.ui.components.section.PelaporanSection
import com.example.firsmadiun.ui.theme.*
import com.example.firsmadiun.viewModel.feature.HomeUiState
import com.example.firsmadiun.viewModel.feature.HomeViewModel

@Composable
fun HomeScreen(
    onLogout: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(),
    onKategoriClick: (String) -> Unit = {},
    onRiwayatClick: () -> Unit = {},
    onBannerClick: (String) -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeContent(
        uiState = uiState,
        onLogout = {
            viewModel.onLogout()
            onLogout()
        },
        onKategoriClick = { id ->
            viewModel.onKategoriClick(id)
            onKategoriClick(id)
        },
        onBannerClick = onBannerClick,
        onPelaporanClick = { id ->
            viewModel.onPelaporanClick(id)
            if (id == "daftar") { // Memastikan ID sesuai dengan data di ViewModel ("daftar")
                onRiwayatClick()
            }
        },
        onLaporClick = {
            viewModel.onLaporClick()
            onKategoriClick("lainnya") // Memicu navigasi Compose dengan ID default
        },
        onProfileClick = onProfileClick
    )
}

@Composable
fun HomeContent(
    uiState: HomeUiState,
    onLogout: () -> Unit,
    onKategoriClick: (String) -> Unit,
    onBannerClick: (String) -> Unit,
    onPelaporanClick: (String) -> Unit,
    onLaporClick: () -> Unit,
    onProfileClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    // Scaffold memisahkan header sticky, konten scroll, dan FAB
    // secara bersih tanpa overlap
    Scaffold(
        containerColor = Color(0xFFF4F6FA),

        // FAB otomatis menghindari navigation bar via WindowInsets
        floatingActionButton = {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500, delayMillis = 500)) +
                        scaleIn(tween(400, delayMillis = 500))
            ) {
                FloatingActionButton(
                    onClick = onLaporClick,
                    shape = CircleShape,
                    containerColor = DamkarBlue,
                    contentColor = White,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 12.dp
                    ),
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Buat Laporan Baru",
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        },

        // Header sticky — tidak ikut scroll
        topBar = {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500))
            ) {
                HeaderHome(
                    namaUser = uiState.namaUser,
                    onLogout = onLogout,
                    onProfileClick = onProfileClick
                )
            }
        }
    ) { innerPadding ->

        // Konten scrollable — pakai innerPadding dari Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500, delayMillis = 50))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E4DB7))
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 24.dp)
                ) {
                    // Tagline
                    Column {
                        Text(
                            text = "Siap menerima\nlaporan darurat.",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                lineHeight = 34.sp
                            ),
                            color = White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Cepat. Sigap. Selamat.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.5.sp
                            ),
                            color = White.copy(alpha = 0.75f)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Kartu Panggilan Darurat
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(500, delayMillis = 100)) +
                            slideInVertically(tween(500, delayMillis = 100)) { 30 }
                ) {
                    CardEmergencyCall(number = "113")
                }

                // Kategori Laporan
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(500, delayMillis = 200)) +
                            slideInVertically(tween(500, delayMillis = 200)) { 30 }
                ) {
                    KategoriLaporanSection(
                        kategoriList = uiState.kategoriList,
                        onKategoriClick = onKategoriClick,
                        onLihatSemua = {}
                    )
                }

                // Banner Tips & Edukasi
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(500, delayMillis = 300)) +
                            slideInVertically(tween(500, delayMillis = 300)) { 30 }
                ) {
                    BannerRow(
                        bannerList = uiState.bannerList,
                        onBannerClick = onBannerClick
                    )
                }

                // Pelaporan
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(500, delayMillis = 400)) +
                            slideInVertically(tween(500, delayMillis = 400)) { 30 }
                ) {
                    PelaporanSection(
                        pelaporanList = uiState.pelaporanList,
                        onItemClick = onPelaporanClick
                    )
                }

                // Jarak bawah agar konten tidak tertutup FAB
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}

// ============================================================
// PREVIEW
// ============================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    DamkarTheme {
        val fakeState = HomeUiState(
            namaUser = "sss@gmail",
            kategoriList = listOf(
                KategoriLaporan("1", "Kebakaran", Icons.Outlined.LocalFireDepartment),
                KategoriLaporan("2", "Penyelamatan", Icons.Outlined.HealthAndSafety),
                KategoriLaporan("3", "Hewan", Icons.Outlined.Pets),
                KategoriLaporan("4", "Pohon Tumbang", Icons.Outlined.Park),
                KategoriLaporan("5", "Lainnya", Icons.Outlined.MoreHoriz),
            ),
            bannerList = listOf(
                BannerItem("tips", "Tips & Edukasi", "Cegah kebakaran di rumah", Icons.Outlined.Lightbulb, true),
                BannerItem("no_penting", "No. Penting", "Hubungi nomor darurat", Icons.Outlined.Contacts, false)
            ),
            pelaporanList = listOf(
                PelaporanItem("daftar", "Daftar Laporan", "Pantau riwayat & status", Icons.AutoMirrored.Outlined.Assignment)
            )
        )
        HomeContent(
            uiState = fakeState,
            onLogout = {},
            onKategoriClick = {},
            onBannerClick = {},
            onPelaporanClick = {},
            onLaporClick = {}
        )
    }
}