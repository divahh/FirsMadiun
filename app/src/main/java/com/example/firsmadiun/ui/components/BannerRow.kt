package com.example.firsmadiun.ui.components

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.firsmadiun.data.models.BannerItem
import com.example.firsmadiun.ui.theme.*

private val RedGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFE53E3E), Color(0xFFC0392B))
)
private val DarkGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E))
)

/**
 * Komponen baris banner Tips & Edukasi + no.penting reusable.
 *
 * @param bannerList Daftar banner (maks 2 ditampilkan berdampingan)
 * @param onBannerClick Callback saat banner diklik
 * @param modifier Modifier tambahan
 */
@Composable
fun BannerRow(
    bannerList: List<BannerItem>,
    onBannerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        bannerList.forEach { banner ->
            BannerCard(
                item = banner,
                onClick = { onBannerClick(banner.id) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Single banner card — merah (Tips) atau gelap (No. Penting).
 */
@Composable
fun BannerCard(
    item: BannerItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradient = if (item.isPrimary) RedGradient else DarkGradient

    Box(
        modifier = modifier
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(brush = gradient)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            // Icon
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(White.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Judul + Subjudul
            Column {
                Text(
                    text = item.judul,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = item.subjudul,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = White.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}