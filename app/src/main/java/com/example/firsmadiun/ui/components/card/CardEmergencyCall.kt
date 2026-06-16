package com.example.firsmadiun.ui.components.card

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firsmadiun.ui.theme.*
import androidx.core.net.toUri

/**
 * Komponen card panggilan darurat reusable.
 * Intent ACTION_DIAL ditangani langsung di dalam komponen ini
 * sehingga tidak perlu diteruskan dari luar.
 *
 * @param number Nomor darurat (default: "113")
 * @param modifier Modifier tambahan
 */
@Composable
fun CardEmergencyCall(
    number: String = "113",
    modifier: Modifier = Modifier
) {
    // context diambil di sini, di dalam scope @Composable
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = DamkarBlue.copy(alpha = 0.1f),
                spotColor = DamkarBlue.copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // Icon lingkaran
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(DamkarBlue.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Outlined.Call,
                    contentDescription = null,
                    tint = DamkarBlue,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Label + Nomor
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Panggilan Darurat",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Text(
                    text = number,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp
                    ),
                    color = TextPrimary
                )
            }

            // Tombol Telepon — intent langsung di sini
            OutlinedButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = "tel:$number".toUri()
                    }
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.5.dp, DamkarBlue),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DamkarBlue
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Tekan untuk telepon",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}