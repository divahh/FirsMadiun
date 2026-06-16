package com.example.firsmadiun.ui.components.text

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.firsmadiun.ui.theme.DamkarBlue
import com.example.firsmadiun.ui.theme.EmergencyRed
import com.example.firsmadiun.ui.theme.TextSecondary
import androidx.core.net.toUri

/**
 * Komponen teks darurat reusable.
 * Menampilkan "Darurat? Hubungi [number]"
 *
 * @param emergencyNumber Nomor darurat yang ditampilkan (default: "113")
 * @param modifier Modifier tambahan
 */
@Composable
fun TextEmergencyCall(
    emergencyNumber: String = "113",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clickable{
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = "tel:$emergencyNumber".toUri()
            }
            context.startActivity(intent)
        }
    ) {
        Text(
            text = "Darurat? Hubungi",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = emergencyNumber,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = EmergencyRed
        )
    }
}