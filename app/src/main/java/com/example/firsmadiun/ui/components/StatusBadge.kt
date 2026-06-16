package com.example.firsmadiun.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.firsmadiun.data.models.StatusLaporan

/**
 * Badge status laporan — warna berbeda tiap status.
 */
@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status) {
        StatusLaporan.MENUNGGU -> Triple(
            Color(0xFFFFF3CD),
            Color(0xFF856404),
            "Menunggu"
        )
        StatusLaporan.DIPROSES -> Triple(
            Color(0xFFCFE2FF),
            Color(0xFF084298),
            "Diproses"
        )
        StatusLaporan.SELESAI -> Triple(
            Color(0xFFD1E7DD),
            Color(0xFF0A3622),
            "Selesai"
        )
        else -> Triple(Color(0xFFF0F0F0), Color(0xFF666666), status)
    }

    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
        color = textColor,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    )
}