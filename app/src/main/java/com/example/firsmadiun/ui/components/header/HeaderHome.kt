package com.example.firsmadiun.ui.components.header

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firsmadiun.ui.components.img.Logo
import com.example.firsmadiun.ui.theme.*

/**
 * Komponen header halaman Home.
 * Menampilkan avatar, salam, nama user, tagline, dan tombol logout.
 *
 * @param namaUser Nama atau email user yang login
 * @param onLogout Aksi saat tombol logout ditekan
 * @param modifier Modifier tambahan
 */
@Composable
fun HeaderHome(
    namaUser: String,
    onLogout: () -> Unit,
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DamkarBlue,
                        Color(0xFF1E4DB7)
                    )
                )
            )
            .padding(horizontal = 20.dp)
            .padding(top = 48.dp, bottom = 16.dp)
    ) {
        Column {
            // Row: Avatar + Nama + Logout
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar Lingkaran
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(White.copy(alpha = 0.2f))
                        .clickable { onProfileClick() }
                ) {
                    Logo(size = 28.dp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Nama User
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SELAMAT DATANG",
                        style = MaterialTheme.typography.labelMedium.copy(
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = namaUser,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = White
                    )
                }

                // Tombol Logout
                IconButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(White.copy(alpha = 0.12f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Logout,
                        contentDescription = "Logout",
                        tint = White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}