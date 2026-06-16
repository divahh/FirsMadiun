package com.example.firsmadiun.ui.screen.feature

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.firsmadiun.data.models.LaporanModel
import com.example.firsmadiun.data.models.StatusLaporan
import com.example.firsmadiun.ui.components.StatusBadge
import com.example.firsmadiun.ui.theme.*
import com.example.firsmadiun.viewModel.feature.ReportDetailUiState
import com.example.firsmadiun.viewModel.feature.ReportDetailViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ReportDetailScreen(
    laporanId: String,
    onBack: () -> Unit,
    viewModel: ReportDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(laporanId) {
        viewModel.loadLaporan(laporanId)
    }

    ReportDetailContent(
        uiState = uiState,
        onBack = onBack,
        onUbahStatus = viewModel::onUbahStatus
    )
}

@Composable
fun ReportDetailContent(
    uiState: ReportDetailUiState,
    onBack: () -> Unit,
    onUbahStatus: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
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
                        text = "Detail Laporan",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = White
                    )
                    Text(
                        text = "Informasi lengkap kejadian",
                        style = MaterialTheme.typography.bodySmall,
                        color = White.copy(alpha = 0.75f)
                    )
                }
            }
        },
        bottomBar = {
            if (uiState.isAdmin) {
                // Tombol Hubungi Pelapor di bawah untuk admin
                uiState.laporan?.let { laporan ->
                    if (laporan.noTelepon.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF4F6FA))
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:${laporan.noTelepon}")
                                    }
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DamkarBlue
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Phone,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Hubungi Pelapor",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }
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
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        color = DamkarBlue,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.error.isNotEmpty() -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = null,
                            tint = EmergencyRed,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Gagal memuat laporan",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Text(
                            text = uiState.error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }

                uiState.laporan != null -> {
                    val laporan = uiState.laporan

                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { 30 }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // === Card 1: Ringkasan + Status Timeline ===
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // Kategori + Status
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = laporan.kategori.uppercase(),
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = 1.sp
                                                ),
                                                color = DamkarBlue
                                            )

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Text(
                                                text = laporan.namaPelapor,
                                                style = MaterialTheme.typography.titleLarge.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = TextPrimary
                                            )
                                        }
                                        StatusBadge(status = laporan.status)
                                    }

                                    HorizontalDivider(color = DividerColor)

                                    // Timeline status
                                    StatusTimeline(status = laporan.status)
                                }
                            }

                            // === Card 2: Info Detail ===
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFF8FAFC)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(0.dp)
                                ) {
                                    InfoRow(
                                        icon = Icons.Outlined.Person,
                                        label = "Nama Pelapor",
                                        value = laporan.namaPelapor
                                    )
                                    HorizontalDivider(
                                        color = DividerColor,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                    InfoRow(
                                        icon = Icons.Outlined.Phone,
                                        label = "No. Telepon",
                                        value = laporan.noTelepon.ifBlank { "-" }
                                    )
                                    HorizontalDivider(
                                        color = DividerColor,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                    InfoRow(
                                        icon = Icons.Outlined.LocationOn,
                                        label = "Lokasi Kejadian",
                                        value = laporan.lokasi.ifBlank {
                                            laporan.alamatPeta.ifBlank { "-" }
                                        }
                                    )
                                    HorizontalDivider(
                                        color = DividerColor,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                    InfoRow(
                                        icon = Icons.Outlined.CalendarToday,
                                        label = "Waktu Laporan",
                                        value = laporan.createdAt?.toFormattedLong() ?: "-"
                                    )
                                }
                            }

                            // === Card 3: Deskripsi ===
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Outlined.Description,
                                            contentDescription = null,
                                            tint = DamkarBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Deskripsi Kejadian",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = TextPrimary
                                        )
                                    }
                                    Text(
                                        text = laporan.deskripsi.ifBlank { "-" },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary,
                                        lineHeight = 22.sp
                                    )
                                }
                            }

                            // === Card 4: Ubah Status (Admin) ===
                            if (uiState.isAdmin) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "Ubah Status",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.SemiBold
                                                ),
                                                color = TextPrimary
                                            )
                                        }

                                        Text(
                                            text = "Perbarui status penanganan laporan.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextSecondary,
                                            lineHeight = 22.sp
                                        )

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            listOf(
                                                StatusLaporan.MENUNGGU to "Menunggu",
                                                StatusLaporan.DIPROSES to "Diproses",
                                                StatusLaporan.SELESAI  to "Selesai"
                                            ).forEach { (value, label) ->
                                                val isSelected = laporan.status == value
                                                OutlinedButton(
                                                    onClick = { onUbahStatus(value) },
                                                    enabled = !uiState.isUpdatingStatus,
                                                    shape = RoundedCornerShape(8.dp),
                                                    colors = ButtonDefaults.outlinedButtonColors(
                                                        containerColor = if (isSelected) DamkarBlue else White,
                                                        contentColor = if (isSelected) White else TextPrimary
                                                    ),
                                                    border = androidx.compose.foundation.BorderStroke(
                                                        1.5.dp,
                                                        if (isSelected) DamkarBlue else InputBorder
                                                    ),
                                                    contentPadding = PaddingValues(
                                                        horizontal = 12.dp,
                                                        vertical = 8.dp
                                                    ),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    if (uiState.isUpdatingStatus && isSelected) {
                                                        CircularProgressIndicator(
                                                            color = White,
                                                            strokeWidth = 2.dp,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                    } else {
                                                        Text(
                                                            text = label,
                                                            style = MaterialTheme.typography.labelMedium.copy(
                                                                fontWeight = if (isSelected)
                                                                    FontWeight.SemiBold
                                                                else FontWeight.Normal
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Timeline 3 tahap status laporan.
 */
@Composable
private fun StatusTimeline(status: String) {
    val steps = listOf(
        Triple("Laporan Diterima", Icons.Outlined.Inbox, true),
        Triple(
            "Tim Bergerak",
            Icons.Outlined.LocalShipping,
            status == StatusLaporan.DIPROSES || status == StatusLaporan.SELESAI
        ),
        Triple(
            "Selesai Ditangani",
            Icons.Outlined.CheckCircle,
            status == StatusLaporan.SELESAI
        )
    )

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        steps.forEachIndexed { index, (label, icon, isActive) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (isActive) DamkarBlue else Color(0xFFF0F0F0)
                        )
                        .then(
                            if (!isActive) Modifier.border(
                                1.dp, DividerColor, CircleShape
                            ) else Modifier
                        )
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isActive) White else TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                    ),
                    color = if (isActive) DamkarBlue else TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            // Garis penghubung antara step (kecuali yang terakhir)
            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(0.3f)
                        .height(2.dp)
                        .align(Alignment.Top)
                        .padding(top = 21.dp)
                        .background(
                            if (steps[index + 1].third) DamkarBlue
                            else DividerColor
                        )
                )
            }
        }
    }
}

/**
 * Satu baris info — icon, label kecil, value tebal.
 */
@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(DamkarBlue.copy(alpha = 0.08f))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DamkarBlue,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = TextPrimary
            )
        }
    }
}

/** Format Timestamp ke "Jumat, 5 Juni 2026 pukul 21.10" */
private fun Timestamp.toFormattedLong(): String {
    return try {
        val sdf = SimpleDateFormat("EEEE, d MMMM yyyy 'pukul' HH.mm", Locale("id"))
        sdf.format(this.toDate())
    } catch (e: Exception) {
        ""
    }
}

// ============================================================
// PREVIEW
// ============================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReportDetailPreview() {
    DamkarTheme {
        ReportDetailContent(
            uiState = ReportDetailUiState(
                isLoading = false,
                laporan = LaporanModel(
                    id = "1",
                    kategori = "Kebakaran",
                    namaPelapor = "Budi Santoso",
                    noTelepon = "081234567890",
                    lokasi = "Jl. Merdeka No. 12, Jakarta Pusat",
                    deskripsi = "Kebakaran kecil di dapur rumah warga akibat tabung gas bocor. Api sudah mulai membesar dan menyebar ke bagian ruang tamu.",
                    status = StatusLaporan.SELESAI
                )
            ),
            onBack = {},
            onUbahStatus = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Status Diproses")
@Composable
fun ReportDetailDiprosesPreview() {
    DamkarTheme {
        ReportDetailContent(
            uiState = ReportDetailUiState(
                isLoading = false,
                laporan = LaporanModel(
                    id = "2",
                    kategori = "Penyelamatan",
                    namaPelapor = "Ahmad Fauzi",
                    noTelepon = "08567890123",
                    lokasi = "Kompleks Permata Indah Blok C, Bekasi",
                    deskripsi = "Seseorang terjebak di dalam sumur sedalam 10 meter saat membersihkan saluran air.",
                    status = StatusLaporan.DIPROSES
                )
            ),
            onBack = {},
            onUbahStatus = {}
        )
    }
}
