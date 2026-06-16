package com.example.firsmadiun.ui.components.section

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.firsmadiun.data.models.KategoriLaporan
import com.example.firsmadiun.ui.theme.*

/**
 * Komponen section Kategori Laporan reusable.
 * Menampilkan grid 3 kolom dari daftar kategori.
 *
 * @param kategoriList Daftar kategori
 * @param onKategoriClick Callback saat kategori diklik
 * @param onLihatSemua Callback tombol "Lihat semua"
 * @param modifier Modifier tambahan
 */
@Composable
fun KategoriLaporanSection(
    kategoriList: List<KategoriLaporan>,
    onKategoriClick: (String) -> Unit,
    onLihatSemua: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Kategori Laporan",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onLihatSemua) {
                Text(
                    text = "Lihat semua",
                    style = MaterialTheme.typography.labelLarge,
                    color = DamkarBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Grid 3 kolom
        val rows = kategoriList.chunked(3)
        rows.forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { kategori ->
                    KategoriItem(
                        kategori = kategori,
                        onClick = { onKategoriClick(kategori.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Isi kolom kosong jika baris tidak penuh
                repeat(3 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

/**
 * Item kategori single — icon + label.
 */
@Composable
fun KategoriItem(
    kategori: KategoriLaporan,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = DamkarBlue.copy(alpha = 0.06f)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = White),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(DamkarBlue.copy(alpha = 0.08f))
            ) {
                Icon(
                    imageVector = kategori.icon,
                    contentDescription = kategori.nama,
                    tint = DamkarBlue,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = kategori.nama,
                style = MaterialTheme.typography.labelMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}