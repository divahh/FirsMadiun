package com.example.firsmadiun.ui.screen.feature

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.firsmadiun.data.models.TipsItem
import com.example.firsmadiun.data.models.TipsKategori
import com.example.firsmadiun.data.models.TipsType
import com.example.firsmadiun.ui.theme.*
import com.example.firsmadiun.viewModel.feature.TipsUiState
import com.example.firsmadiun.viewModel.feature.TipsViewModel

/**
 * Screen Tips & Edukasi — accordion kategori dari Firestore.
 * Dipakai untuk TipsType.TIPSEDU.
 */
@Composable
fun TipsScreen(
    type: TipsType = TipsType.TIPSEDU,
    onBack: () -> Unit = {},
    viewModel: TipsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(type) {
        viewModel.load(type)
    }

    TipsContent(
        uiState = uiState,
        onBack = onBack,
        onToggleKategori = viewModel::onToggleKategori
    )
}

@Composable
fun TipsContent(
    uiState: TipsUiState,
    onBack: () -> Unit,
    onToggleKategori: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    // Header oranye untuk tips&edu
    val headerColors = listOf(Color(0xFFE53E3E), Color(0xFFC0392B))
    val accentColor = EmergencyRed

    Scaffold(
        containerColor = Color(0xFFF4F6FA),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(headerColors))
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
                        text = uiState.type.judul,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = White
                    )
                    Text(
                        text = "Pilih kategori untuk melihat tipsnya.",
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
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        color = accentColor,
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
                            text = "Gagal memuat data",
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

                uiState.kategoriList.isEmpty() -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Inbox,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Belum ada konten",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        uiState.kategoriList.forEachIndexed { index, kategori ->
                            AnimatedVisibility(
                                visible = visible,
                                enter = fadeIn(tween(400, delayMillis = index * 80)) +
                                        slideInVertically(tween(400, delayMillis = index * 80)) { 20 }
                            ) {
                                TipsKategoriCard(
                                    kategori = kategori,
                                    isExpanded = uiState.expandedId == kategori.id,
                                    accentColor = accentColor,
                                    onToggle = { onToggleKategori(kategori.id) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TipsKategoriCard(
    kategori: TipsKategori,
    isExpanded: Boolean,
    accentColor: Color,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(16.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = kategori.icon.toImageVector(),
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = kategori.judul,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimary
                    )
                    Text(
                        text = kategori.subjudul,
                        style = MaterialTheme.typography.bodySmall,
                        color = accentColor.copy(alpha = 0.85f)
                    )
                }
                Icon(
                    imageVector = if (isExpanded)
                        Icons.Outlined.KeyboardArrowUp
                    else
                        Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(tween(300)) + fadeIn(tween(300)),
                exit = shrinkVertically(tween(200)) + fadeOut(tween(200))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(color = DividerColor)
                    // pakai field "item" sesuai model kamu
                    kategori.item.forEachIndexed { index, item ->
                        TipsItemRow(item = item, accentColor = accentColor)
                        if (index < kategori.item.size - 1) {
                            HorizontalDivider(
                                color = DividerColor,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TipsItemRow(
    item: TipsItem,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.08f))
        ) {
            Icon(
                imageVector = Icons.Outlined.LocalFireDepartment,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.judul,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = TextPrimary
            )
            if (item.deskripsi.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.deskripsi,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

fun String.toImageVector(): ImageVector {
    return when (this) {
        "fire_shield"  -> Icons.Outlined.Shield
        "fire"         -> Icons.Outlined.LocalFireDepartment
        "rescue"       -> Icons.Outlined.HealthAndSafety
        "animal"       -> Icons.Outlined.Pets
        "tree"         -> Icons.Outlined.Park
        "education"    -> Icons.Outlined.School
        "warning"      -> Icons.Outlined.Warning
        "medical"      -> Icons.Outlined.MedicalServices
        "run"          -> Icons.Outlined.DirectionsRun
        "phone"        -> Icons.Outlined.Phone
        "water"        -> Icons.Outlined.WaterDrop
        "electricity"  -> Icons.Outlined.ElectricBolt
        else           -> Icons.Outlined.Info
    }
}

// ============================================================
// PREVIEW
// ============================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TipsScreenPreview() {
    DamkarTheme {
        TipsContent(
            uiState = TipsUiState(
                isLoading = false,
                type = TipsType.TIPSEDU,
                expandedId = "1",
                kategoriList = listOf(
                    TipsKategori(
                        id = "1",
                        judul = "Pencegahan Kebakaran",
                        subjudul = "Langkah sederhana untuk mencegah api muncul.",
                        icon = "fire_shield",
                        item = listOf(
                            TipsItem("1", "Periksa instalasi listrik", "Cek kabel, stop kontak, dan beban listrik secara berkala."),
                            TipsItem("2", "Matikan kompor setelah memasak", "Pastikan regulator gas tertutup rapat.")
                        )
                    ),
                    TipsKategori(
                        id = "2",
                        judul = "Menyelamatkan Diri",
                        subjudul = "Yang harus dilakukan saat kebakaran terjadi.",
                        icon = "rescue",
                        item = emptyList()
                    )
                )
            ),
            onBack = {},
            onToggleKategori = {}
        )
    }
}